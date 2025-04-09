package Model.BasicAlgorithm;

import java.util.Random;

public class CaesarCipher implements BasicAlgorithm {
    private int shift;

    public CaesarCipher() {
        genKey();
    }

    @Override
    public void genKey() {
        shift = new Random().nextInt(25) + 1;
        System.out.println("Generated Caesar key: " + shift);
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
