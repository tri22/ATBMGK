package Controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Model.AsymmetryAlgorithm.*;
import Model.BasicAlgorithm.*;
import Model.HashAlgo.*;
import Model.SymmetryAlgorithm.*;

public class EncryptionController {
	private final Map<String, BasicAlgorithm> basics = new HashMap<>();
	private final Map<String, SymmetryAlgorithm> symmetrics = new HashMap<>();
	private final List<String> asymmetricsFile = new ArrayList<>();
	private final Map<String, AsymmetryAlgorithm> asymmetrics = new HashMap<>();
	private final Map<String, HashAlgo> hash = new HashMap<>();
	private Map<String, String[]> validKeySizes = new HashMap<>();

	public EncryptionController() {
		// Chuẩn hóa key thành chữ in hoa
		hash.put("MD5", new MD5());
		hash.put("SHA256", new SHA256());

		// Basic Algorithms
		basics.put("CEASAR", new CaesarCipher());
		basics.put("AFFINE", new AffineCipher());
		basics.put("SUBSTITUTION", new SubstitutionCipher());
		basics.put("VIGENERE", new VigenereCipher());
		basics.put("HILL", new Hill());

		// Symmetric Algorithms
		symmetrics.put("AES", new AES());
		symmetrics.put("BLOWFISH", new BlowFish());
		symmetrics.put("CAMELLIA", new Camellia());
		symmetrics.put("CAST_128", new Cast_128());
		symmetrics.put("DES", new Des());
		symmetrics.put("RC5", new RC5());
		symmetrics.put("3DES", new ThreeDES());
		symmetrics.put("TWOFISH", new Twofish());

		// Asymmetric Algorithms
		asymmetrics.put("RSA", new RSA());
		
		
		asymmetricsFile.add("RSA-AES");
		asymmetricsFile.add("RSA-DES");
		asymmetricsFile.add("RSA-3DES");
		asymmetricsFile.add("RSA-BLOWFISH");
		asymmetricsFile.add("RSA-TWOFISH");
		asymmetricsFile.add("RSA-RC5");
		asymmetricsFile.add("RSA-CAMELLIA");
		
		initValidKeySizes();
	}

	private void initValidKeySizes() {
		validKeySizes.put("AES", new String[] { "128", "192", "256" });
		validKeySizes.put("DES", new String[] { "56" });
		validKeySizes.put("3DES", new String[] { "112", "168" });
		validKeySizes.put("TWOFISH", new String[] { "128", "192", "256" });
		validKeySizes.put("BLOWFISH", new String[] { "32", "64", "128", "192", "256" });
		validKeySizes.put("CAMELLIA", new String[] { "128", "192", "256" });
		validKeySizes.put("RC5", new String[] { "64", "128", "256" });
		validKeySizes.put("CAST_128", new String[] { "40", "56", "64", "80", "128" });
		validKeySizes.put("RSA", new String[] { "512", "1024", "2048" });

		// Nếu cần thêm thuật toán mới => chỉ cần thêm dòng vào đây
	}

	private String normalize(String algorithm) {
		return algorithm.trim().toUpperCase();
	}
	
	public boolean genKey(String algorithm, int keySize) throws Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán tạo khóa đã chọn: " + algorithm);
		
		if (basics.containsKey(key)) {
			return basics.get(key).genKey(); // không cần kiểm tra key size
		}
		if (symmetrics.containsKey(key)) {
			int validatedSize = getValidatedKeySize(key, keySize);
			return symmetrics.get(key).genkey(validatedSize);
		}
		if (key.contains("RSA")) {			
			int validatedSize = getValidatedKeySize("RSA", keySize);
			return asymmetrics.get("RSA").genKey(validatedSize);
		}
		throw new Exception("Thuật toán không được hỗ trợ: " + key);
	}
	
	private int getValidatedKeySize(String algo, int requestedSize) throws Exception {
		String key = normalize(algo);
		String[] sizes = validKeySizes.get(key);
		if (sizes == null) {
			throw new Exception("Thuật toán \"" + key + "\" không hỗ trợ kiểm tra key size.");
		}
		
		for (String sizeStr : sizes) {
			if (Integer.parseInt(sizeStr) == requestedSize) {
				return requestedSize; // Hợp lệ
			}
		}
		
		throw new Exception("Key size " + requestedSize + " không hợp lệ cho thuật toán " + key
				+ ". Các giá trị hợp lệ là: " + String.join(", ", sizes));
	}
	
	public void loadKey(String algorithm) throws Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán tải khóa đã chọn: " + algorithm);
		
		if (basics.containsKey(key)) {
			basics.get(key).loadKey();
		}
		if (symmetrics.containsKey(key)) {
			symmetrics.get(key).loadKey();
		}
		if (key.contains("RSA")) {			
			asymmetrics.get(key).loadKey();
		}
	}

	public String encrypt(String algorithm, String data, String mode, String padding) throws Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán mã hóa đã chọn: " + algorithm);
		System.out.println("Mode đã chọn: " + mode);
		System.out.println("Padding đã chọn: " + padding);
		if (basics.containsKey(key)) {
			return basics.get(key).encrypt(data);
		}
		if (symmetrics.containsKey(key)) {
			SymmetryAlgorithm algo = symmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			return algo.encrypt(data);
		}
		if (asymmetrics.containsKey(key)) {
			AsymmetryAlgorithm algo = asymmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			return algo.encrypt(data);
		}
		if (hash.containsKey(key)) {
			return hash.get(key).hash(data);
		}
		return hash.get("MD5").hash(data);
	}


	public String decrypt(String algorithm, String encryptedData, String mode, String padding) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán giải mã đã chọn: " + algorithm);

		if (basics.containsKey(key)) {
			return basics.get(key).decrypt(encryptedData);
		}
		if (symmetrics.containsKey(key)) {
			SymmetryAlgorithm algo = symmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			return algo.decrypt(encryptedData);
		}
		if (asymmetrics.containsKey(key)) {
			AsymmetryAlgorithm algo = asymmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			return algo.decrypt(encryptedData);
		}
		return "Thuật toán chưa được hỗ trợ: " + algorithm;
	}

	public String encryptFile(String algorithm, String selectedFile, String mode, String padding) throws IOException, Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán mã hóa đã chọn: " + algorithm);
		if (symmetrics.containsKey(key)) {
			SymmetryAlgorithm algo = symmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			return symmetrics.get(key).encryptFile(selectedFile);
		}
		if (asymmetricsFile.contains(key)) {
			AsymmetryAlgorithm algo = asymmetrics.get("RSA");
			algo.setMode(mode);
			algo.setPadding(padding);
			String[] parts = key.split("-");
			String asymAlgo = parts[0]; // "RSA"
			String symAlgo = parts[1]; 
			System.out.println(symAlgo);
			String res = "Thuật toán đã mã hóa vào file: ";
			res += algo.encryptFile(selectedFile,symAlgo);
			return res;
		}
		if (hash.containsKey(key)) {
			return hash.get(key).hashFile(selectedFile);
		}
		return "Thuật toán chưa được hỗ trợ: " + algorithm;
	}

	public String decryptFile(String algorithm, String encryptedFile, String mode, String padding) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, Exception {
		String key = normalize(algorithm);
		System.out.println("Thuật toán mã hóa đã chọn: " + algorithm);
		if (symmetrics.containsKey(key)) { 
			SymmetryAlgorithm algo = symmetrics.get(key);
			algo.setMode(mode);
			algo.setPadding(padding);
			String res = "Thuật toán đã giải mã vào file: ";
			res += algo.decryptFile(encryptedFile);
			return res;
		}
		if (asymmetricsFile.contains(key)) {
			AsymmetryAlgorithm algo = asymmetrics.get("RSA");
			algo.setMode(mode);
			algo.setPadding(padding);
			String[] parts = key.split("-");
			String asymAlgo = parts[0]; // "RSA"
			String symAlgo = parts[1];  
			System.out.println(symAlgo);
			String res = "Thuật toán đã giải mã vào file: ";
			res +=  algo.decryptFile(symAlgo);
			return res;

		}
		return "Thuật toán chưa được hỗ trợ: " + algorithm;
	}
}
