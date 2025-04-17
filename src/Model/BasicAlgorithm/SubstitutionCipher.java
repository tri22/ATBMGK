package Model.BasicAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstitutionCipher implements BasicAlgorithm{
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		   
	 private Map<Character, Character> encryptMap = new HashMap<>();
	    private Map<Character, Character> decryptMap = new HashMap<>();

	    public SubstitutionCipher() {
	        genKey();
	    }

	    public boolean genKey() {
	        List<Character> list = new ArrayList<>();
	        for (char c : ALPHABET.toCharArray()) {
	            list.add(c);
	        }
	        Collections.shuffle(list);
	        for (int i = 0; i < ALPHABET.length(); i++) {
	            encryptMap.put(ALPHABET.charAt(i), list.get(i));
	            decryptMap.put(list.get(i), ALPHABET.charAt(i));
	        }
	        return true;
	    }

	    public String encrypt(String text) {
	        StringBuilder result = new StringBuilder();
	        for (char c : text.toCharArray()) {
	            result.append(encryptMap.getOrDefault(c, c));
	        }
	        return result.toString();
	    }

	    public String decrypt(String text) {
	        StringBuilder result = new StringBuilder();
	        for (char c : text.toCharArray()) {
	            result.append(decryptMap.getOrDefault(c, c));
	        }
	        return result.toString();
	    }

	    public static void main(String[] args) {
	        SubstitutionCipher cipher = new SubstitutionCipher();
	        String original = "Hello world";
	        String encrypted = cipher.encrypt(original);
	        String decrypted = cipher.decrypt(encrypted);

	        System.out.println("Original:  " + original);
	        System.out.println("Encrypted: " + encrypted);
	        System.out.println("Decrypted: " + decrypted);
	    }
	}
