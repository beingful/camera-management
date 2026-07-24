package admin.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MainPanel extends JPanel {
    public MainPanel(Runnable openCamera, Runnable openMotion) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel actions = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(12, 12, 12, 12);
        constraints.fill = GridBagConstraints.NONE;

        JButton cameraButton = optionButton("Camera");
        cameraButton.addActionListener(event -> openCamera.run());

        JButton motionButton = optionButton("<html><center>Motion<br>Detection</center></html>");
        motionButton.addActionListener(event -> openMotion.run());

        constraints.gridx = 0;
        actions.add(cameraButton, constraints);
        constraints.gridx = 1;
        actions.add(motionButton, constraints);

        add(actions, BorderLayout.CENTER);
    }

    private JButton optionButton(String label) {
        JButton button = new JButton(label);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 18f));
        Dimension size = new Dimension(190, 160);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        return button;
    }
}
