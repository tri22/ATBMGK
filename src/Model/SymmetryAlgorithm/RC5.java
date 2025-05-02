package Model.SymmetryAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RC5 implements SymmetryAlgorithm {
    static {
        // Thêm provider Bouncy Castle
        Security.addProvider(new BouncyCastleProvider());
    }
	private SecretKey secretKey;
    private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/rc5.key";
    public String decrypt_path = "";
    public String encrypt_path = "";
    public String mode ="";
    public  String padding ="";

    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("RC5");
        keyGen.init(128); 
        secretKey = keyGen.generateKey();
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("RC5");
        keyGen.init(keySize);
        secretKey = keyGen.generateKey();
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
    
    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "RC5");
        this.secretKey = key;
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

	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] rawData = data.getBytes();
		return encrypt(rawData);
	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RC5");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RC5");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(data);
		return new String(decryptedBytes);
	}

    @Override
    public String encryptFile(String src) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
    	this.decrypt_path = generateFileName(src,"decrypt");
        this.encrypt_path = generateFileName(src,"encrypt");
    	Cipher cipher = Cipher.getInstance("RC5");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(encrypt_path)) {
            byte[] inputBytes = fis.readAllBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
        return encrypt_path;
    }

    @Override
    public String decryptFile(String encryptedFilePath) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("RC5");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(encryptedFilePath);
             FileOutputStream fos = new FileOutputStream(decrypt_path)) {
            byte[] inputBytes = fis.readAllBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
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
		this.secretKey = new SecretKeySpec(keyBytes, "RC5");
	}


    public static void main(String[] args) throws Exception {
        RC5 camellia = new RC5();
        System.out.println("Key generated: " + camellia.genkey(128)); // Example with 128 bits key

        String src = "C:\\Users\\Trung Tri\\Documents\\test.txt";
        String path=camellia.encryptFile(src);
        System.out.println("Encrypted file: " +path );
        System.out.println("Decrypted file: " + camellia.decryptFile(path));
    }
}
