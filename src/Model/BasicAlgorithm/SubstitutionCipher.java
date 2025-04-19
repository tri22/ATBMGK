package Model.BasicAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstitutionCipher implements BasicAlgorithm{
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String KEY_PATH = "src/Model/BasicAlgorithm/keys/substitution.key";
	private Map<Character, Character> encryptMap = new HashMap<>();
	private Map<Character, Character> decryptMap = new HashMap<>();

	    public SubstitutionCipher() {
	        genKey();
	    }
	    
	    @Override
		public void loadKey() {
	    	loadKeyFromFile(KEY_PATH);
			
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
	        return saveKeyToFile(KEY_PATH);
	    }

	    public boolean saveKeyToFile(String path) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
	            for (char c : ALPHABET.toCharArray()) {
	                writer.write(c + ":" + encryptMap.get(c));
	                writer.newLine();
	            }
	            return true;
	        } catch (IOException e) {
	            System.err.println("Lỗi khi ghi key vào file: " + e.getMessage());
	            return false;
	        }
	    }

	    
	    public boolean loadKeyFromFile(String path) {
	        encryptMap.clear();
	        decryptMap.clear();

	        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains(":")) {
	                    char original = line.charAt(0);
	                    char mapped = line.charAt(2);
	                    encryptMap.put(original, mapped);
	                    decryptMap.put(mapped, original);
	                }
	            }
	            return encryptMap.size() == ALPHABET.length();
	        } catch (IOException e) {
	            System.err.println("Lỗi khi đọc key từ file: " + e.getMessage());
	            return false;
	        }
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
