package Model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class Des {
	

	public SecretKey genKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("DES");
		generator.init(56);
		return generator.generateKey();
	}
	
	public byte[] encrypt(String data,SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.ENCRYPT_MODE, key);	
		return c.doFinal(data.getBytes());
		
	}
	public String encryptToString(String data,SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = encrypt(data,key);
		 return Base64.getEncoder().encodeToString(bytes);
		
	}
	
	public void encrypt(String path, SecretKey key, String dest) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.ENCRYPT_MODE, key);

        File inputFile = new File(path);
        File outputFile = new File(dest);

        try (FileInputStream fis = new FileInputStream(inputFile);
             CipherOutputStream cos = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)), c)) {
            byte[] bytes = new byte[1024];
            int i;
            while ((i = fis.read(bytes)) != -1) {
                cos.write(bytes, 0, i);
            }
        }
    }

    public void decrypt(String path, SecretKey key, String dest) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.DECRYPT_MODE, key);

        File inputFile = new File(path);
        File outputFile = new File(dest);


        try (CipherInputStream cis = new CipherInputStream(new FileInputStream(inputFile), c);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            byte[] bytes = new byte[1024];
            int i;
            while ((i = cis.read(bytes)) != -1) {
                bos.write(bytes, 0, i);
            }
        }
    }
	
	public String decrypt(byte[] data,SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.DECRYPT_MODE, key);	
		return new String(c.doFinal(data));
	}
	
	public String decryptToString(String data,SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = Base64.getDecoder().decode(data);
		return decrypt(bytes, key);
		
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Des des = new Des();
		SecretKey key = des.genKey();
		String src = "C:\\Users\\Trung Tri\\Desktop\\product-1.jpg";
		String dest = "C:\\Users\\Trung Tri\\Desktop\\product-2.jpg";
		String res = "C:\\Users\\Trung Tri\\Desktop\\product-3.jpg";
//		String data = "Truong dai hoc Nong Lam";
//		String encryted = des.encryptToString(data,key);
//		System.out.println(encryted);
//		System.out.println(des.decryptToString(encryted,key));
		des.encrypt(src, key, dest);
		des.decrypt(dest, key, res);
		
	}
}
