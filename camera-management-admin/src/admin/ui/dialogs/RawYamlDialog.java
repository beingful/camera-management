package admin.ui.dialogs;

import admin.config.FileService;
import admin.ui.UiSupport;
import admin.ui.frames.AdminFrame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;

public class RawYamlDialog extends JDialog {
    public RawYamlDialog(JFrame owner, FileService fileService, Path path, Runnable onSaved) {
        super(owner, "Raw YAML - " + path.getFileName(), true);

        JTextArea textArea = new JTextArea(fileService.read(path));
        textArea.setTabSize(2);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> {
            try {
                fileService.write(path, textArea.getText());
                onSaved.run();
                UiSupport.showSaved(this);
                dispose();
            }
            catch (RuntimeException exception) {
                UiSupport.showError(this, exception);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dispose());

        JPanel buttons = AdminFrame.buttonBar(saveButton, cancelButton);

        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setSize(relativeSize(owner));
        setLocationRelativeTo(owner);
    }

    private Dimension relativeSize(JFrame owner) {
        Dimension base = owner == null || owner.getWidth() == 0 || owner.getHeight() == 0
                ? Toolkit.getDefaultToolkit().getScreenSize()
                : owner.getSize();
        return new Dimension((int) (base.width * 0.82), (int) (base.height * 0.82));
    }
}
