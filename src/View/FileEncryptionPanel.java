package View;

import Controller.EncryptionController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileEncryptionPanel extends JPanel {
		private JComboBox<String> typeComboBox;
	    private JComboBox<String> SymmetricComboBox;
	    private JComboBox<String> AsymmetricComboBox;
	    private JComboBox<String> HashComboBox;
	    private JComboBox<Integer> AsymmetricKeysize;
	    private JComboBox<Integer> SymmetrickeySize;
	    private JButton generateKeyButton, loadKeyButton, encryptButton, decryptButton, loadFileButton;
	    private JTextField inputField;
	    private JTextArea outputArea, decryptedOutputArea, fileContentArea ;
	    private EncryptionController controller;
	    private JPanel cardPanel;
	    private CardLayout cardLayout;
	    private String selectedAlgo;
	    private String selectedType;
	    private int selectedKeySize;
	    private String selectedMode;
	    private String selectedPadding;

	    public FileEncryptionPanel(EncryptionController controller) {
	        this.controller = controller;
	        createUI();
	    }

	    private void createUI() {
	        setLayout(new BorderLayout(10, 10));
	        setBorder(BorderFactory.createTitledBorder("Mã hóa File"));
	        JPanel topPanel = new JPanel();
	        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
	        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
	        topPanel.add(createTypeSelectorPanel());

	        cardLayout = new CardLayout();
	        cardPanel = new JPanel(cardLayout);
	        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	        cardPanel.add(createSymmetricPanel(), "Symmetric");
	        cardPanel.add(createAsymmetricPanel(), "Asymmetric");
	        cardPanel.add(createHashPanel(), "Hash");

	        add(topPanel, BorderLayout.NORTH);
	        add(cardPanel, BorderLayout.CENTER);
	        add(createEncryptionPanel(), BorderLayout.SOUTH);
	    }

	    private JPanel createTypeSelectorPanel() {
	        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        panel.add(new JLabel("Chọn loại mã hóa:"));

	        String[] types = { "Symmetric", "Asymmetric", "Hash" };
	        typeComboBox = new JComboBox<>(types);
	        typeComboBox.addActionListener(e -> {
	            selectedType = (String) typeComboBox.getSelectedItem();
	            cardLayout.show(cardPanel, selectedType);
	        });

	        panel.add(typeComboBox);
	        return panel;
	    }

	    private JPanel createSymmetricPanel() {
	        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        String[] algorithms = new String[] {
	            "AES", "DES", "TwoFish", "3DES", "BlowFish", "Camellia", "RC5", "Cast_128"
	        };
	        panel.add(new JLabel("Chọn thuật toán:"));
	        
	        SymmetricComboBox = new JComboBox<>(algorithms);
	        SymmetricComboBox.addActionListener(e -> {
	            selectedAlgo = (String) SymmetricComboBox.getSelectedItem();
	            System.out.println(selectedAlgo);
	        });
	        panel.add(SymmetricComboBox);

	        SymmetrickeySize = new JComboBox<>(new Integer[] {
	            40, 56, 64, 80, 112,128, 160,168 ,192, 224, 256, 512, 1024, 2048
	        });
	        SymmetrickeySize.addActionListener(e -> {
	            selectedKeySize = (Integer) SymmetrickeySize.getSelectedItem();
	            System.out.println("Key Size Changed To: " + selectedKeySize);
	        });
	        panel.add(SymmetrickeySize);

	        // Thêm ComboBox cho chế độ (mode)
	        String[] modes = new String[] {
	            "ECB", "CBC", "CFB", "OFB", "CTR"
	        };
	        JComboBox<String> modeComboBox = new JComboBox<>(modes);
	        modeComboBox.addActionListener(e -> {
	            selectedMode = (String) modeComboBox.getSelectedItem();
	            System.out.println("Chế độ: " + selectedMode);
	        });
	        panel.add(new JLabel("Chế độ:"));
	        panel.add(modeComboBox);

	        // Thêm ComboBox cho padding
	        String[] paddings = new String[] {
	            "PKCS5Padding", "NoPadding", "ISO10126Padding"
	        };
	        JComboBox<String> paddingComboBox = new JComboBox<>(paddings);
	        paddingComboBox.addActionListener(e -> {
	            selectedPadding = (String) paddingComboBox.getSelectedItem();
	            System.out.println("Padding: " + selectedPadding);
	        });
	        panel.add(new JLabel("Padding:"));
	        panel.add(paddingComboBox);

	        // Nếu bạn có method xử lý nút thì gọi lại ở đây
	        createKeySizeAndButtons(panel, algorithms, true);

	        return panel;
	    }


	    private JPanel createAsymmetricPanel() {
	        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        String[] algorithms = new String[] { "RSA-AES","RSA-DES","RSA-3DES","RSA-BlowFish","RSA-TwoFish","RSA-RC5","RSA-Camellia" };
	        panel.add(new JLabel("Chọn thuật toán:"));
	        AsymmetricComboBox = new JComboBox<>(algorithms);
	        AsymmetricComboBox.addActionListener(e -> {
	            selectedAlgo = (String) AsymmetricComboBox.getSelectedItem();
	            System.out.println(selectedAlgo);
	        });
	        panel.add(AsymmetricComboBox);
	        AsymmetricKeysize = new JComboBox<>(new Integer[] { 512, 1024, 2048 });
	        AsymmetricKeysize.addActionListener(e->{
	        	selectedKeySize = (Integer) AsymmetricKeysize.getSelectedItem();
                System.out.println("Key Size Changed To: " + selectedKeySize);
	        });
	    
	        panel.add(AsymmetricKeysize);
	        // Thêm ComboBox cho mode (thực tế RSA thường dùng ECB, nhưng bạn vẫn có thể cho người dùng chọn)
	        String[] modes = new String[] {
	            "ECB" // RSA chủ yếu dùng ECB trong Java
	        };
	        JComboBox<String> modeComboBox = new JComboBox<>(modes);
	        modeComboBox.addActionListener(e -> {
	            selectedMode = (String) modeComboBox.getSelectedItem();
	            System.out.println("Chế độ: " + selectedMode);
	        });
	        panel.add(new JLabel("Chế độ:"));
	        panel.add(modeComboBox);

	        // Thêm ComboBox cho padding
	        String[] paddings = new String[] {
	            "PKCS1Padding", "OAEPPadding", "NoPadding"
	        };
	        JComboBox<String> paddingComboBox = new JComboBox<>(paddings);
	        paddingComboBox.addActionListener(e -> {
	            selectedPadding = (String) paddingComboBox.getSelectedItem();
	            System.out.println("Padding: " + selectedPadding);
	        });
	        panel.add(new JLabel("Padding:"));
	        panel.add(paddingComboBox);

	        createKeySizeAndButtons(panel, algorithms, true);

	        return panel;
	    }

	    private JPanel createHashPanel() {
	        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        String[] algorithms = new String[] { "MD5","SHA256" };
	        panel.add(new JLabel("Chọn thuật toán:"));
	        HashComboBox = new JComboBox<>(algorithms);
	        HashComboBox.addActionListener(e -> {
	            selectedAlgo = (String) HashComboBox.getSelectedItem();
	            System.out.println(selectedAlgo);
	        });
	        panel.add(HashComboBox);

	        createKeySizeAndButtons(panel, algorithms, false);

	        return panel;
	    }

	    private void createKeySizeAndButtons(JPanel panel, String[] algorithms, boolean showKeyControls) {
	        if (showKeyControls) {

	            generateKeyButton = new JButton("Gen Key");
	            generateKeyButton.addActionListener(e -> generateKeyAction());
	            panel.add(generateKeyButton);

	            loadKeyButton = new JButton("Load Key");
	            loadKeyButton.addActionListener(e -> loadKeyAction());
	            panel.add(loadKeyButton);
	        }
	    }

	    private JPanel createEncryptionPanel() {
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	        panel.add(createInputPanel());
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

	        decryptButton = new JButton("Giải Mã");
	        decryptButton.addActionListener(e -> {
	            try {
					decryptData();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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



	    private JPanel createInputPanel() {
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc

	        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        topRow.add(new JLabel("Dữ liệu:"));

	        inputField = new JTextField(30);
	        topRow.add(inputField);

	        loadFileButton = new JButton("Load file");
	        loadFileButton.addActionListener(e -> chooseFileAndLoadContent());
	        topRow.add(loadFileButton);

	        panel.add(topRow);

	        // Thêm vùng hiển thị nội dung file
	        fileContentArea = new JTextArea(5, 40); // 5 dòng, 40 ký tự
	        fileContentArea.setLineWrap(true);
	        fileContentArea.setWrapStyleWord(true);
	        fileContentArea.setEditable(false);

	        JScrollPane scrollPane = new JScrollPane(fileContentArea);
	        panel.add(scrollPane);

	        return panel;
	    }


	    private void encryptData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
	            IllegalBlockSizeException, BadPaddingException, Exception {
	        String data = inputField.getText();

	        if (data.isEmpty()) {
	            outputArea.setText("Vui lòng nhập dữ liệu!");
	            return;
	        }

	        String result = controller.encryptFile(selectedAlgo, data,selectedMode,selectedPadding);
	        outputArea.setText(result);
	    }

	    private void decryptData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
	            IllegalBlockSizeException, BadPaddingException, Exception {
	        String encryptedData = outputArea.getText().trim();

	        if (encryptedData.isEmpty()) {
	            decryptedOutputArea.setText("Vui lòng mã hóa trước!");
	            return;
	        }

	        String result = controller.decryptFile(selectedAlgo,encryptedData,selectedMode,selectedPadding);
	        decryptedOutputArea.setText(result);
	    }

	    private void generateKeyAction() {
	        System.out.println(selectedKeySize);
	        try {
	            boolean success = controller.genKey(selectedAlgo, selectedKeySize);
	            if (success) {
	                JOptionPane.showMessageDialog(this, "Key đã được tạo và lưu thành công!", "Thông báo",
	                        JOptionPane.INFORMATION_MESSAGE);
	            } else {
	                JOptionPane.showMessageDialog(this, "Tạo key thất bại (chưa lưu được vào file).", "Lỗi",
	                        JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this, "Lỗi tạo key: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    private void loadKeyAction() {
	        try {
	            controller.loadKey(selectedAlgo);

	            JOptionPane.showMessageDialog(this, "Key đã được tải thành công!", "Thông báo",
	                    JOptionPane.INFORMATION_MESSAGE);
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this, "Lỗi tải key: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    
	    private void chooseFileAndLoadContent() {
	        JFileChooser fileChooser = new JFileChooser();
	        int result = fileChooser.showOpenDialog(this);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File selectedFile = fileChooser.getSelectedFile();
	            inputField.setText(selectedFile.getAbsolutePath());

	            try {
	                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

	                // Kiểm tra kiểu MIME để xác định có phải file văn bản không
	                String mimeType = Files.probeContentType(selectedFile.toPath());

	                if (mimeType != null && mimeType.startsWith("text")) {
	                    // Nếu là file text, hiển thị nội dung lên TextArea
	                    fileContentArea.setText(new String(fileBytes, StandardCharsets.UTF_8));
	                } else {
	                    // Nếu là file nhị phân
	                    fileContentArea.setText("Đã chọn file nhị phân: " + selectedFile.getName() + "\nKích thước: " + fileBytes.length + " bytes");
	                }

	            } catch (IOException ex) {
	                JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }
}
