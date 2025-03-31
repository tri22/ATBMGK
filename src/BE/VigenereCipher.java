package BE;

import java.util.Random;

public class VigenereCipher {
    public static String generateRandomKey(int length) {
        Random rand = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + rand.nextInt(26));
            key.append(randomChar);
        }
        return key.toString();
    }

    public static String generateKey(String text, String key) {
        StringBuilder newKey = new StringBuilder();
        int keyIndex = 0;
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                newKey.append(key.charAt(keyIndex % key.length()));
                keyIndex++;
            } else {
                newKey.append(ch);
            }
        }
        return newKey.toString();
    }

    public static String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = generateKey(text, key);
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i);
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

    public static String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = generateKey(text, key);
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = key.charAt(i);
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

    public static void main(String[] args) {
        String plaintext = "Hello World!";
        String key = generateRandomKey(5);
        System.out.println("Generated Key: " + key);
        
        String encryptedText = encrypt(plaintext, key);
        System.out.println("Encrypted: " + encryptedText);
        
        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted: " + decryptedText);
    }
}
