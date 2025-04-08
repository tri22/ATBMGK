package Model;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	 public void genKey(int size) {
	        try {
	            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	            keyGen.initialize(size); // Bạn có thể chọn 1024, 2048, 4096...

	            KeyPair keyPair = keyGen.generateKeyPair();
	            publicKey = keyPair.getPublic();
	            privateKey = keyPair.getPrivate();

	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	 }

	 public byte[] encryt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	     Cipher cipher = Cipher.getInstance("RSA");
	     cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	     return cipher.doFinal(data.getBytes()); 
	 }

	
	 public String encryt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	     byte[] encryptedBytes = encryt(new String(data));
	     return Base64.getEncoder().encodeToString(encryptedBytes); 
	 }

	 

	 public String decryt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
	     return decryt(Base64.getDecoder().decode(data));
	 }


	 public String decryt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
	     Cipher cipher = Cipher.getInstance("RSA");
	     cipher.init(Cipher.DECRYPT_MODE, privateKey);
	     byte[] decryptedBytes = cipher.doFinal(data); 
	     return new String(decryptedBytes); 
	 }
	 
	 public static void main(String[] args) {
	        try {
	            RSA rsa = new RSA();
	            rsa.genKey(2048);

	            String original = "Hello RSA!";
	            System.out.println("Original: " + original);

	            String encrypted = rsa.encryt(original.getBytes()); 
	            System.out.println("Encrypted: " + encrypted);

	            String decrypted = rsa.decryt(encrypted); 
	            System.out.println("Decrypted: " + decrypted);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
