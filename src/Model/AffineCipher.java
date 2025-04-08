package Model;

import java.util.Random;

public class AffineCipher {
    private static final int N = 26;

    public static int[] generateKey() {
        Random rand = new Random();
        int a;
        do {
            a = rand.nextInt(N);
        } while (gcd(a, N) != 1); // Chọn a sao cho gcd(a, N) = 1
        int b = rand.nextInt(N); // Chọn b bất kỳ trong khoảng [0, N-1]
        return new int[]{a, b};
    }

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static String encrypt(String plaintext, int a, int b) {
        StringBuilder ciphertext = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                int x = ch - 'A';
                int y = (a * x + b) % N;
                ciphertext.append((char) (y + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int x = ch - 'a';
                int y = (a * x + b) % N;
                ciphertext.append((char) (y + 'a'));
            } else {
                ciphertext.append(ch);
            }
        }
        return ciphertext.toString();
    }

    public static String decrypt(String ciphertext, int a, int b) {
        StringBuilder plaintext = new StringBuilder();
        int aInverse = modInverse(a, N);

        for (char ch : ciphertext.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                int y = ch - 'A';
                int x = (aInverse * (y - b + N)) % N;
                plaintext.append((char) (x + 'A'));
            } else if (Character.isLowerCase(ch)) {
                int y = ch - 'a';
                int x = (aInverse * (y - b + N)) % N;
                plaintext.append((char) (x + 'a'));
            } else {
                plaintext.append(ch);
            }
        }
        return plaintext.toString();
    }

    private static int modInverse(int a, int mod) {
        for (int i = 1; i < mod; i++) {
            if ((a * i) % mod == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("a không có nghịch đảo modulo theo " + mod);
    }

    public static void main(String[] args) {
        int[] key = generateKey();
        int a = key[0], b = key[1];
        System.out.println("Generated Key: a = " + a + ", b = " + b);
        
        String plaintext = "Hello World!";
        String encryptedText = encrypt(plaintext, a, b);
        System.out.println("Encrypted: " + encryptedText);
        
        String decryptedText = decrypt(encryptedText, a, b);
        System.out.println("Decrypted: " + decryptedText);
    }
}
