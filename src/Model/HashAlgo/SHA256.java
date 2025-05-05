package Model.HashAlgo;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;

public class SHA256 implements HashAlgo {

    @Override
    public String hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data.getBytes("UTF-8"));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    @Override
    public String hashFile(String src) {
        try {
            byte[] fileBytes = Files.readAllBytes(new File(src).toPath());
            return hash(new String(fileBytes));
        } catch (IOException e) {
            throw new RuntimeException("File hashing error", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
