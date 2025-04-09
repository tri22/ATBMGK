package Model.SymmetryAlgorithm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public interface SymmetryAlgorithm {
	public SecretKey genkey() throws NoSuchAlgorithmException, Exception;

	public SecretKey genkey(int keySize) throws NoSuchAlgorithmException;

	public void loadKey(SecretKey key);

	public String encryptBase64(String text) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public String decryptBase64(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;

	public boolean decryptFile(String srcf, String desf) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;
}
