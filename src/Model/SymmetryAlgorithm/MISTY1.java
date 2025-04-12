package Model.SymmetryAlgorithm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;

public class MISTY1 implements SymmetryAlgorithm {

    private SecretKey secretKey;
    private Cipher cipher;

    @Override
    public SecretKey genkey() throws NoSuchAlgorithmException {
        // Tạo một khóa ngẫu nhiên với thuật toán MISTY1
        // Lưu ý rằng MISTY1 không phải là thuật toán có sẵn trong Java mặc định
        // Do đó, bạn cần tìm hoặc sử dụng thư viện bên ngoài để hỗ trợ thuật toán này
        return null;
    }

    @Override
    public SecretKey genkey(int keySize) throws NoSuchAlgorithmException {
        // MISTY1 hỗ trợ kích thước khóa 128 bit
        if (keySize != 128) {
            throw new IllegalArgumentException("MISTY1 chỉ hỗ trợ khóa có kích thước 128 bit.");
        }
        // Tạo khóa với kích thước 128 bit cho thuật toán MISTY1
        return null;
    }

    @Override
    public void loadKey(SecretKey key) {
        // Tải khóa được cung cấp
        this.secretKey = key;
    }

    
    public byte[] encrypt(String text) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        // Mã hóa văn bản thành Base64 sử dụng thuật toán MISTY1
        cipher = Cipher.getInstance("MISTY1");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return encrypted;
    }
    
    @Override
 	public String encryptBase64(String text) throws InvalidKeyException, NoSuchAlgorithmException,
 			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
 		return Base64.getEncoder().encodeToString(encrypt(text));
 	}

 	public String decrypt(String data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
 			IllegalBlockSizeException, BadPaddingException, Exception {
 		return decryptBase64(data.getBytes());
 	}

    @Override
    public String decryptBase64(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception {
        // Giải mã dữ liệu đã mã hóa Base64 sử dụng thuật toán MISTY1
        cipher = Cipher.getInstance("MISTY1");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted);
    }

    @Override
    public boolean encryptFile(String srcf, String desf) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(srcf);
        FileOutputStream fos = new FileOutputStream(desf);
        cipher = Cipher.getInstance("MISTY1");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        cis.close();
        fos.close();
        return true;
    }

    @Override
    public boolean decryptFile(String srcf, String desf) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, Exception {
        FileInputStream fis = new FileInputStream(srcf);
        FileOutputStream fos = new FileOutputStream(desf);
        cipher = Cipher.getInstance("MISTY1");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            cos.write(buffer, 0, bytesRead);
        }
        cos.close();
        fis.close();
        fos.close();
        return true;
    }
}
