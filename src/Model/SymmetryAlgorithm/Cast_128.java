package Model.SymmetryAlgorithm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;

public class Cast_128 implements SymmetryAlgorithm {

    private SecretKey secretKey;
    private Cipher cipher;
    private static final String KEY_PATH = "src/Model/SymmetryAlgorithm/keys/cast_128.key";


    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("CAST5");  // CAST-128 được gọi là CAST5 trong Java
        keyGenerator.init(128); // CAST-128 sử dụng kích thước khóa 128 bit
        this.secretKey = keyGenerator.generateKey();
        return saveKeyToFile();
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("CAST5");
        keyGenerator.init(keySize); // Bạn có thể chỉ định kích thước khóa (128 bit cho CAST-128)
        this.secretKey = keyGenerator.generateKey();

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


    
    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "CAST5");
        this.secretKey = key;
    }

	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] rawData = data.getBytes();
		return encrypt(rawData);
	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("CAST5");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("CAST5");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(data);
		return new String(decryptedBytes);
	}

    @Override
    public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(srcf);
        FileOutputStream fos = new FileOutputStream(desf);
        cipher = Cipher.getInstance("CAST5");
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
        cipher = Cipher.getInstance("CAST5");
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
