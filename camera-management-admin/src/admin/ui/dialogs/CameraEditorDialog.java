package admin.ui.dialogs;

import admin.model.CameraEntry;
import admin.ui.UiSupport;
import admin.ui.frames.AdminFrame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CameraEditorDialog extends JDialog {
    private boolean saved;
    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField urlField;
    private final JTextField rateField;
    private final JTextField widthField;
    private final JTextField heightField;
    private final JTextField serviceCodeField;
    private final JTextField encodingField;

    public CameraEditorDialog(JFrame owner, CameraEntry camera) {
        super(owner, "Camera", true);

        idField = new JTextField(String.valueOf(camera.id), 20);
        nameField = new JTextField(camera.name, 20);
        urlField = new JTextField(camera.url, 32);
        rateField = new JTextField(String.valueOf(camera.rate), 20);
        widthField = new JTextField(String.valueOf(camera.width), 20);
        heightField = new JTextField(String.valueOf(camera.height), 20);
        serviceCodeField = new JTextField(String.valueOf(camera.streamingServiceCode), 20);
        encodingField = new JTextField(camera.encoding, 20);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> {
            try {
                apply(camera);
                saved = true;
                dispose();
            }
            catch (RuntimeException exception) {
                UiSupport.showError(this, exception);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dispose());

        setLayout(new BorderLayout());
        add(fields(), BorderLayout.CENTER);
        add(AdminFrame.buttonBar(saveButton, cancelButton), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() {
        return saved;
    }

    private JPanel fields() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));

        addRow(panel, 0, "Id", idField);
        addRow(panel, 1, "Name", nameField);
        addRow(panel, 2, "Connection URL", urlField);
        addRow(panel, 3, "Frame rate", rateField);
        addRow(panel, 4, "Width", widthField);
        addRow(panel, 5, "Height", heightField);
        addRow(panel, 6, "Streaming service code", serviceCodeField);
        addRow(panel, 7, "Encoding", encodingField);

        return panel;
    }

    private void addRow(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private void apply(CameraEntry camera) {
        camera.id = Integer.parseInt(idField.getText().trim());
        camera.name = requireText(nameField, "Name");
        camera.url = requireText(urlField, "Connection URL");
        camera.rate = Integer.parseInt(rateField.getText().trim());
        camera.width = Integer.parseInt(widthField.getText().trim());
        camera.height = Integer.parseInt(heightField.getText().trim());
        camera.streamingServiceCode = Integer.parseInt(serviceCodeField.getText().trim());
        camera.encoding = requireText(encodingField, "Encoding");
    }

    private String requireText(JTextField field, String name) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value;
    }
}
