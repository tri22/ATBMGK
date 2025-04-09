package Model.BasicAlgorithm;

import java.util.Random;

public class AffineCipher implements BasicAlgorithm{
	int [] key ;
    private static final int N = 26;
    
    public AffineCipher() {
    	genKey();
    }
    
    @Override
	public void genKey() {
		Random rand = new Random();
		int a;
		do {
			a = rand.nextInt(N);
		} while (gcd(a, N) != 1); // Chọn a sao cho gcd(a, N) = 1
		int b = rand.nextInt(N); // Chọn b bất kỳ trong khoảng [0, N-1]
		key = new int[] { a, b };
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

  

	
	
	  public static void main(String[] args) {
	        AffineCipher af = new AffineCipher();
	        
	        String plaintext = "Hello World!";
	        String encryptedText = af.encrypt(plaintext);
	        System.out.println("Encrypted: " + encryptedText);
	        
	        String decryptedText = af.decrypt(encryptedText);
	        System.out.println("Decrypted: " + decryptedText);
	    }
}
