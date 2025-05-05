package Model.AsymmetryAlgorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Model.SymmetryAlgorithm.AES;
import Model.SymmetryAlgorithm.BlowFish;
import Model.SymmetryAlgorithm.Camellia;
import Model.SymmetryAlgorithm.Cast_128;
import Model.SymmetryAlgorithm.Des;
import Model.SymmetryAlgorithm.RC5;
import Model.SymmetryAlgorithm.SymmetryAlgorithm;
import Model.SymmetryAlgorithm.ThreeDES;
import Model.SymmetryAlgorithm.Twofish;


public class RSA implements AsymmetryAlgorithm {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String KEY_FOLDER       = "keys";
    private static final String PUBLIC_KEY_PATH  = KEY_FOLDER + "/public.txt";
    private static final String PRIVATE_KEY_PATH = KEY_FOLDER + "/private.txt";
    private final Map<String, SymmetryAlgorithm> symmetrics = new HashMap<>();
    SymmetryAlgorithm symmetricsAlgo;
    String encryptedFilePath ="";
    String encryptedKeyPath ="";
    public String mode ="";
    public  String padding ="";
    
    public RSA() {
        // khởi tạo map thuật toán đối xứng
        symmetrics.put("AES", new AES());
        symmetrics.put("BLOWFISH", new BlowFish());
        symmetrics.put("CAMELLIA", new Camellia());
        symmetrics.put("CAST_128", new Cast_128());
        symmetrics.put("DES", new Des());
        symmetrics.put("RC5", new RC5());
        symmetrics.put("3DES", new ThreeDES());
        symmetrics.put("TWOFISH", new Twofish());
        // tạo thư mục lưu key nếu chưa có
        File keyDir = new File(KEY_FOLDER);
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
    }

    @Override
    public boolean genKey(int size) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            return saveKeyToFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveKeyToFile() {
        try {
            // đảm bảo thư mục tồn tại
            Path keyDir = Paths.get(KEY_FOLDER);
            if (!Files.exists(keyDir)) {
                Files.createDirectories(keyDir);
            }
            if (publicKey == null || privateKey == null) return false;
            Files.write(Paths.get(PUBLIC_KEY_PATH), publicKey.getEncoded());
            Files.write(Paths.get(PRIVATE_KEY_PATH), privateKey.getEncoded());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void loadKey() throws Exception {
        byte[] publicBytes = Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
        byte[] privateBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicBytes);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
        publicKey = keyFactory.generatePublic(pubSpec);
        privateKey = keyFactory.generatePrivate(privSpec);
    }

    public String encrypt(String data) throws Exception {
        return encrypt(data.getBytes(StandardCharsets.UTF_8));
    }

    public String encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA" + mode + padding);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public String decrypt(String data) throws Exception {
        return decrypt(Base64.getDecoder().decode(data));
    }

    public String decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA" + mode + padding);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @Override
    public String encryptFile(String filePath, String algorithmName) throws Exception {
        algorithmName = algorithmName.trim().toUpperCase();
        if (!symmetrics.containsKey(algorithmName)) {
            throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algorithmName);
        }
        symmetricsAlgo = symmetrics.get(algorithmName);
        // sinh khóa đối xứng và mã hóa file
        symmetricsAlgo.genkey();
        this.encryptedFilePath = symmetricsAlgo.encryptFile(filePath);
        // mã hóa khóa đối xứng bằng RSA
        byte[] symKeyBytes = symmetricsAlgo.getSecretKey().getEncoded();
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedSymKey = rsaCipher.doFinal(symKeyBytes);
        // lưu khóa mã hóa
        this.encryptedKeyPath = generateFileName(filePath, algorithmName + "_encryptedKey.txt");
        Files.write(Paths.get(encryptedKeyPath), encryptedSymKey);
        return "Mã hóa hoàn tất:\n- " + encryptedFilePath + "\n- " + encryptedKeyPath;
    }

    @Override
    public String decryptFile(String algo) throws Exception {
        algo = algo.trim().toUpperCase();
        if (!symmetrics.containsKey(algo)) {
            throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algo);
        }
        symmetricsAlgo = symmetrics.get(algo);
        // đọc và giải khóa đối xứng
        byte[] encKeyBytes = Files.readAllBytes(Paths.get(encryptedKeyPath));
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decKeyBytes = rsaCipher.doFinal(encKeyBytes);
        symmetricsAlgo.setSecretKey(decKeyBytes);
        // giải mã file
        return symmetricsAlgo.decryptFile(encryptedFilePath);
    }

    private String generateFileName(String originalPath, String newName) {
        Path original = Paths.get(originalPath);
        String fileName = original.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String newFileName = (dotIndex != -1)
                ? fileName.substring(0, dotIndex) + "_" + newName + fileName.substring(dotIndex)
                : fileName + "_" + newName;
        Path parent = original.getParent();
        return (parent != null) ? parent.resolve(newFileName).toString() : newFileName;
    }

    @Override
    public void setMode(String mode) {
        this.mode = "/" + mode;
    }

    @Override
    public void setPadding(String padding) {
        this.padding = "/" + padding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSA rsa = (RSA) o;
        return PUBLIC_KEY_PATH.equals(rsa.PUBLIC_KEY_PATH)
                && PRIVATE_KEY_PATH.equals(rsa.PRIVATE_KEY_PATH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH);
    }

    public static void main(String[] args) {
        try {
            RSA rsa = new RSA();
            rsa.genKey(512);

            String filePath = "C:/Users/Trung Tri/Documents/test.txt";
            String algorithmName = "AES";

            String enc = rsa.encryptFile(filePath, algorithmName);
            System.out.println(enc);

            String dec = rsa.decryptFile(algorithmName);
            System.out.println(dec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
