package Model.AsymmetryAlgorithm;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA implements AsymmetryAlgorithm {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void genKey(int size) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = encrypt(new String(data));
        return Base64.getEncoder().encodeToString(encryptedBytes);
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
    public String encryptBase64(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(data.getBytes());
    }

    @Override
    public String decryptBase64(String data) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(data);
    }

    @Override
    public void loadKey() throws Exception {
        // Hiện tại chưa có logic nạp khóa từ bên ngoài.
        throw new UnsupportedOperationException("Chức năng loadKey chưa được hỗ trợ.");
    }

    public static void main(String[] args) {
        try {
            RSA rsa = new RSA();
            rsa.genKey(2048);

            String original = "Hello RSA!";
            System.out.println("Original: " + original);

            String encrypted = rsa.encryptBase64(original);
            System.out.println("Encrypted: " + encrypted);

            String decrypted = rsa.decryptBase64(encrypted);
            System.out.println("Decrypted: " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
