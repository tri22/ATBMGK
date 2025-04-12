package Model.SymmetryAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RC5 implements SymmetryAlgorithm {
    private SecretKey secretKey;

    @Override
    public SecretKey genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("RC5");
        keyGen.init(128); // RC5 thường dùng key 128-bit
        secretKey = keyGen.generateKey();
        return secretKey;
    }

    @Override
    public SecretKey genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("RC5");
        keyGen.init(keySize);
        secretKey = keyGen.generateKey();
        return secretKey;
    }

    @Override
    public void loadKey(SecretKey key) {
        this.secretKey = key;
    }

    
    public byte[] encrypt(String text) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("RC5");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return encrypted;
    }

    @Override
 	public String encryptBase64(String text) throws InvalidKeyException, NoSuchAlgorithmException,
 			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
 		return Base64.getEncoder().encodeToString(encrypt(text));
 	}

 	public String decrypt(String data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
 			IllegalBlockSizeException, BadPaddingException, Exception {
 		return decryptBase64(data.getBytes());
 	}
    
    
    @Override
    public String decryptBase64(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("RC5");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(data);
        return new String(decrypted);
    }

    @Override
    public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        Cipher cipher = Cipher.getInstance("RC5");
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
        Cipher cipher = Cipher.getInstance("RC5");
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
