package admin.ui;

import admin.config.FileService;
import admin.config.MotionYamlService;
import admin.model.MotionConfig;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;

public class MotionPanel extends JPanel {
    private final FileService fileService;
    private final MotionYamlService yamlService;
    private final Path path;
    private final JTextField historyField;
    private final JTextField varThresholdField;
    private final JCheckBox detectShadowsField;
    private final JTextField learningRateField;
    private final JTextField minimumMotionAreaField;
    private final JTextField motionEndFrameCountField;

    public MotionPanel(FileService fileService, Path path, Runnable back) {
        this.fileService = fileService;
        this.path = path;
        this.yamlService = new MotionYamlService();
        this.historyField = new JTextField(16);
        this.varThresholdField = new JTextField(16);
        this.detectShadowsField = new JCheckBox();
        this.learningRateField = new JTextField(16);
        this.minimumMotionAreaField = new JTextField(16);
        this.motionEndFrameCountField = new JTextField(16);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Motion detection");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);
        add(fields(), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> back.run());

        JButton rawButton = new JButton("Raw YAML");
        rawButton.addActionListener(event -> new RawYamlDialog(owner(), fileService, path, this::load).setVisible(true));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> save());

        add(AdminFrame.buttonBar(backButton, rawButton, saveButton), BorderLayout.SOUTH);

        load();
    }

    private JPanel fields() {
        JPanel panel = new JPanel(new GridBagLayout());
        addRow(panel, 0, "History", historyField);
        addRow(panel, 1, "Variance threshold", varThresholdField);
        addRow(panel, 2, "Detect shadows", detectShadowsField);
        addRow(panel, 3, "Learning rate", learningRateField);
        addRow(panel, 4, "Minimum motion area", minimumMotionAreaField);
        addRow(panel, 5, "Motion end frame count", motionEndFrameCountField);
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, java.awt.Component field) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(field, constraints);
    }

    private void load() {
        try {
            MotionConfig config = yamlService.parse(fileService.read(path));
            historyField.setText(String.valueOf(config.history));
            varThresholdField.setText(String.valueOf(config.varThreshold));
            detectShadowsField.setSelected(config.detectShadows);
            learningRateField.setText(String.valueOf(config.learningRate));
            minimumMotionAreaField.setText(String.valueOf(config.minimumMotionArea));
            motionEndFrameCountField.setText(String.valueOf(config.motionEndFrameCount));
        }
        catch (RuntimeException exception) {
            UiSupport.showError(this, exception);
        }
    }

    private void save() {
        try {
            MotionConfig config = new MotionConfig();
            config.history = Integer.parseInt(historyField.getText().trim());
            config.varThreshold = Double.parseDouble(varThresholdField.getText().trim());
            config.detectShadows = detectShadowsField.isSelected();
            config.learningRate = Double.parseDouble(learningRateField.getText().trim());
            config.minimumMotionArea = Double.parseDouble(minimumMotionAreaField.getText().trim());
            config.motionEndFrameCount = Integer.parseInt(motionEndFrameCountField.getText().trim());

            fileService.write(path, yamlService.write(config));
            UiSupport.showSaved(this);
        }
        catch (RuntimeException exception) {
            UiSupport.showError(this, exception);
        }
    }

    private JFrame owner() {
        return (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }
}
