package Model.AsymmetryAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA implements AsymmetryAlgorithm {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String PUBLIC_KEY_PATH = "src/Model/AsymmetryAlgorithm/keys/public.key";
    private static final String PRIVATE_KEY_PATH = "src/Model/AsymmetryAlgorithm/keys/private.key";

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


}
