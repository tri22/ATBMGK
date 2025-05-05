package Model.SymmetryAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class Camellia implements SymmetryAlgorithm {
    static {
        // Thêm provider Bouncy Castle
        Security.addProvider(new BouncyCastleProvider());
    }

    private SecretKey secretKey;
    private Cipher cipher;
    private static final String KEY_FOLDER       = "keys";
    private static final String KEY_PATH = KEY_FOLDER+"/camellia.txt";
    public String decrypt_path = "";
    public String encrypt_path = "";
    public String mode = "";  
    public String padding = "";  
    public IvParameterSpec ivSpec;
	  
    public Camellia() {
    	File keyDir = new File(KEY_FOLDER);
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
	}
    
	@Override
	public void generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16]; 
        random.nextBytes(iv);  
        this.ivSpec = new IvParameterSpec(iv);  
    }
	
    @Override
    public boolean genkey() throws NoSuchAlgorithmException, NoSuchProviderException {
       return genkey(128);
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Camellia", "BC");
        keyGenerator.init(keySize); // Specify key size (128, 192, or 256 bits)
        this.secretKey = keyGenerator.generateKey();
        generateIV();
        return saveKeyToFile();
    }

    @Override
    public void loadKey() {
        try {
            loadKeyFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveKeyToFile() {
        try {
            if (secretKey == null) return false;
            byte[] encoded = secretKey.getEncoded();
            Files.write(Paths.get(KEY_PATH), encoded);
            return true;
        } catch (IOException e) {
            e.printStackTrace(); // Hoặc log ra UI
            return false;
        }
    }

    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "Camellia");
        this.secretKey = key;
    }

    public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        byte[] rawData = data.getBytes();
        return encrypt(rawData);
    }

    public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("Camellia" + mode  + padding, "BC");
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		} else {
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		}
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        return decrypt(Base64.getDecoder().decode(data));
    }

    public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("Camellia" + mode + padding, "BC");
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
		} else {
		    cipher.init(Cipher.DECRYPT_MODE, secretKey);
		}
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes);
    }

    @Override
    public String encryptFile(String src) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        this.decrypt_path = generateFileName(src,"decrypt");
        this.encrypt_path = generateFileName(src,"encrypt");

        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(encrypt_path);
        cipher = Cipher.getInstance("Camellia" + mode +  padding, "BC");
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		} else {
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		}
        CipherInputStream cis = new CipherInputStream(fis, cipher);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        cis.close();
        fos.close();
        return encrypt_path;
    }

    @Override
    public String decryptFile(String encryptedFilePath) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(encryptedFilePath);
        FileOutputStream fos = new FileOutputStream(decrypt_path);
        cipher = Cipher.getInstance("Camellia" + mode + padding, "BC");
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
		} else {
		    cipher.init(Cipher.DECRYPT_MODE, secretKey);
		}
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            cos.write(buffer, 0, bytesRead);
        }
        cos.close();
        fis.close();
        fos.close();
        // Sau khi giải mã xong
	    Path outputPath = Paths.get(decrypt_path);
	    String mimeType = Files.probeContentType(outputPath);
	    
	    if (mimeType != null && mimeType.startsWith("text")) {
	        String content = Files.readString(outputPath, StandardCharsets.UTF_8);
	        return "Đường dẫn: " + outputPath.toAbsolutePath() + "\n" + content;
	    } else {
	        return "Đường dẫn: " + outputPath.toAbsolutePath() + "\n(File không phải dạng văn bản)";
	    }
    }

    private String generateFileName(String originalPath, String suffix) {
        int dotIndex = originalPath.lastIndexOf('.');
        if (dotIndex != -1) {
            return originalPath.substring(0, dotIndex) + "_" + suffix + originalPath.substring(dotIndex);
        } else {
            return originalPath + "_" + suffix;
        }
    }
    
	@Override
	public SecretKey getSecretKey() {
		// TODO Auto-generated method stub
		return this.secretKey;
	}
	
	@Override
	public void setSecretKey(byte[] keyBytes) {
		this.secretKey = new SecretKeySpec(keyBytes, "CAMELLIA");
	}
	
	@Override
	public void setMode(String mode) {
		this.mode = "/"+mode;
		
	}
	
	@Override
	public void setPadding(String padding) {
		this.padding ="/"+padding;
		
	}


    public static void main(String[] args) throws Exception {
        Camellia camellia = new Camellia();
        System.out.println("Key generated: " + camellia.genkey(128)); // Example with 128 bits key

        String src = "C:\\Users\\Trung Tri\\Documents\\test.txt";
        String path=camellia.encryptFile(src);
        System.out.println("Encrypted file: " +path );
        System.out.println("Decrypted file: " + camellia.decryptFile(path));
    }
}
