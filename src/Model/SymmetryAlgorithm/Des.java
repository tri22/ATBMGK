package Model.SymmetryAlgorithm;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;

public class Des implements SymmetryAlgorithm {

    private SecretKey currentKey;

    @Override
    public SecretKey genkey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        generator.init(56);
        this.currentKey = generator.generateKey();
        return currentKey;
    }

    @Override
    public SecretKey genkey(int keySize) throws NoSuchAlgorithmException {
        // DES chỉ hỗ trợ key 56 bit nên tham số keySize bị bỏ qua
        return genkey();
    }

    @Override
    public void loadKey(SecretKey key) {
        this.currentKey = key;
    }

    @Override
    public String encryptBase64(String text) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }
    
    public byte[] encrypt(String text) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, currentKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return encryptedBytes;
    }

    @Override
    public String decryptBase64(byte[] data) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, currentKey);
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes);
    }
    
    public String decrypt(String data) throws Exception {
        return decryptBase64(data.getBytes());
    }

    @Override
    public boolean encryptFile(String srcf, String desf) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, currentKey);

        try (FileInputStream fis = new FileInputStream(new File(srcf));
             CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(new File(desf)), cipher)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
            }
        }
        return true;
    }

    @Override
    public boolean decryptFile(String srcf, String desf) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, currentKey);

        try (CipherInputStream cis = new CipherInputStream(new FileInputStream(new File(srcf)), cipher);
             FileOutputStream fos = new FileOutputStream(new File(desf))) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Des des = new Des();
        SecretKey key = des.genkey();
        des.loadKey(key);

        String data = "Truong dai hoc Nong Lam";
        String encrypted = des.encryptBase64(data);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + des.decryptBase64(Base64.getDecoder().decode(encrypted)));

        String src = "C:\\Users\\Trung Tri\\Desktop\\product-1.jpg";
        String dest = "C:\\Users\\Trung Tri\\Desktop\\product-2.jpg";
        String res = "C:\\Users\\Trung Tri\\Desktop\\product-3.jpg";
        des.encryptFile(src, dest);
        des.decryptFile(dest, res);
    }
}
