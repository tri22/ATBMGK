package BE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstitutionCipher {
	private static final String VIETNAMESE_ALPHABET = 
		    "AĂÂBCDĐEÊGHIKLMNOÔƠPQRSTUƯVXY"
		    + "aăâbcdđeêghiklmnoôơpqrstuưvxy"
		    + "ÁÀẢÃẠẮẰẲẴẶẤẦẨẪẬÉÈẺẼẸẾỀỂỄỆÍÌỈĨỊ"
		    + "ÓÒỎÕỌỐỒỔỖỘỚỜỞỠỢÚÙỦŨỤỨỪỬỮỰÝỲỶỸỴ"
		    + "áàảãạắằẳẵặấầẩẫậéèẻẽẹếềểễệíìỉĩị"
		    + "óòỏõọốồổỗộớờởỡợúùủũụứừửữựýỳỷỹỵ";
	private static Map<Character, Character> encryptMap = new HashMap<>();
	private static Map<Character, Character> decryptMap = new HashMap<>();
	
	public static void mapAlphabet( ) {
		List<Character> list = new ArrayList<Character>();
		char [] arr = VIETNAMESE_ALPHABET.toCharArray();
		for (Character character : arr) {
			list.add(character);
		}
		Collections.shuffle(list);
		for (int i = 0; i < arr.length; i++) {
			encryptMap.put(arr[i], list.get(i));
			decryptMap.put( list.get(i),arr[i]);
		}
		
	}
	
	public static String encrypt ( String text) {
		StringBuilder res = new StringBuilder();
		 for (char c : text.toCharArray()) {
	            res.append(encryptMap.getOrDefault(c,c));
	        }
	        return res.toString();
	}
	public static String decrypt ( String text) {
		StringBuilder res = new StringBuilder();
		 for (char c : text.toCharArray()) {
	            res.append(decryptMap.getOrDefault(c,c));
	        }
	        return res.toString();
	}
	 public static void main(String[] args) {
	        mapAlphabet(); // 
	        String original = "khoa công nghệ thông tin đại học nông lâm";
	        String encrypted = encrypt(original);
	        System.out.println("Encrypted: " + encrypted);
	        System.out.println("Decrypted: " + decrypt(encrypted));
	    }
}
