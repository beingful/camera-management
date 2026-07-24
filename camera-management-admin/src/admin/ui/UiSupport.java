package admin.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

final class UiSupport {
    private UiSupport() {
    }

    static void showError(Component parent, RuntimeException exception) {
        String message = exception.getMessage() == null ? exception.toString() : exception.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void showSaved(Component parent) {
        JOptionPane.showMessageDialog(parent, "Saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
