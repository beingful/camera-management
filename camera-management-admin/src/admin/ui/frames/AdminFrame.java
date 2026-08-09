package admin.ui.frames;

import admin.config.AppPaths;
import admin.config.FileService;
import admin.ui.panels.CameraPanel;
import admin.ui.panels.MainPanel;
import admin.ui.panels.MotionPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

public class AdminFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel content;

    public AdminFrame() {
        super("Camera Management Admin");

        FileService fileService = new FileService();
        cardLayout = new CardLayout();
        content = new JPanel(cardLayout);

        MainPanel mainPanel = new MainPanel(this::showCameraPanel, this::showMotionPanel);
        CameraPanel cameraPanel = new CameraPanel(fileService, AppPaths.CAMERAS_FILE, this::showMainPanel);
        MotionPanel motionPanel = new MotionPanel(fileService, AppPaths.MOTION_FILE, this::showMainPanel);

        content.add(mainPanel, "main");
        content.add(cameraPanel, "camera");
        content.add(motionPanel, "motion");

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setInitialSize();
        setLocationRelativeTo(null);
        showMainPanel();
    }

    private void setInitialSize() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(new Dimension((int) (screen.width * 0.45), (int) (screen.height * 0.45)));
        setSize((int) (screen.width * 0.72), (int) (screen.height * 0.72));
    }

    private void showMainPanel() {
        cardLayout.show(content, "main");
    }

    private void showCameraPanel() {
        cardLayout.show(content, "camera");
    }

    private void showMotionPanel() {
        cardLayout.show(content, "motion");
    }

    public static JPanel buttonBar(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }
}
