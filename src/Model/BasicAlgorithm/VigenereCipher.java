package Model.BasicAlgorithm;

import java.util.Random;

public class VigenereCipher implements BasicAlgorithm {

    private String key = "";

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
    public void genKey() {
        this.key = generateRandomKey(5);
    }

    // Dùng key đã tạo để mã hóa
    @Override
    public String encrypt(String text) {
        if (key.isEmpty()) {
            genKey();
        }
        return encrypt(text, key);
    }

    // Dùng key đã tạo để giải mã
    @Override
    public String decrypt(String text) {
        if (key.isEmpty()) {
            throw new IllegalStateException("Key is not generated. Call genKey() first.");
        }
        return decrypt(text, key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
