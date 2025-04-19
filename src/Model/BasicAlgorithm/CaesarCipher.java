package Model.BasicAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CaesarCipher implements BasicAlgorithm {
    private int shift;
    private static final String KEY_PATH = "src/Model/BasicAlgorithm/keys/caesar.key";
    
    public CaesarCipher() {
//        genKey();
    }

    @Override
    public boolean genKey() {
        shift = new Random().nextInt(25) + 1;
        return saveKeyToFile(KEY_PATH);
    }
    
    @Override
	public void loadKey() {
    	loadKeyFromFile(KEY_PATH);
		
	}
    
    public boolean saveKeyToFile(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(String.valueOf(shift));
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi key vào file: " + e.getMessage());
            return false;
        }
    }

    public boolean loadKeyFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            shift = Integer.parseInt(line.trim());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc key từ file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String encrypt(String text) {
        return caesarShift(text, shift);
    }

    @Override
    public String decrypt(String text) {
        return caesarShift(text, -shift);
    }

    private String caesarShift(String text, int shiftVal) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char shifted = (char) ((c - 'A' + shiftVal + 26) % 26 + 'A');
                result.append(shifted);
            } else if (Character.isLowerCase(c)) {
                char shifted = (char) ((c - 'a' + shiftVal + 26) % 26 + 'a');
                result.append(shifted);
            } else {
                result.append(c); // giữ nguyên các ký tự khác
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        CaesarCipher cipher = new CaesarCipher(); // sẽ tự random key

        String original = "Trí đẹp trai quá Hello!";
        String encrypted = cipher.encrypt(original);
        System.out.println("Encrypted: " + encrypted);

        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

	
}
