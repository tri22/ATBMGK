package Model.HashAlgo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Model.SymmetryAlgorithm.Camellia;

public class MD5 implements HashAlgo {
	public String encrypt_path = "";

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

	public String hashFile(String src) {
		this.encrypt_path = generateFileName(src, "encrypt");
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
			if (encrypt_path != null && !encrypt_path.isEmpty()) {
				File outFile = new File(encrypt_path);
				outFile.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
					writer.write(hashResult);
				}
			}
			hashResult += "/n Xem chi tiết ở file: ";
			hashResult +=encrypt_path;
			return hashResult;

		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException("Error while hashing file", e);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private String generateFileName(String originalPath, String suffix) {
		int dotIndex = originalPath.lastIndexOf('.');
		if (dotIndex != -1) {
			return originalPath.substring(0, dotIndex) + "_" + suffix + originalPath.substring(dotIndex);
		} else {
			return originalPath + "_" + suffix;
		}
	}

}
