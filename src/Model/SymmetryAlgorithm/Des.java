package Model.SymmetryAlgorithm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Des implements SymmetryAlgorithm {
	private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/des.key";
    private SecretKey currentKey;

    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        generator.init(56);
        this.currentKey = generator.generateKey();
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        // DES chỉ hỗ trợ key 56 bit nên tham số keySize bị bỏ qua
        return genkey();
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
            if (currentKey == null) return false;

            byte[] encoded = currentKey.getEncoded();
            Files.write(Paths.get(KEY_PATH), encoded);
            return true;

        } catch (IOException e) {
            e.printStackTrace(); // Hoặc log ra UI
            return false;
        }
    }
    
    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "DES");
        this.currentKey = key;
    }



	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] rawData = data.getBytes();
		return encrypt(rawData);
	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, currentKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, currentKey);
		byte[] decryptedBytes = cipher.doFinal(data);
		return new String(decryptedBytes);
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

    
}
