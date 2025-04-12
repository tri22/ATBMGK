package Model.SymmetryAlgorithm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;

public class BlowFish implements SymmetryAlgorithm {

    private SecretKey secretKey;
    private Cipher cipher;

    @Override
    public SecretKey genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(128); // Blowfish key size is 128 bits by default
        this.secretKey = keyGenerator.generateKey();
        return this.secretKey;
    }

    @Override
    public SecretKey genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(keySize); // You can specify key size (e.g., 128, 192, or 256)
        this.secretKey = keyGenerator.generateKey();
        return this.secretKey;
    }

    @Override
    public void loadKey(SecretKey key) {
        this.secretKey = key;
    }

    
    public byte[] encrypt(String text) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
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
        cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted);
    }

    @Override
    public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(srcf);
        FileOutputStream fos = new FileOutputStream(desf);
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
        return true;
    }

    @Override
    public boolean decryptFile(String srcf, String desf) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(srcf);
        FileOutputStream fos = new FileOutputStream(desf);
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
        return true;
    }
}
