package Model.BasicAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class VigenereCipher implements BasicAlgorithm {
	private static final String KEY_PATH = "src/Model/BasicAlgorithm/keys/vigenere.txt";
    private String key = "";

    
    
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	// Sinh khóa ngẫu nhiên với độ dài chỉ định
    public static String generateRandomKey(int length) {
        Random rand = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + rand.nextInt(26));
            key.append(randomChar);
        }
        return key.toString();
    }
    
    @Override
	public void loadKey() {
    	loadKeyFromFile(KEY_PATH);
	}
    
    public boolean saveKeyToFile(String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(key);
            }

            return true;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi key vào file: " + e.getMessage());
            return false;
        }
    }

    public boolean loadKeyFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            key = reader.readLine();
            return key != null && !key.isEmpty();
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc key từ file: " + e.getMessage());
            return false;
        }
    }

    // Mã hóa văn bản với key
    public static String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i % key.length());
            if (Character.isUpperCase(ch)) {
                int x = (ch - 'A' + (Character.toUpperCase(keyCh) - 'A')) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int x = (ch - 'a' + (Character.toUpperCase(keyCh) - 'A')) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    // Giải mã văn bản với key
    public static String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i % key.length());
            if (Character.isUpperCase(ch)) {
                int x = (ch - 'A' - (Character.toUpperCase(keyCh) - 'A') + 26) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int x = (ch - 'a' - (Character.toUpperCase(keyCh) - 'A') + 26) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    // Gán key ngẫu nhiên mặc định độ dài 5
    @Override
    public boolean genKey() {
        this.key = generateRandomKey(5);
        return saveKeyToFile(KEY_PATH);
    }

    // Dùng key đã tạo để mã hóa
    @Override
    public String encrypt(String text) {
        if (key.isEmpty()) {
            genKey();
        }
        return encrypt(text, key);
    }

    @Override
    public String decrypt(String text) {
        if (key.isEmpty()) {
            throw new IllegalStateException("Key is not generated. Call genKey() first.");
        }
        return decrypt(text, key);
    }

    public static void main(String[] args) {
        VigenereCipher cipher = new VigenereCipher();
        cipher.genKey();

        String plaintext = "Hello World! I am Tri.";
        String encrypted = cipher.encrypt(plaintext);
        String decrypted = cipher.decrypt(encrypted);

        System.out.println("Generated Key: " + cipher.getKey());
        System.out.println("Plaintext:  " + plaintext);
        System.out.println("Encrypted:  " + encrypted);
        System.out.println("Decrypted:  " + decrypted);
    }

	
}
