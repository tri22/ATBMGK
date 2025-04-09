package Model.SymmetryAlgorithm;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

public class Twofish implements SymmetryAlgorithm {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private SecretKey key;

    @Override
    public SecretKey genkey() throws NoSuchAlgorithmException {
        byte[] keyBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(keyBytes);
        key = new SecretKeySpec(keyBytes, "Twofish");
        return key;
    }

    @Override
    public SecretKey genkey(int keySize) throws NoSuchAlgorithmException {
        byte[] keyBytes = new byte[keySize / 8];
        new java.security.SecureRandom().nextBytes(keyBytes);
        key = new SecretKeySpec(keyBytes, "Twofish");
        return key;
    }

    @Override
    public void loadKey(SecretKey key) {
        this.key = key;
    }

    private byte[] process(boolean encrypt, byte[] input) throws CryptoException {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new TwofishEngine());
        cipher.init(encrypt, new KeyParameter(key.getEncoded()));
        byte[] output = new byte[cipher.getOutputSize(input.length)]; 
        int bytesProcessed = cipher.processBytes(input, 0, input.length, output, 0);
        bytesProcessed += cipher.doFinal(output, bytesProcessed);
        byte[] result = new byte[bytesProcessed];
        System.arraycopy(output, 0, result, 0, bytesProcessed);
        return result;
    }

    @Override
    public String encryptBase64(String text) throws Exception {
        byte[] encrypted = process(true, text.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decryptBase64(byte[] data) throws Exception {
        byte[] decrypted = process(false, Base64.getDecoder().decode(data));
        return new String(decrypted);
    }

    @Override
    public boolean encryptFile(String srcf, String desf) throws Exception {
        byte[] content = readFile(srcf);
        byte[] encrypted = process(true, content);
        writeFile(desf, encrypted);
        return true;
    }

    @Override
    public boolean decryptFile(String srcf, String desf) throws Exception {
        byte[] content = readFile(srcf);
        byte[] decrypted = process(false, content);
        writeFile(desf, decrypted);
        return true;
    }

    private byte[] readFile(String path) throws IOException {
        return java.nio.file.Files.readAllBytes(new File(path).toPath());
    }

    private void writeFile(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
    }

    public static void main(String[] args) throws Exception {
        Twofish tf = new Twofish();
        SecretKey key = tf.genkey();
        tf.loadKey(key);

        String plain = "Twofish";
        String encrypted = tf.encryptBase64(plain);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = tf.decryptBase64(encrypted.getBytes());
        System.out.println("Decrypted: " + decrypted);
    }
}
