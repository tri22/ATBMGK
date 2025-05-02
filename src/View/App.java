package View;

import Controller.EncryptionController;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EncryptionController controller = new EncryptionController();
            EncryptionMainPanel mainPanel = new EncryptionMainPanel(controller);

            // Tạo cửa sổ chính
            JFrame frame = new JFrame("Ứng dụng Mã Hóa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null); // Căn giữa màn hình

            // Đặt panel chính vào frame
            frame.setContentPane(mainPanel);

    

            frame.setVisible(true);
        });
    }
}
