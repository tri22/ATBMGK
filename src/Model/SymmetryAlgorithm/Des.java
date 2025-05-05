package Model.SymmetryAlgorithm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des implements SymmetryAlgorithm {
	private static final String KEY_FOLDER       = "keys";
	private static final String KEY_PATH = KEY_FOLDER+"/des.txt";
    private SecretKey currentKey;
    public String decrypt_path = "";
    public String encrypt_path = "";
    public String mode = "";  
    public String padding = "";  
    public IvParameterSpec ivSpec;
	  
    public Des() {
    	File keyDir = new File(KEY_FOLDER);
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
	}
    
	@Override
	public void generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[8]; 
        random.nextBytes(iv);  
        this.ivSpec = new IvParameterSpec(iv);  
    }
	
    @Override
    public boolean genkey() throws NoSuchAlgorithmException {
        return genkey(56);
    }

    @Override
    public boolean genkey(int keySize) throws NoSuchAlgorithmException {
        // DES chỉ hỗ trợ key 56 bit nên tham số keySize bị bỏ qua
    	  KeyGenerator generator = KeyGenerator.getInstance("DES");
          generator.init(keySize);
          this.currentKey = generator.generateKey();
          generateIV();
          return saveKeyToFile();
    }

    @Override
    public void loadKey() {
    	try {
			loadKeyFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean saveKeyToFile() {
        try {
            if (currentKey == null) return false;

            byte[] encoded = currentKey.getEncoded();
            Files.write(Paths.get(KEY_PATH), encoded);
            return true;

        } catch (IOException e) {
            e.printStackTrace(); // Hoặc log ra UI
            return false;
        }
    }
    
    public void loadKeyFromFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(KEY_PATH));
        SecretKey key = new SecretKeySpec(encoded, "DES");
        this.currentKey = key;
    }



	public String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		byte[] rawData = data.getBytes();
		return encrypt(rawData);
	}

	public String encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance("DES"+ mode + padding);
		if (!mode.contains("ECB")) {
		    cipher.init(Cipher.ENCRYPT_MODE, currentKey, ivSpec);
		} else {
		    cipher.init(Cipher.ENCRYPT_MODE, currentKey);
		}
		return Base64.getEncoder().encodeToString(cipher.doFinal(data));
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		return decrypt(Base64.getDecoder().decode(data));
	}

	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance("DES"+ mode + padding);
		if (!mode.contains("ECB")) {
		    cipher.init(Cipher.DECRYPT_MODE, currentKey, ivSpec);
		} else {
		    cipher.init(Cipher.DECRYPT_MODE, currentKey);
		}
		byte[] decryptedBytes = cipher.doFinal(data);
		return new String(decryptedBytes);
	}

    @Override
    public String encryptFile(String src) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        this.decrypt_path = generateFileName(src,"decrypt");
        this.encrypt_path = generateFileName(src,"encrypt");
        Cipher cipher = Cipher.getInstance("DES"+ mode + padding);
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.ENCRYPT_MODE, currentKey, ivSpec);
		} else {
		    cipher.init(Cipher.ENCRYPT_MODE, currentKey);
		}
        try (FileInputStream fis = new FileInputStream(new File(src));
             CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(new File(encrypt_path)), cipher)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
            }
        }
        return encrypt_path;
    }

    @Override
    public String decryptFile(String encryptedFilePath) throws Exception {
        if (currentKey == null) throw new Exception("Key is not initialized");
        Cipher cipher = Cipher.getInstance("DES"+ mode + padding);
        if (!mode.contains("ECB")) {
		    cipher.init(Cipher.DECRYPT_MODE, currentKey, ivSpec);
		} else {
		    cipher.init(Cipher.DECRYPT_MODE, currentKey);
		}
        try (CipherInputStream cis = new CipherInputStream(new FileInputStream(new File(encryptedFilePath)), cipher);
             FileOutputStream fos = new FileOutputStream(new File(decrypt_path))) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }
        // Sau khi giải mã xong
	    Path outputPath = Paths.get(decrypt_path);
	    String mimeType = Files.probeContentType(outputPath);
	    
	    if (mimeType != null && mimeType.startsWith("text")) {
	        String content = Files.readString(outputPath, StandardCharsets.UTF_8);
	        return "Đường dẫn: " + outputPath.toAbsolutePath() + "\n" + content;
	    } else {
	        return "Đường dẫn: " + outputPath.toAbsolutePath() + "\n(File không phải dạng văn bản)";
	    }
    }
    private String generateFileName(String originalPath, String suffix) {
        int dotIndex = originalPath.lastIndexOf('.');
        if (dotIndex != -1) {
            return originalPath.substring(0, dotIndex) + "_" + suffix + originalPath.substring(dotIndex);
        } else {
            return originalPath + "_" + suffix;
        }
    }
    
	@Override
	public SecretKey getSecretKey() {
		// TODO Auto-generated method stub
		return this.currentKey;
	}
	
	@Override
	public void setSecretKey(byte[] keyBytes) {
		this.currentKey = new SecretKeySpec(keyBytes, "DES");
	}
	
	@Override
	public void setMode(String mode) {
		this.mode = "/"+mode;
		
	}
	
	@Override
	public void setPadding(String padding) {
		this.padding ="/"+padding;
		
	}


    public static void main(String[] args) throws Exception {
        Des camellia = new Des();
        System.out.println("Key generated: " + camellia.genkey(56)); // Example with 128 bits key

        String src = "C:\\Users\\Trung Tri\\Documents\\test.txt";
        String path=camellia.encryptFile(src);
        System.out.println("Encrypted file: " +path );
        System.out.println("Decrypted file: " + camellia.decryptFile(path));
    }
    
}
