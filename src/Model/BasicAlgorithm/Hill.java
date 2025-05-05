package Model.BasicAlgorithm;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hill implements BasicAlgorithm {
    private int[][] keyMatrix;
    private int matrixSize = 2;
    private static final int MOD = 26;
    private static final String KEY_FOLDER       = "keys";
    private static final String KEY_PATH = KEY_FOLDER+"/hill.txt";
    private Map<Integer, Integer> wordLengths = new HashMap<>(); 
    
    public Hill() {
    	File keyDir = new File(KEY_FOLDER);
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
    }
    
    @Override
    public boolean genKey() {
        Random rnd = new Random();
        int det;
        do {
            keyMatrix = new int[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++)
                for (int j = 0; j < matrixSize; j++)
                    keyMatrix[i][j] = rnd.nextInt(MOD);
            det = keyMatrix[0][0]*keyMatrix[1][1] - keyMatrix[0][1]*keyMatrix[1][0];
        } while (gcd(det, MOD) != 1);
        return saveKeyToFile(KEY_PATH);
    }

    private boolean saveKeyToFile(String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < matrixSize; i++) {
                bw.write(keyMatrix[i][0] + " " + keyMatrix[i][1]);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void loadKey() {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(KEY_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tk = line.trim().split("\\s+");
                rows.add(new int[]{ Integer.parseInt(tk[0]), Integer.parseInt(tk[1]) });
            }
            matrixSize = rows.size();
            keyMatrix = new int[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++)
                keyMatrix[i] = rows.get(i);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            keyMatrix = null;
        }
    }
    
    private String encryptWord(String word, int[][] key) {
        int n = key.length;
        List<Integer> block = new ArrayList<>();
        List<Boolean> isUpper = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) {
                block.add(Character.toUpperCase(c) - 'A');
                isUpper.add(Character.isUpperCase(c));

                if (block.size() == n) {
                    int[] encryptedBlock = multiplyMatrix(key, block);
                    for (int i = 0; i < n; i++) {
                        char encChar = (char) (encryptedBlock[i] + 'A');
                        if (!isUpper.get(i)) encChar = Character.toLowerCase(encChar);
                        result.append(encChar);
                    }
                    block.clear();
                    isUpper.clear();
                }
            } else {
                result.append(c); // nếu không phải chữ thì giữ nguyên
            }
        }

        // Padding nếu còn thừa
        if (!block.isEmpty()) {
            while (block.size() < n) {
                block.add('X' - 'A');
                isUpper.add(true);
            }
            int[] encryptedBlock = multiplyMatrix(key, block);
            for (int i = 0; i < n; i++) {
                char encChar = (char) (encryptedBlock[i] + 'A');
                if (!isUpper.get(i)) encChar = Character.toLowerCase(encChar);
                result.append(encChar);
            }
        }

        return result.toString();
    }

    
    public String encrypt(String plaintext) {
        String[] parts = plaintext.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String word = parts[i];
            wordLengths.put(i, countLetters(word));  // lưu độ dài thật của từ
            result.append(encryptWord(word, keyMatrix));
            if (i != parts.length - 1) result.append(" ");
        }

        return result.toString();
    }

    private int countLetters(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) count++;
        }
        return count;
    }

    private String decryptWord(String word, int[][] inverseKey, int originalLen) {
        int n = inverseKey.length;
        List<Integer> block = new ArrayList<>();
        List<Boolean> isUpper = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int letterCount = 0;

        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) {
                block.add(Character.toUpperCase(c) - 'A');
                isUpper.add(Character.isUpperCase(c));
                if (block.size() == n) {
                    int[] decryptedBlock = multiplyMatrix(inverseKey, block);
                    for (int i = 0; i < n; i++) {
                        if (letterCount < originalLen) {
                            char decChar = (char) (decryptedBlock[i] + 'A');
                            if (!isUpper.get(i)) decChar = Character.toLowerCase(decChar);
                            result.append(decChar);
                            letterCount++;
                        }
                    }
                    block.clear();
                    isUpper.clear();
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }



    public String decrypt(String ciphertext) {
        int[][] inverseKey = inverse(keyMatrix);
        String[] parts = ciphertext.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String word = parts[i];
            int originalLen = wordLengths.getOrDefault(i, word.length());
            result.append(decryptWord(word, inverseKey, originalLen));
            if (i != parts.length - 1) result.append(" ");
        }

        return result.toString();
    }




    // Hàm nhân ma trận 2x2 với vector 2x1, trả về vector 2 phần tử (mod 26).
    public static int[] applyMatrix(int[][] matrix, int[] vector) {
        int[] result = new int[2];
        result[0] = (matrix[0][0] * vector[0] + matrix[0][1] * vector[1]) % 26;
        result[1] = (matrix[1][0] * vector[0] + matrix[1][1] * vector[1]) % 26;
        // Đảm bảo không âm
        if (result[0] < 0) result[0] += 26;
        if (result[1] < 0) result[1] += 26;
        return result;
    }

    private int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }

    private int modInv(int a, int m) {
        a = (a%m + m)%m;
        for (int x = 1; x < m; x++) if (a*x % m == 1) return x;
        return -1;
    }

    private int[][] inverse(int[][] M) {
        int det = M[0][0]*M[1][1] - M[0][1]*M[1][0];
        int invDet = modInv(det, MOD);
        if (invDet == -1) return null;
        int[][] adj = {
            { M[1][1], -M[0][1] },
            { -M[1][0], M[0][0] }
        };
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                adj[i][j] = ((adj[i][j] * invDet) % MOD + MOD) % MOD;
        return adj;
    }

    private void ensureKeyLoaded() {
        if (keyMatrix == null) throw new IllegalStateException("Key not loaded"); 
    }
    public static int[] multiplyMatrix(int[][] matrix, List<Integer> block) {
        int n = matrix.length;
        int[] result = new int[n];
        
        // Thực hiện phép nhân ma trận với vector
        for (int i = 0; i < n; i++) {
            result[i] = 0;
            for (int j = 0; j < n; j++) {
                result[i] += matrix[i][j] * block.get(j);
            }
            result[i] = result[i] % 26;  // Đảm bảo giá trị nằm trong phạm vi [0, 25]
        }
        
        return result;
    }


    public static void main(String[] args) {
        Hill h = new Hill();
        System.out.println("GenKey: " + h.genKey());    
        h.loadKey();                                    
        String plain = "TAo la Tri cu bu";
        System.out.println(plain);
        String enc = h.encrypt(plain);
        System.out.println("Encrypted: " + enc);        
        System.out.println("Decrypted: " + h.decrypt(enc));
    }


}
