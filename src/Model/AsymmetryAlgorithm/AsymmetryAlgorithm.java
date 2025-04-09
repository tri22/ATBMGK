package Model.AsymmetryAlgorithm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface AsymmetryAlgorithm {
	public void genKey(int keySize) throws NoSuchAlgorithmException, IOException;

	public String encryptBase64(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

	public String decryptBase64(String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException;

	public void loadKey() throws Exception;

	
}
