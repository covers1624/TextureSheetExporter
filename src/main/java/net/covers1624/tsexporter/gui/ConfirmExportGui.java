package net.covers1624.tsexporter.gui;

import net.covers1624.swing.LayoutHelper;
import net.covers1624.tsexporter.gui.TextureExporterGui.ExportElement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.stream.Stream;

/**
 * Created by covers1624 on 13/11/2017.
 */
public class ConfirmExportGui extends JDialog {

    private TextureExporterGui parent;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField outputDirectory;
    private JLabel directoryLabel;
    private JButton chooseFolderButton;
    private JFileChooser fileChooser;
    private JList<ExportElement> elementList;
    private DefaultListModel<ExportElement> listModel;
    private JScrollPane elementScrollPane;

    public ConfirmExportGui(TextureExporterGui parent) {
        super(parent, true);
        this.parent = parent;
        setTitle("Confirm Export");
        setResizable(false);
        setMinimumSize(new Dimension(300, 400));
        okButton = new JButton("Export!");
        okButton.setPreferredSize(new Dimension(80, 20));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            File file = getExportFolder();
            if (!file.isDirectory()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Invalid output directory."));
            } else {
                parent.doExport();
            }
        });
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 20));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> this.dispose());
        outputDirectory = new JTextField();
        outputDirectory.setPreferredSize(new Dimension(150, 20));
        directoryLabel = new JLabel("Output:");
        directoryLabel.setPreferredSize(new Dimension(150, 20));
        chooseFolderButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
        chooseFolderButton.setFocusPainted(false);
        chooseFolderButton.setPreferredSize(new Dimension(20, 20));
        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select a folder for exported textures");
        chooseFolderButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int ret = fileChooser.showDialog(this, "Select");
            if (ret == JFileChooser.APPROVE_OPTION) {
                outputDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }));
        elementList = new JList<>();
        listModel = new DefaultListModel<>();
        elementList.setModel(listModel);
        elementList.setCellRenderer(new CellRenderer());
        elementScrollPane = new JScrollPane(elementList);

        setLayout(new LayoutHelper(new Dimension(200, 400), p -> {
            int parentWidth = getRootPane().getWidth();
            int parentHeight = getRootPane().getHeight();
            int div3 = parentWidth / 3;
            {
                Dimension s = okButton.getPreferredSize();
                okButton.setBounds(div3 - (s.width / 2), parentHeight - s.height - 10, s.width, s.height);
            }
            {
                Dimension s = cancelButton.getPreferredSize();
                cancelButton.setBounds(div3 * 2 - (s.width / 2), parentHeight - s.height - 10, s.width, s.height);
            }
            {
                Dimension s = outputDirectory.getPreferredSize();
                outputDirectory.setBounds(parentWidth / 2 - s.width / 2, 10, s.width, s.height);
            }
            {
                Dimension s = directoryLabel.getPreferredSize();
                directoryLabel.setBounds(15, 10, s.width, s.height);
            }
            {
                Dimension s = chooseFolderButton.getPreferredSize();
                int xOff = outputDirectory.getPreferredSize().width / 2 + 10;
                int yOff = 10 + (outputDirectory.getPreferredSize().height / 2) - (s.height / 2);
                chooseFolderButton.setBounds(parentWidth / 2 + xOff, yOff, s.width, s.height);
            }
            {
                int yOff = 20 + outputDirectory.getPreferredSize().height;
                int width = parentWidth - 20;
                int height = parentHeight - yOff - 20 - okButton.getPreferredSize().height;
                elementScrollPane.setBounds(10, yOff, width, height);
            }
        }));

        add(okButton);
        add(cancelButton);
        add(outputDirectory);
        add(directoryLabel);
        add(chooseFolderButton);
        add(elementScrollPane);
    }

    public void setElements(Stream<ExportElement> elements) {
        listModel.removeAllElements();
        elements.forEach(listModel::addElement);
    }

    public File getExportFolder() {
        return new File(outputDirectory.getText());
    }

    public class CellRenderer implements ListCellRenderer<ExportElement> {

        @Override
        public Component getListCellRendererComponent(JList<? extends ExportElement> list, ExportElement value, int index, boolean isSelected, boolean cellHasFocus) {
            return new ElementPanel(value);
        }
    }

    public static class ElementPanel extends JPanel {

        private static final int ICON_WIDTH = 64;
        private static final int ICON_HEIGHT = 64;
        private static final int ELEMENT_OFFSET = 5;

        public ExportElement element;
        public JLabel icon;
        public JLabel posLabel;
        public JLabel nameLabel;

        public ElementPanel(ExportElement element) {
            this.element = element;
            setSize(ICON_HEIGHT + ELEMENT_OFFSET, ICON_HEIGHT + ELEMENT_OFFSET);//Not sure width matters here.
            icon = new JLabel(new ImageIcon(TextureExporterGui.scaleImage(element.image, ICON_WIDTH, ICON_HEIGHT)));
            posLabel = new JLabel("Pos: " + element.posName);
            nameLabel = new JLabel("Name: " + element.fileName);

            setLayout(new LayoutHelper(getSize(), p -> {
                icon.setBounds(ELEMENT_OFFSET, ELEMENT_OFFSET, ICON_WIDTH, ICON_HEIGHT);
                int textHeight = 20;
                int textWidth = 100;
                int h = ICON_HEIGHT + ELEMENT_OFFSET * 2;
                int hdiv3 = h / 3;
                int xOff = ELEMENT_OFFSET * 2 + ICON_WIDTH;
                int yOffA = hdiv3 - (textHeight / 2);
                int yOffB = hdiv3 * 2 - (textHeight / 2);
                posLabel.setBounds(xOff, yOffA, textWidth, textHeight);
                nameLabel.setBounds(xOff, yOffB, textWidth, textHeight);
            }));

            add(icon);
            add(posLabel);
            add(nameLabel);
        }
    }
}
