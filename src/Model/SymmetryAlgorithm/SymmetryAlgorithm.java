package Model.SymmetryAlgorithm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public interface SymmetryAlgorithm {
	public boolean genkey() throws NoSuchAlgorithmException, Exception;

	public boolean genkey(int keySize) throws NoSuchAlgorithmException;

	public void loadKey();

	public String encrypt(String text) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public String decrypt(String data) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;

	public boolean decryptFile(String srcf, String desf) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;

}
