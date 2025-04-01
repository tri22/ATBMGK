package BE;

import java.util.Random;

public class HillCipher {

	// Hàm tạo ma trận khóa ngẫu nhiên
	public static int[][] generateKey(int n, int mod) {
	    int[][] key = new int[n][n];
	    Random rand = new Random();

	    // Sinh ma trận khóa ngẫu nhiên
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            key[i][j] = rand.nextInt(mod); // Giá trị ngẫu nhiên trong phạm vi modulo
	        }
	    }

	    // Kiểm tra ma trận có khả năng nghịch đảo không
	    while (determinant(key, mod) == 0 || modInverse(determinant(key, mod), mod) == -1) {
	        // Nếu định thức bằng 0 hoặc không có nghịch đảo, tạo lại ma trận
	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j < n; j++) {
	                key[i][j] = rand.nextInt(mod);
	            }
	        }
	    }

	    return key;
	}


    // Hàm tính định thức của ma trận
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

    // Hàm tính cofactor
    private static int cofactor(int[][] matrix, int row, int col, int mod) {
        return (int) (Math.pow(-1, row + col) * determinant(getMinor(matrix, row, col), mod)) % mod;
    }

    // Hàm lấy ma trận con (minor)
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

    // Hàm tính ma trận nghịch đảo modulo
    private static int[][] modInverseMatrix(int[][] matrix, int mod) {
        int n = matrix.length;
        int det = determinant(matrix, mod);
        int detInv = modInverse(det, mod);

        int[][] adjugate = adjugateMatrix(matrix, mod);
        int[][] inverse = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = (adjugate[i][j] * detInv) % mod;
                if (inverse[i][j] < 0) {
                    inverse[i][j] += mod;
                }
            }
        }
        return inverse;
    }

    // Hàm tính ma trận adjugate (bù đại số)
    private static int[][] adjugateMatrix(int[][] matrix, int mod) {
        int n = matrix.length;
        int[][] adj = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[j][i] = cofactor(matrix, i, j, mod); // Chuyển vị cofactor
                if (adj[j][i] < 0) adj[j][i] += mod;
            }
        }
        return adj;
    }

 // Hàm tìm nghịch đảo modulo
    private static int modInverse(int a, int m) {
        a = (a % m + m) % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;  // Không có nghịch đảo
    }


    // Hàm nhân ma trận với vector
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

    // Hàm mã hóa
    public static String encrypt(String plaintext, int[][] key) {
        int n = key.length;
        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        int len = plaintext.length();

        // Thêm 'X' để độ dài chia hết cho n
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

            int[] result = multiplyMatrixVector(key, vector, 26);
            for (int j = 0; j < n; j++) {
                ciphertext.append((char) (result[j] + 'A'));
            }
        }
        return ciphertext.toString();
    }

    // Hàm giải mã
    public static String decrypt(String ciphertext, int[][] key) {
        int n = key.length;
        int[][] inverseKey = modInverseMatrix(key, 26);

        StringBuilder plaintext = new StringBuilder();
        int len = ciphertext.length();

        for (int i = 0; i < len; i += n) {
            int[] vector = new int[n];
            for (int j = 0; j < n; j++) {
                vector[j] = ciphertext.charAt(i + j) - 'A';
            }

            int[] result = multiplyMatrixVector(inverseKey, vector, 26);
            for (int j = 0; j < n; j++) {
                plaintext.append((char) (result[j] + 'A'));
            }
        }
        return plaintext.toString();
    }

    public static void main(String[] args) {
        // Ví dụ tạo ma trận khóa 3x3
        int n = 3;
        int mod = 26;  // Modulo 26 cho bảng chữ cái tiếng Anh
        int[][] key = generateKey(n, mod);

        // In ma trận khóa
        System.out.println("Generated Key Matrix:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(key[i][j] + " ");
            }
            System.out.println();
        }

        String plaintext = "PAYMOREMONEY";
        System.out.println("Original text: " + plaintext);

        // Mã hóa văn bản
        String encrypted = encrypt(plaintext, key);
        System.out.println("Encrypted text: " + encrypted);

        // Giải mã văn bản
        String decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted text: " + decrypted);
    }
}
