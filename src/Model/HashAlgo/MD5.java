package Model.HashAlgo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 implements HashAlgo{

    public String hash(String input) {
        return hash(input.getBytes());
    }

    public String hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(data);
            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public String hashFile(String src, String des) {
        File file = new File(src);

        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] digest = md.digest();
            String hashResult = bytesToHex(digest);

            // Ghi hash vào file des (nếu có)
            if (des != null && !des.isEmpty()) {
                File outFile = new File(des);
                outFile.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                    writer.write(hashResult);
                }
            }

            return hashResult;

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing file", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
