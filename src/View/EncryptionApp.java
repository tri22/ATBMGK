package View;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import Controller.EncryptionController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptionApp {
    private JFrame frame;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> keySizeComboBox;
    private JButton generateKeyButton, loadKeyButton, encryptButton, decryptButton, loadFileButton;
    private JTextField inputField;
    private JTextArea outputArea, decryptedOutputArea;
    private EncryptionController controller;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public EncryptionApp() {
        controller = new EncryptionController();
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Ứng dụng Mã Hóa");
        frame.setSize(750, 600);  // Tăng kích thước frame để chứa thêm các phần tử
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.add(createTypeSelectorPanel());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        cardPanel.add(createSymmetricPanel(), "Đối xứng");
        cardPanel.add(createAsymmetricPanel(), "Bất đối xứng");
        cardPanel.add(createClassicalPanel(), "Cổ điển");

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createTypeSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Chọn loại mã hóa:"));

        String[] types = {"Đối xứng", "Bất đối xứng", "Cổ điển"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.addActionListener(e -> {
            String selected = (String) typeComboBox.getSelectedItem();
            cardLayout.show(cardPanel, selected);
        });

        panel.add(typeComboBox);
        return panel;
    }

    private JPanel createSymmetricPanel() {
        return createEncryptionPanel(new String[]{
                "AES", "DES", "TwoFish", "3DES", "BlowFish", "Anubis", "Camellia", "RC5", "Cast_128", "MISTY1"
        }, true, true);
    }

    private JPanel createAsymmetricPanel() {
        return createEncryptionPanel(new String[]{
                "RSA"
        }, true, true);
    }

    private JPanel createClassicalPanel() {
        return createEncryptionPanel(new String[]{
                "Ceasar", "Hill", "Vigenere", "Substitution", "Affine", "MD5"
        }, true, false);  // Thêm Gen Key cho các thuật toán cổ điển
    }

    private JPanel createEncryptionPanel(String[] algorithms, boolean showKeyControls, boolean showLoadFile) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createAlgorithmPanel(algorithms, showKeyControls));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createInputPanel(showLoadFile));
        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createSideBySideIOPanel());



        return panel;
    }
    private JPanel createSideBySideIOPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // --- Panel Mã Hóa ---
        JPanel encryptPanel = new JPanel();
        encryptPanel.setLayout(new BorderLayout(5, 5));
        encryptPanel.setBorder(BorderFactory.createTitledBorder("Mã Hóa"));

        encryptButton = new JButton("Mã Hóa");
        encryptButton.addActionListener(e -> {
            try {
                encryptData();
            } catch (Exception e1) {
                outputArea.setText("Lỗi khi mã hóa: " + e1.getMessage());
                e1.printStackTrace();
            }
        });
        outputArea = new JTextArea(10, 30);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);

        encryptPanel.add(encryptButton, BorderLayout.NORTH);
        encryptPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // --- Panel Giải Mã ---
        JPanel decryptPanel = new JPanel();
        decryptPanel.setLayout(new BorderLayout(5, 5));
        decryptPanel.setBorder(BorderFactory.createTitledBorder("Giải Mã"));

        JButton decryptButton = new JButton("Giải Mã");
        decryptButton.addActionListener(e -> {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            try {
                String decrypted = controller.decrypt(algorithm, outputArea.getText());
                decryptedOutputArea.setText(decrypted);
            } catch (Exception ex) {
                decryptedOutputArea.setText("Lỗi khi giải mã: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        decryptedOutputArea = new JTextArea(10, 30);
        decryptedOutputArea.setLineWrap(true);
        decryptedOutputArea.setWrapStyleWord(true);
        decryptedOutputArea.setEditable(false);

        decryptPanel.add(decryptButton, BorderLayout.NORTH);
        decryptPanel.add(new JScrollPane(decryptedOutputArea), BorderLayout.CENTER);

        // Add cả 2 panel vào giao diện ngang
        mainPanel.add(encryptPanel);
        mainPanel.add(decryptPanel);

        return mainPanel;
    }



    private JPanel createAlgorithmPanel(String[] algorithms, boolean showKeyControls) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Chọn thuật toán:"));
        algorithmComboBox = new JComboBox<>(algorithms);
        panel.add(algorithmComboBox);

        if (showKeyControls) {
            panel.add(new JLabel("Key Size:"));
            keySizeComboBox = new JComboBox<>();
            panel.add(keySizeComboBox);

            algorithmComboBox.addActionListener(e -> updateKeySizeOptions());
            updateKeySizeOptions();

            generateKeyButton = new JButton("Gen Key");
            generateKeyButton.addActionListener(e -> generateKeyAction());
            panel.add(generateKeyButton);

            if (!algorithms[0].equals("Ceasar")) {
                loadKeyButton = new JButton("Load Key");
                loadKeyButton.addActionListener(e -> loadKeyAction());
                panel.add(loadKeyButton);
            }
        }

        return panel;
    }

    private JPanel createInputPanel(boolean showLoadFile) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Dữ liệu:"));
        inputField = new JTextField(30);
        panel.add(inputField);

        if (showLoadFile) {
            loadFileButton = new JButton("Load File");
            panel.add(loadFileButton);
        }

        return panel;
    }


    private JPanel createOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Kết quả"));

        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDecryptButtonPanel() {
        JPanel panel = new JPanel();
        decryptButton = new JButton("Giải Mã");
        panel.add(decryptButton);

        decryptButton.addActionListener(e -> {
            try {
                decryptData();
            } catch (Exception e1) {
                decryptedOutputArea.setText("Lỗi khi giải mã: " + e1.getMessage());
                e1.printStackTrace();
            }
        });

        return panel;
    }

    private JPanel createDecryptedOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Kết quả Giải Mã"));

        decryptedOutputArea = new JTextArea(10, 40);
        decryptedOutputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(decryptedOutputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateKeySizeOptions() {
        if (keySizeComboBox == null) return;

        keySizeComboBox.removeAllItems();
        String algo = (String) algorithmComboBox.getSelectedItem();

        switch (algo) {
            case "AES":
            case "TwoFish":
            case "Camellia":
                keySizeComboBox.addItem("128");
                keySizeComboBox.addItem("192");
                keySizeComboBox.addItem("256");
                break;
            case "DES":
                keySizeComboBox.addItem("56");
                break;
            case "3DES":
                keySizeComboBox.addItem("112");
                keySizeComboBox.addItem("168");
                break;
            case "RSA":
                keySizeComboBox.addItem("1024");
                keySizeComboBox.addItem("2048");
                keySizeComboBox.addItem("4096");
                break;
            case "RC5":
            case "BlowFish":
                keySizeComboBox.addItem("64");
                keySizeComboBox.addItem("128");
                keySizeComboBox.addItem("256");
                break;
            case "Anubis":
                keySizeComboBox.addItem("128");
                keySizeComboBox.addItem("160");
                keySizeComboBox.addItem("192");
                keySizeComboBox.addItem("224");
                keySizeComboBox.addItem("256");
                keySizeComboBox.addItem("288");
                keySizeComboBox.addItem("320");
                break;
            case "CAST_128":
                keySizeComboBox.addItem("40");
                keySizeComboBox.addItem("80");
                keySizeComboBox.addItem("128");
                break;
            default:
                keySizeComboBox.addItem("Không áp dụng");
        }
    }

    private void encryptData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, Exception {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String data = inputField.getText();

        if (data.isEmpty()) {
            outputArea.setText("Vui lòng nhập dữ liệu!");
            return;
        }

        String result = controller.encrypt(algorithm, data);
        outputArea.setText(result);
    }

    private void decryptData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, Exception {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String encryptedData = outputArea.getText();  // Lấy dữ liệu đã mã hóa từ ô outputArea

        if (encryptedData.isEmpty()) {
            decryptedOutputArea.setText("Vui lòng mã hóa trước!");
            return;
        }

        String result = controller.decrypt(algorithm, encryptedData);  // Giải mã dữ liệu
        decryptedOutputArea.setText(result);  // Hiển thị kết quả giải mã
    }

    private void generateKeyAction() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        int keySize = -1;

        if (keySizeComboBox != null && keySizeComboBox.getSelectedItem() != null) {
            try {
                keySize = Integer.parseInt((String) keySizeComboBox.getSelectedItem());
            } catch (NumberFormatException e) {
                keySize = -1;
            }
        }

        try {
            boolean success = controller.genKey(algorithm, keySize);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Key đã được tạo và lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Tạo key thất bại (chưa lưu được vào file).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Lỗi tạo key: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadKeyAction() {
        // Xử lý Load Key nếu có (cho các thuật toán đối xứng và bất đối xứng)
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        try {
            controller.loadKey(algorithm);
            
            JOptionPane.showMessageDialog(frame, "Key đã được tải thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Lỗi tải key: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EncryptionApp::new);
    }
}
