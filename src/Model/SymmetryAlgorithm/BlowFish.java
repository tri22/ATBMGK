package Model.SymmetryAlgorithm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;

public class BlowFish implements SymmetryAlgorithm {

    private SecretKey secretKey;
    private Cipher cipher;
    private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/blowfish.key";
    public String decrypt_path = "";
    public String encrypt_path = "";
    public String mode ="";
    public  String padding ="";
    

    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(128); // Blowfish key size is 128 bits by default
        this.secretKey = keyGenerator.generateKey();
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(keySize); // You can specify key size (e.g., 128, 192, or 256)
        this.secretKey = keyGenerator.generateKey();
        return saveKeyToFile();
    }

    @Override
    public void loadKey() {
    	try {
			loadKeyFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
        SecretKey key = new SecretKeySpec(encoded, "Blowfish");
        this.secretKey = key;
    }


    
	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] rawData = data.getBytes();
		return encrypt(rawData);
	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
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
        cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
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
        cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            cos.write(buffer, 0, bytesRead);
        }
        cos.close();
        fis.close();
        fos.close();
        return decrypt_path;
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
		this.secretKey = new SecretKeySpec(keyBytes, "BLOWFISH");
	}
    
    public static void main(String[] args) throws Exception {
    	BlowFish aes = new BlowFish();
    	System.out.println(aes.genkey(128));
    	String src ="C:\\Users\\Trung Tri\\Documents\\test.txt";
    	String path=aes.encryptFile(src);
        System.out.println("Encrypted file: " +path );
        System.out.println("Decrypted file: " + aes.decryptFile(path));
    }
}
