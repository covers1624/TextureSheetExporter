package net.covers1624.tsexporter.gui;

import net.covers1624.swing.IntFilter;
import net.covers1624.swing.LayoutHelper;
import net.covers1624.tsexporter.util.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by covers1624 on 13/11/2017.
 */
public class LoadTextureSheetGui extends JDialog {

    private JLabel fileLabel;
    private JTextField fileField;
    private JButton chooseFileButton;
    private JFileChooser fileChooser;
    private JLabel resolutionLabel;
    private JTextField resolutionField;
    private JLabel orLabel;
    private JSeparator separator;
    private JLabel rowsLabel;
    private JLabel columnsLabel;
    private JTextField rowsField;
    private JTextField columnsField;
    private JButton loadButton;

    private int resolution = -1;

    private int rows = -1;
    private int columns = -1;

    public LoadTextureSheetGui(TextureExporterGui parent) {
        super(parent, true);
        setTitle("Select texture sheet");
        setResizable(false);
        setMinimumSize(new Dimension(300, 200));

        fileLabel = new JLabel("File:");
        fileLabel.setPreferredSize(new Dimension(150, 20));
        fileField = new JTextField();
        fileField.setPreferredSize(new Dimension(150, 20));
        chooseFileButton = new JButton(UIManager.getIcon("FileView.fileIcon"));
        chooseFileButton.setFocusPainted(false);
        chooseFileButton.setPreferredSize(new Dimension(20, 20));
        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("png") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "PNG Files *.png";
            }
        });
        fileChooser.setDialogTitle("Select Texture Sheet to open");
        chooseFileButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int ret = fileChooser.showDialog(this, "Select");
            if (ret == JFileChooser.APPROVE_OPTION) {
                fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }));

        resolutionLabel = new JLabel("Texture Resolution:");
        resolutionLabel.setPreferredSize(new Dimension(110, 20));
        resolutionField = new JTextField("");
        resolutionField.setPreferredSize(new Dimension(35, 20));
        resolutionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    resolution = Integer.parseInt(resolutionField.getText());
                } catch (NumberFormatException ignored) {
                }
                if (isNullOrEmpty(resolutionField.getText())) {
                    resolution = -1;
                }
            }
        });
        addIntFilter(resolutionField);
        orLabel = new JLabel("Or:");
        orLabel.setPreferredSize(new Dimension(35, 20));
        separator = new JSeparator();
        separator.setPreferredSize(new Dimension(300, 1));
        rowsLabel = new JLabel("Rows:");
        rowsLabel.setPreferredSize(new Dimension(60, 20));
        columnsLabel = new JLabel("Columns:");
        columnsLabel.setPreferredSize(new Dimension(60, 20));
        rowsField = new JTextField();
        rowsField.setPreferredSize(new Dimension(35, 20));
        rowsField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    rows = Integer.parseInt(rowsField.getText());
                } catch (NumberFormatException ignored) {
                }
                if (isNullOrEmpty(rowsField.getText())) {
                    rows = -1;
                }
            }
        });
        addIntFilter(rowsField);
        columnsField = new JTextField();
        columnsField.setPreferredSize(new Dimension(35, 20));
        columnsField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    columns = Integer.parseInt(columnsField.getText());
                } catch (NumberFormatException ignored) {
                }
                if (isNullOrEmpty(columnsField.getText())) {
                    columns = -1;
                }
            }
        });
        addIntFilter(columnsField);
        loadButton = new JButton("Load");
        loadButton.setPreferredSize(new Dimension(70, 20));
        loadButton.setFocusPainted(false);
        loadButton.addActionListener(event -> {
            File file = new File(fileField.getText());
            if (!file.isFile()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Selected file is not a file."));
                return;
            }
            if (!file.exists()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Selected file does not exist."));
                return;
            }
            if (resolution == -1 && rows == -1 && columns == -1) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You must specify either a resolution or rows & columns."));
            } else {
                if (resolution != -1) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            BufferedImage image = ImageIO.read(file);
                            int w = image.getWidth();
                            int h = image.getHeight();
                            parent.displayArea.loadImage(image, h / resolution, w / resolution);
                            this.dispose();
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Error loading image, see console for details.");
                        }
                    });
                } else {
                    if (rows == -1) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You cannot specify only columns and not rows."));
                        return;
                    }
                    if (columns == -1) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You cannot specify only rows and not columns."));
                        return;
                    }
                    try {
                        BufferedImage image = ImageIO.read(file);
                        parent.displayArea.loadImage(image, rows, columns);
                        this.dispose();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading image, see console for details.");
                    }
                }
            }
        });

        setLayout(new LayoutHelper(getPreferredSize(), p -> {
            int parentWidth = getRootPane().getWidth();
            int parentHeight = getRootPane().getHeight();

            //scratch fields.
            int xOff;
            int yOff;
            int seperatorY;
            Dimension s_fileLabel = fileLabel.getPreferredSize();
            Dimension s_fileField = fileField.getPreferredSize();
            Dimension s_chooseFileButton = chooseFileButton.getPreferredSize();
            Dimension s_resolutionLabel = resolutionLabel.getPreferredSize();
            Dimension s_resolutionField = resolutionField.getPreferredSize();
            Dimension s_orLabel = orLabel.getPreferredSize();
            Dimension s_separator = separator.getPreferredSize();
            Dimension s_rowsLabel = rowsLabel.getPreferredSize();
            Dimension s_columnsLabel = columnsLabel.getPreferredSize();
            Dimension s_rowsField = rowsField.getPreferredSize();
            Dimension s_columnsField = columnsField.getPreferredSize();
            Dimension s_loadButton = loadButton.getPreferredSize();

            fileLabel.setBounds(15, 10, s_fileLabel.width, s_fileLabel.height);
            fileField.setBounds(parentWidth / 2 - s_fileField.width / 2, 10, s_fileField.width, s_fileField.height);

            xOff = s_fileField.width / 2 + 10;
            yOff = 10 + (s_fileField.height / 2) - (s_chooseFileButton.height / 2);
            chooseFileButton.setBounds(parentWidth / 2 + xOff, yOff, s_chooseFileButton.width, s_chooseFileButton.height);

            resolutionLabel.setBounds(10, 10 * 2 + s_fileField.height, s_resolutionLabel.width, s_resolutionLabel.height);
            xOff = 10 * 2 + s_resolutionLabel.width;
            yOff = 10 * 2 + s_fileField.height;
            resolutionField.setBounds(xOff, yOff, s_resolutionField.width, s_resolutionField.height);

            yOff = 10 * 3 + s_fileField.height + s_resolutionLabel.height;
            orLabel.setBounds(10, yOff, s_orLabel.width, s_orLabel.height);
            seperatorY = 5 + 10 * 3 + s_fileField.height + s_resolutionLabel.height + s_orLabel.height;
            separator.setBounds(0, seperatorY, s_separator.width, s_separator.height);

            rowsLabel.setBounds(10, seperatorY + 5, s_rowsLabel.width, s_rowsLabel.height);
            columnsLabel.setBounds(10, seperatorY + 15 + s_rowsLabel.height, s_columnsLabel.width, s_columnsLabel.height);
            rowsField.setBounds(10 + 5 + s_rowsLabel.width, seperatorY + 5, s_rowsField.width, s_rowsField.height);
            columnsField.setBounds(10 + 5 + s_columnsLabel.width, seperatorY + 15 + s_rowsLabel.height, s_columnsField.width, s_columnsField.height);

            loadButton.setBounds(parentWidth - 10 - s_loadButton.width, parentHeight - 10 - s_loadButton.height, s_loadButton.width, s_loadButton.height);
        }));

        add(fileLabel);
        add(fileField);
        add(chooseFileButton);
        add(resolutionLabel);
        add(resolutionField);
        add(orLabel);
        add(separator);
        add(rowsLabel);
        add(columnsLabel);
        add(rowsField);
        add(columnsField);
        add(loadButton);

        pack();
        Utils.centerOnTheFuckingScreen(this);
    }

    private static void addIntFilter(JTextField field) {
        ((PlainDocument) field.getDocument()).setDocumentFilter(new IntFilter());
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
