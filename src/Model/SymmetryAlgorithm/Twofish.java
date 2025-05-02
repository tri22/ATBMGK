package Model.SymmetryAlgorithm;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

public class Twofish implements SymmetryAlgorithm {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/Twofish.key";
    public String decrypt_path = "";
    public String encrypt_path = "";
    public String mode ="";
    public  String padding ="";
    private SecretKey key;

    @Override
    public boolean genkey() throws Exception {
        byte[] keyBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(keyBytes);
        key = new SecretKeySpec(keyBytes, "RAW");
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) {
        byte[] keyBytes = new byte[keySize / 8];
        new java.security.SecureRandom().nextBytes(keyBytes);
        key = new SecretKeySpec(keyBytes, "RAW");
        return saveKeyToFile();
    }

    @Override
    public void loadKey( ) {
        try {
			loadKeyFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "Twofish");
        this.key = key;
    }
    
    public boolean saveKeyToFile() {
        try {
            if (key == null) return false;

            byte[] encoded = key.getEncoded();
            Files.write(Paths.get(KEY_PATH), encoded);
            return true;

        } catch (IOException e) {
            e.printStackTrace(); // Hoặc log ra UI
            return false;
        }
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

	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, CryptoException {
		byte[] rawData = data.getBytes();
		byte[] encryptedData = process(true, rawData);
		return Base64.getEncoder().encodeToString(encryptedData);

	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, CryptoException {
		byte[] encryptedData = process(true, data);
		return Base64.getEncoder().encodeToString(encryptedData);

	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, CryptoException {
		byte[] encryptedData = Base64.getDecoder().decode(data);
		return decrypt(encryptedData);
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, CryptoException {
		byte[] decryptedData = process(false, data);
		return new String(decryptedData);

	}


    @Override
    public String encryptFile(String src) throws Exception {
    	this.decrypt_path = generateFileName(src,"decrypt");
    	this.encrypt_path = generateFileName(src,"encrypt");
    	byte[] content = readFile(src);
        byte[] encrypted = process(true, content);
        writeFile(encrypt_path, encrypted);
        return encrypt_path;
    }

    @Override
    public String decryptFile(String encryptedFilePath) throws Exception {
        byte[] content = readFile(encryptedFilePath);
        byte[] decrypted = process(false, content);
        writeFile(decrypt_path, decrypted);
        return decrypt_path ;
    }

    private byte[] readFile(String path) throws IOException {
        return java.nio.file.Files.readAllBytes(new File(path).toPath());
    }

    private void writeFile(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
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
 		return this.key;
 	}
    
    @Override
   	public void setSecretKey(byte[] keyBytes) {
   		this.key = new SecretKeySpec(keyBytes, "Twofish");
   	}

    public static void main(String[] args) throws Exception {
        Twofish camellia = new Twofish();
        System.out.println("Key generated: " + camellia.genkey(128)); // Example with 128 bits key

        String src = "C:\\Users\\Trung Tri\\Documents\\test.txt";
        String path=camellia.encryptFile(src);
        System.out.println("Encrypted file: " +path );
        System.out.println("Decrypted file: " + camellia.decryptFile(path));
    }
    

}
