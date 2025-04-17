package Controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Model.AsymmetryAlgorithm.*;
import Model.BasicAlgorithm.*;
import Model.SymmetryAlgorithm.*;


public class EncryptionController {
	private final Map<String, BasicAlgorithm> basics = new HashMap<>();
	private final Map<String, SymmetryAlgorithm> symmetrics = new HashMap<>();
	private final Map<String, AsymmetryAlgorithm> asymmetrics = new HashMap<>();;

	public EncryptionController() {
        // Basic Algorithms
        basics.put("Ceasar", new CaesarCipher());
        basics.put("Affine", new AffineCipher());
        basics.put("Hill", new HillCipher());
        basics.put("Substitution", new SubstitutionCipher());
        basics.put("Vigenere", new VigenereCipher());

        // Symmetric Algorithms
        symmetrics.put("AES", new AES());
        symmetrics.put("Anubis", new Anubis());
        symmetrics.put("BlowFish", new BlowFish());
        symmetrics.put("Camellia", new Camellia());
        symmetrics.put("Cast_128", new Cast_128());
        symmetrics.put("Des", new Des());
        symmetrics.put("MARS", new MARS());
        symmetrics.put("RC5", new RC5());
        symmetrics.put("3Des", new ThreeDES());
        symmetrics.put("TwoFish", new Twofish());

        // Asymmetric Algorithms
        asymmetrics.put("RSA", new RSA());
    }

    public String encrypt(String algorithm, String data) throws Exception {
        if (basics.containsKey(algorithm)) {
            return basics.get(algorithm).encrypt(data);
        }
        if (symmetrics.containsKey(algorithm)) {
            return symmetrics.get(algorithm).encrypt(data);
        }
        if (asymmetrics.containsKey(algorithm)) {
            return asymmetrics.get(algorithm).encrypt(data);
        }
        return "Thuật toán chưa được hỗ trợ: " + algorithm;
    }
    public boolean genKey(String algorithm, int keySize) throws Exception {
        if (basics.containsKey(algorithm)) {
           return  basics.get(algorithm).genKey();
        }
        if (symmetrics.containsKey(algorithm)) {
           return symmetrics.get(algorithm).genkey(keySize);
        }
        if (asymmetrics.containsKey(algorithm)) {
           return  asymmetrics.get(algorithm).genKey(keySize);
        }
		return false;
        
    }
    
    public void loadKey(String algorithm) throws Exception {
        if (symmetrics.containsKey(algorithm)) {
            symmetrics.get(algorithm).loadKey();
        }
        if (asymmetrics.containsKey(algorithm)) {
             asymmetrics.get(algorithm).loadKey();
        }
		
        
    }

	public String decrypt(String algorithm, String encryptedData) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
		   if (basics.containsKey(algorithm)) {
	            return basics.get(algorithm).decrypt(encryptedData);
	        }
	        if (symmetrics.containsKey(algorithm)) {
	            return symmetrics.get(algorithm).decrypt(encryptedData);
	        }
	        if (asymmetrics.containsKey(algorithm)) {
	            return asymmetrics.get(algorithm).decrypt(encryptedData);
	        }
	        return "Thuật toán chưa được hỗ trợ: " + algorithm;
	}


}
