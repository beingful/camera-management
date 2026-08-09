package admin.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class UiSupport {
    private UiSupport() {
    }

    public static void showError(Component parent, RuntimeException exception) {
        String message = exception.getMessage() == null ? exception.toString() : exception.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSaved(Component parent) {
        JOptionPane.showMessageDialog(parent, "Saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
