package Model.AsymmetryAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Model.SymmetryAlgorithm.AES;
import Model.SymmetryAlgorithm.BlowFish;
import Model.SymmetryAlgorithm.Camellia;
import Model.SymmetryAlgorithm.Cast_128;
import Model.SymmetryAlgorithm.Des;
import Model.SymmetryAlgorithm.RC5;
import Model.SymmetryAlgorithm.SymmetryAlgorithm;
import Model.SymmetryAlgorithm.ThreeDES;
import Model.SymmetryAlgorithm.Twofish;


public class RSA implements AsymmetryAlgorithm {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String PUBLIC_KEY_PATH = "src/Model/AsymmetryAlgorithm/keys/public.key";
    private static final String PRIVATE_KEY_PATH = "src/Model/AsymmetryAlgorithm/keys/private.key";
    private final Map<String, SymmetryAlgorithm> symmetrics = new HashMap<>();
    SymmetryAlgorithm symmetricsAlgo;
    String encryptedFilePath ="";
    String encryptedKeyPath ="";
    
    
    public RSA() {
        symmetrics.put("AES", new AES());
        symmetrics.put("BLOWFISH", new BlowFish());
        symmetrics.put("CAMELLIA", new Camellia());
        symmetrics.put("CAST_128", new Cast_128());
        symmetrics.put("DES", new Des());
        symmetrics.put("RC5", new RC5());
        symmetrics.put("3DES", new ThreeDES());
        symmetrics.put("TWOFISH", new Twofish());
    }
    
    public boolean genKey(int size) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            return saveKeyToFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean saveKeyToFile() throws IOException {
        if (publicKey == null || privateKey == null) return false;

        Files.write(Paths.get(PUBLIC_KEY_PATH), publicKey.getEncoded());
        Files.write(Paths.get(PRIVATE_KEY_PATH), privateKey.getEncoded());
        return true;
    }


    @Override
    public void loadKey() throws Exception {
        byte[] publicBytes = Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
        byte[] privateBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicBytes);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);

        publicKey = keyFactory.generatePublic(pubSpec);
        privateKey = keyFactory.generatePrivate(privSpec);
    }


    public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        return encrypt(data.getBytes());
    }

    public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        return decrypt(Base64.getDecoder().decode(data));
    }

    public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSA rsa = (RSA) o;
        return PUBLIC_KEY_PATH.equals(rsa.PUBLIC_KEY_PATH)
                && PRIVATE_KEY_PATH.equals(rsa.PRIVATE_KEY_PATH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH);
    }

	@Override
	public String encryptFile(String filePath, String algorithmName) throws Exception {
		algorithmName.trim().toUpperCase();
		if (symmetrics.containsKey(algorithmName)) {
			symmetricsAlgo = symmetrics.get(algorithmName);
		}

		// 3. Sinh khóa đối xứng và mã hóa dữ liệu file
		symmetricsAlgo.genkey(); // Hàm này bạn cần có trong mỗi class đối xứng
		this.encryptedFilePath = symmetricsAlgo.encryptFile(filePath);

		// 5. Lấy khóa đối xứng dạng byte
		byte[] symmetricKeyBytes = symmetricsAlgo.getSecretKey().getEncoded(); // cần hàm getSecretKey()

		// 6. Mã hóa khóa đối xứng bằng RSA
		Cipher rsaCipher = Cipher.getInstance("RSA");
		rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedSymmetricKey = rsaCipher.doFinal(symmetricKeyBytes);

		// 7. Lưu khóa mã hóa vào file: AES_encryptedKey.txt
		this.encryptedKeyPath = generateFileName(filePath, algorithmName + "_encryptedKey.txt");
		Files.write(Paths.get(encryptedKeyPath), encryptedSymmetricKey);

		return "Mã hóa hoàn tất:\n- " + encryptedFilePath + "\n- " + encryptedKeyPath;
	}

	@Override
	public String decryptFile(String algo) throws Exception {
	    algo = algo.trim().toUpperCase();

	    if (!symmetrics.containsKey(algo)) {
	        throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algo);
	    }

	    // 1. Lấy thuật toán đối xứng
	    symmetricsAlgo = symmetrics.get(algo);

	    // 2. Đọc khóa đối xứng đã mã hóa từ file
	    byte[] encryptedKeyBytes = Files.readAllBytes(Paths.get(encryptedKeyPath));

	    // 3. Giải mã khóa đối xứng bằng RSA private key
	    Cipher rsaCipher = Cipher.getInstance("RSA");
	    rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
	    byte[] decryptedSymmetricKeyBytes = rsaCipher.doFinal(encryptedKeyBytes);

	    // 4. Gán lại khóa cho thuật toán đối xứng
	    symmetricsAlgo.setSecretKey(decryptedSymmetricKeyBytes); // Bạn cần cài đặt phương thức này

	    // 5. Giải mã file
	    String decryptedFilePath = symmetricsAlgo.decryptFile(encryptedFilePath); // file được mã hóa trước đó

	    return "Giải mã hoàn tất:\n- " + decryptedFilePath;
	}

	
	private String generateFileName(String originalPath, String newname) {
	    Path original = Paths.get(originalPath);
	    String fileName = original.getFileName().toString();
	    int dotIndex = fileName.lastIndexOf('.');

	    String newFileName;
	    if (dotIndex != -1) {
	        newFileName = fileName.substring(0, dotIndex) + "_" + newname + fileName.substring(dotIndex);
	    } else {
	        newFileName = fileName + "_" + newname;
	    }

	    // Ghép với đường dẫn gốc
	    Path parentDir = original.getParent();
	    if (parentDir != null) {
	        return parentDir.resolve(newFileName).toString();
	    } else {
	        return newFileName; // Nếu không có thư mục cha (file nằm ở root)
	    }
	}

	 public static void main(String[] args) {
	        try {
	           
	            RSA rsa = new RSA();
	            rsa.genKey(512);
	       
	            // Test mã hóa và giải mã file
	            String filePath = "C:\\Users\\Trung Tri\\Documents\\test.txt"; 
	            String algorithmName = "AES"; // Thuật toán đối xứng để mã hóa

	            // Mã hóa file
	            String encryptFileResult = rsa.encryptFile(filePath, algorithmName);
	            System.out.println(encryptFileResult);

	            // Giải mã file
	            String decryptFileResult = rsa.decryptFile(algorithmName);
	            System.out.println(decryptFileResult);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
