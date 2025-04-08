package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EncryptionApp {
    private JFrame frame;
    private JComboBox<String> algorithmComboBox;
    private JButton generateKeyButton, loadKeyButton, encryptButton, loadFileButton;
    private JTextField inputField;
    private JTextArea outputArea;

    public EncryptionApp() {
        createUI();
    }

    private void createUI() {
        // Khởi tạo JFrame
        frame = new JFrame("Ứng dụng Mã Hóa");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Chọn thuật toán
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Chọn thuật toán:"), gbc);

        String[] algorithms = {"AES", "DES", "RSA"};
        algorithmComboBox = new JComboBox<>(algorithms);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        frame.add(algorithmComboBox, gbc);

        // Button tạo khóa
        generateKeyButton = new JButton("Gen Key");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        frame.add(generateKeyButton, gbc);

        // Button Load Key
        loadKeyButton = new JButton("Load Key");
        gbc.gridx = 3;
        gbc.gridy = 0;
        frame.add(loadKeyButton, gbc);

        // Input nhập dữ liệu
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Dữ liệu:"), gbc);

        inputField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(inputField, gbc);

        // Button tải file
        loadFileButton = new JButton("Load File");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(loadFileButton, gbc);

        // Button mã hóa
        encryptButton = new JButton("Mã Hóa");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(encryptButton, gbc);

        // Kết quả mã hóa
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        frame.add(new JLabel("Kết quả:"), gbc);

        outputArea = new JTextArea(10, 40); // Tăng chiều cao và chiều rộng
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;  // Đẩy xuống đáy
        gbc.fill = GridBagConstraints.BOTH; // Cho phép mở rộng
        frame.add(scrollPane, gbc);

        // Căn giữa màn hình
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Xử lý sự kiện
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptData();
            }
        });
    }

    private void encryptData() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String data = inputField.getText();
        if (data.isEmpty()) {
            outputArea.setText("Vui lòng nhập dữ liệu!");
            return;
        }
        outputArea.setText("[" + algorithm + "] " + data + " → Mã hóa thành công!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EncryptionApp::new);
    }
}
