package Model.BasicAlgorithm;

import java.util.Random;

public class HillCipher implements BasicAlgorithm {

    private int[][] key;
    private final int MOD = 26; // Modulo cho bảng chữ cái tiếng Anh (A-Z)
    private final int MATRIX_SIZE = 3; // Kích thước ma trận khóa

    public HillCipher() {
        genKey();
    }

    // Sinh ma trận khóa ngẫu nhiên hợp lệ
    public void genKey() {
        key = generateKey(MATRIX_SIZE, MOD);
    }

    // Tạo ma trận khóa ngẫu nhiên
    public static int[][] generateKey(int n, int mod) {
        int[][] key = new int[n][n];
        Random rand = new Random();

        do {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    key[i][j] = rand.nextInt(mod);
                }
            }
        } while (determinant(key, mod) == 0 || modInverse(determinant(key, mod), mod) == -1);

        return key;
    }

    // Mã hóa văn bản
    public String encrypt(String plaintext) {
        int n = key.length;
        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        int len = plaintext.length();

        while (len % n != 0) {
            plaintext += "X";
            len++;
        }

        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < len; i += n) {
            int[] vector = new int[n];
            for (int j = 0; j < n; j++) {
                vector[j] = plaintext.charAt(i + j) - 'A';
            }

            int[] result = multiplyMatrixVector(key, vector, MOD);
            for (int j = 0; j < n; j++) {
                ciphertext.append((char) (result[j] + 'A'));
            }
        }
        return ciphertext.toString();
    }

    // Giải mã văn bản
    public String decrypt(String ciphertext) {
        int n = key.length;
        int[][] inverseKey = modInverseMatrix(key, MOD);

        StringBuilder plaintext = new StringBuilder();
        int len = ciphertext.length();

        for (int i = 0; i < len; i += n) {
            int[] vector = new int[n];
            for (int j = 0; j < n; j++) {
                vector[j] = ciphertext.charAt(i + j) - 'A';
            }

            int[] result = multiplyMatrixVector(inverseKey, vector, MOD);
            for (int j = 0; j < n; j++) {
                plaintext.append((char) (result[j] + 'A'));
            }
        }
        return plaintext.toString();
    }

    // ===== Các hàm toán học hỗ trợ =====

    private static int[][] modInverseMatrix(int[][] matrix, int mod) {
        int n = matrix.length;
        int det = determinant(matrix, mod);
        int detInv = modInverse(det, mod);

        int[][] adjugate = adjugateMatrix(matrix, mod);
        int[][] inverse = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = (adjugate[i][j] * detInv) % mod;
                if (inverse[i][j] < 0) inverse[i][j] += mod;
            }
        }
        return inverse;
    }

    private static int[][] adjugateMatrix(int[][] matrix, int mod) {
        int n = matrix.length;
        int[][] adj = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[j][i] = cofactor(matrix, i, j, mod);
                if (adj[j][i] < 0) adj[j][i] += mod;
            }
        }
        return adj;
    }

    private static int cofactor(int[][] matrix, int row, int col, int mod) {
        return (int) (Math.pow(-1, row + col) * determinant(getMinor(matrix, row, col), mod)) % mod;
    }

    private static int[][] getMinor(int[][] matrix, int row, int col) {
        int n = matrix.length;
        int[][] minor = new int[n - 1][n - 1];
        int r = 0, c;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        return minor;
    }

    private static int determinant(int[][] matrix, int mod) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) % mod;

        int det = 0;
        for (int j = 0; j < n; j++) {
            det += matrix[0][j] * cofactor(matrix, 0, j, mod);
        }
        return (det % mod + mod) % mod;
    }

    private static int modInverse(int a, int m) {
        a = (a % m + m) % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }

    private static int[] multiplyMatrixVector(int[][] matrix, int[] vector, int mod) {
        int n = matrix.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = 0;
            for (int j = 0; j < n; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
            result[i] = (result[i] % mod + mod) % mod;
        }
        return result;
    }

    public static void main(String[] args) {
        HillCipher hill = new HillCipher();

        String plain = "HELLOHILL i am tri";
        String encrypted = hill.encrypt(plain);
        String decrypted = hill.decrypt(encrypted);

        System.out.println("Original: " + plain);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
} 
