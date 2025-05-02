package View;

import Controller.EncryptionController;

import javax.swing.*;
import java.awt.*;

public class EncryptionMainPanel extends JPanel {
    private TextEncryptionPanel textPanel;
    private FileEncryptionPanel filePanel;

    public EncryptionMainPanel(EncryptionController controller) {
        setLayout(new GridLayout(1, 2, 10, 10)); // Trái: Text, Phải: File
        textPanel = new TextEncryptionPanel(controller);
        filePanel = new FileEncryptionPanel(controller);

        add(textPanel);
        add(filePanel);
    }

}
