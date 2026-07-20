package com.beingful.camera.admin;

import com.beingful.camera.admin.ui.AdminFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CameraManagementAdmin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ignored) {
            }

            AdminFrame frame = new AdminFrame();
            frame.setVisible(true);
        });
    }
}
