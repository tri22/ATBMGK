package Model;

import java.util.Random;

public class VigenereCipher {

    // Hàm tạo key ngẫu nhiên với độ dài nhất định
    public static String generateRandomKey(int length) {
        Random rand = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + rand.nextInt(26));  // Lấy ký tự ngẫu nhiên từ A-Z
            key.append(randomChar);
        }
        return key.toString();
    }

    // Hàm mã hóa Vigenère
    public static String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        
        // Chia văn bản thành các khối và mã hóa mỗi khối
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i % key.length());  // Lặp lại key nếu cần
            if (Character.isUpperCase(ch)) {
                int x = (ch - 'A' + (Character.toUpperCase(keyCh) - 'A')) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int x = (ch - 'a' + (Character.toUpperCase(keyCh) - 'A')) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(ch);  // Thêm ký tự không phải chữ cái vào kết quả mà không thay đổi
            }
        }
        return result.toString();
    }

    // Hàm giải mã Vigenère
    public static String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        
        // Giải mã văn bản theo từng khối
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i % key.length());  // Lặp lại key nếu cần
            if (Character.isUpperCase(ch)) {
                int x = (ch - 'A' - (Character.toUpperCase(keyCh) - 'A') + 26) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int x = (ch - 'a' - (Character.toUpperCase(keyCh) - 'A') + 26) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(ch);  // Thêm ký tự không phải chữ cái vào kết quả mà không thay đổi
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String plaintext = "HELLO WORLD I AM TRI";
        String key = generateRandomKey(5);  // Tạo khóa ngẫu nhiên có độ dài 5
        System.out.println("Generated Key: " + key);
        
        String encryptedText = encrypt(plaintext, key);  // Mã hóa văn bản
        System.out.println("Encrypted: " + encryptedText);
        
        String decryptedText = decrypt(encryptedText, key);  // Giải mã văn bản
        System.out.println("Decrypted: " + decryptedText);
    }
}

