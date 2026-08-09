package admin.ui.panels;

import admin.config.CameraYamlService;
import admin.config.FileService;
import admin.model.CameraConfig;
import admin.model.CameraEntry;
import admin.ui.UiSupport;
import admin.ui.dialogs.CameraEditorDialog;
import admin.ui.dialogs.RawYamlDialog;
import admin.ui.frames.AdminFrame;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;

public class CameraPanel extends JPanel {
    private final FileService fileService;
    private final CameraYamlService yamlService;
    private final Path path;
    private final DefaultListModel<CameraEntry> cameraListModel;
    private final JList<CameraEntry> cameraList;
    private final JTextField storagePathField;
    private final JTextField extensionField;
    private CameraConfig config;

    public CameraPanel(FileService fileService, Path path, Runnable back) {
        this.fileService = fileService;
        this.path = path;
        this.yamlService = new CameraYamlService();
        this.cameraListModel = new DefaultListModel<>();
        this.cameraList = new JList<>(cameraListModel);
        this.storagePathField = new JTextField(42);
        this.extensionField = new JTextField(12);
        this.extensionField.setEditable(false);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Cameras");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);
        add(content(), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> back.run());

        JButton rawButton = new JButton("Raw YAML");
        rawButton.addActionListener(event -> new RawYamlDialog(owner(), fileService, path, this::load).setVisible(true));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> save());

        add(AdminFrame.buttonBar(backButton, rawButton, saveButton), BorderLayout.SOUTH);

        load();
    }

    private JPanel content() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(storagePanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(cameraList), BorderLayout.CENTER);
        panel.add(actions(), BorderLayout.EAST);
        return panel;
    }

    private JPanel storagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.LINE_END;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Storage path"), constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        panel.add(storagePathField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("File extension"), constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(extensionField, constraints);

        return panel;
    }

    private JPanel actions() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 4, 4, 4);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> addCamera());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(event -> editCamera());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(event -> deleteCamera());

        constraints.gridy = 0;
        panel.add(addButton, constraints);
        constraints.gridy = 1;
        panel.add(editButton, constraints);
        constraints.gridy = 2;
        panel.add(deleteButton, constraints);

        return panel;
    }

    private void load() {
        try {
            config = yamlService.parse(fileService.read(path));
            storagePathField.setText(config.storagePath);
            extensionField.setText(config.fileExtension);
            refreshList();
        }
        catch (RuntimeException exception) {
            UiSupport.showError(this, exception);
        }
    }

    private void save() {
        try {
            config.storagePath = storagePathField.getText().trim();
            fileService.write(path, yamlService.write(config));
            UiSupport.showSaved(this);
        }
        catch (RuntimeException exception) {
            UiSupport.showError(this, exception);
        }
    }

    private void addCamera() {
        CameraEntry camera = new CameraEntry();
        camera.id = nextId();
        camera.rate = 30;
        camera.width = 1280;
        camera.height = 720;
        camera.streamingServiceCode = 1900;
        camera.encoding = "MJPG";

        if (edit(camera)) {
            if (yamlService.hasDuplicateCameraKeys(config.cameras, camera)) {
                UiSupport.showError(this, new IllegalArgumentException("Camera id, name, and URL must be unique."));
                return;
            }

            config.cameras.add(camera);
            refreshList();
        }
    }

    private void editCamera() {
        CameraEntry camera = cameraList.getSelectedValue();
        if (camera == null) {
            return;
        }

        CameraEntry copy = copy(camera);
        if (edit(copy)) {
            config.cameras.remove(camera);
            boolean hasDuplicates = yamlService.hasDuplicateCameraKeys(config.cameras, copy);
            config.cameras.add(camera);

            if (hasDuplicates) {
                UiSupport.showError(this, new IllegalArgumentException("Camera id, name, and URL must be unique."));
                return;
            }

            copyInto(copy, camera);
            refreshList();
        }
    }

    private void deleteCamera() {
        CameraEntry camera = cameraList.getSelectedValue();
        if (camera == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Delete " + camera.name + "?", "Delete Camera",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            config.cameras.remove(camera);
            refreshList();
        }
    }

    private boolean edit(CameraEntry camera) {
        CameraEditorDialog dialog = new CameraEditorDialog(owner(), camera);
        dialog.setVisible(true);
        return dialog.isSaved();
    }

    private void refreshList() {
        cameraListModel.clear();
        for (CameraEntry camera : config.cameras) {
            cameraListModel.addElement(camera);
        }
    }

    private int nextId() {
        int id = 1;
        for (CameraEntry camera : config.cameras) {
            id = Math.max(id, camera.id + 1);
        }
        return id;
    }

    private CameraEntry copy(CameraEntry camera) {
        CameraEntry copy = new CameraEntry();
        copyInto(camera, copy);
        return copy;
    }

    private void copyInto(CameraEntry source, CameraEntry target) {
        target.id = source.id;
        target.name = source.name;
        target.url = source.url;
        target.rate = source.rate;
        target.width = source.width;
        target.height = source.height;
        target.streamingServiceCode = source.streamingServiceCode;
        target.encoding = source.encoding;
    }

    private JFrame owner() {
        return (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }
}
