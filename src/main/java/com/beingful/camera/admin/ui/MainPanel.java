package com.beingful.camera.admin.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MainPanel extends JPanel {
    public MainPanel(Runnable openCamera, Runnable openMotion) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel title = new JLabel("Camera Management Admin");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(12, 12, 12, 12);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JButton cameraButton = new JButton("Camera");
        cameraButton.addActionListener(event -> openCamera.run());

        JButton motionButton = new JButton("Motion");
        motionButton.addActionListener(event -> openMotion.run());

        constraints.gridx = 0;
        actions.add(cameraButton, constraints);
        constraints.gridx = 1;
        actions.add(motionButton, constraints);

        add(actions, BorderLayout.CENTER);
    }
}
