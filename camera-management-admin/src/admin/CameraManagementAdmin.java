package admin;

import admin.ui.frames.AdminFrame;

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
