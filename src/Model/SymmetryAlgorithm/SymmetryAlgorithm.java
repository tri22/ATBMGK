package Model.SymmetryAlgorithm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public interface SymmetryAlgorithm {
	public boolean genkey() throws NoSuchAlgorithmException, Exception;

	public boolean genkey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException;

	public void loadKey();

	public String encrypt(String text) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public String decrypt(String data) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception;

	public String encryptFile(String src) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;

	public String decryptFile(String encryptedFilePath) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;
	public SecretKey getSecretKey();

	public void setSecretKey(byte[] decryptedSymmetricKeyBytes);
	
	public void setMode(String mode);

	public void setPadding(String padding);
	
	public void generateIV();
}
