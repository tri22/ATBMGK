package Model.BasicAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class AffineCipher implements BasicAlgorithm{
	int [] key ;
    private static final int N = 26;
    private static final String KEY_PATH = "src/Model/BasicAlgorithm/keys/affine.txt";
    public AffineCipher() {
    	genKey();
    }
    
    @Override
	public boolean genKey() {
		Random rand = new Random();
		int a;
		do {
			a = rand.nextInt(N);
		} while (gcd(a, N) != 1); 
		int b = rand.nextInt(N); 
		key = new int[] { a, b };
		return saveKeyToFile(KEY_PATH);
	}
    
    @Override
	public void loadKey() {
    	loadKeyFromFile(KEY_PATH);
	}
    
    public boolean saveKeyToFile(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(key[0] + "," + key[1]);
            return true; // Ghi file thành công
        } catch (Exception e) {
            System.err.println("Lỗi khi ghi key vào file: " + e.getMessage());
            return false; // Ghi file thất bại
        }
    }


    public void loadKeyFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                key = new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc key từ file: " + e.getMessage());
        }
    }


	@Override
	public String encrypt(String plaintext) {
		StringBuilder ciphertext = new StringBuilder();
		int a = key[0];
		int b = key[1];
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

	@Override
	public String decrypt(String ciphertext) {
		StringBuilder plaintext = new StringBuilder();
		int a = key[0];
		int b = key[1];
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

    
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }



    private static int modInverse(int a, int mod) {
        for (int i = 1; i < mod; i++) {
            if ((a * i) % mod == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("a không có nghịch đảo modulo theo " + mod);
    }

	

  

}
