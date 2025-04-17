package Model.AsymmetryAlgorithm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface AsymmetryAlgorithm {
	public boolean genKey(int keySize) throws NoSuchAlgorithmException, IOException;

	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

	public String decrypt(String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException;

	public void loadKey() throws Exception;

	
}
