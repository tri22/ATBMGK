package Model.SymmetryAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ThreeDES implements SymmetryAlgorithm {
    private SecretKey secretKey;
    private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/3des.key";


    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
        keyGen.init(168); // 3DES dùng 168-bit key
        secretKey = keyGen.generateKey();
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
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
        SecretKey key = new SecretKeySpec(encoded, "DESede");
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
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(data);
		return new String(decryptedBytes);
	}

    @Override
    public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(srcf);
             FileOutputStream fos = new FileOutputStream(desf)) {
            byte[] inputBytes = fis.readAllBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
        return true;
    }

    @Override
    public boolean decryptFile(String srcf, String desf) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(srcf);
             FileOutputStream fos = new FileOutputStream(desf)) {
            byte[] inputBytes = fis.readAllBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
        return true;
    }
}
