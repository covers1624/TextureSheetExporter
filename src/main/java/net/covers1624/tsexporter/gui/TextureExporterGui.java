package net.covers1624.tsexporter.gui;

import net.covers1624.swing.GLLikeJPanel;
import net.covers1624.swing.IInputHandler;
import net.covers1624.swing.InputShim;
import net.covers1624.swing.LayoutHelper;
import net.covers1624.tsexporter.Launcher;
import net.covers1624.tsexporter.util.Rectangle4i;
import net.covers1624.tsexporter.util.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 10/11/2017.
 */
public class TextureExporterGui extends JFrame {

    public DisplayArea displayArea;

    public JPanel editPanel;
    public JLabel nameLabel;
    public JLabel imageLabel;
    public JLabel newNameLabel;
    public TextField newNameBox;
    public JButton exportButton;
    public JButton loadButton;

    public ConfirmExportGui confirmGui;
    public LoadTextureSheetGui loadGui;

    public ExportElement editingElement;

    public TextureExporterGui() {

        confirmGui = new ConfirmExportGui(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 500));

        displayArea = new DisplayArea();
        editPanel = new JPanel();
        nameLabel = new JLabel();
        nameLabel.setPreferredSize(new Dimension(100, 14));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(128, 182));
        newNameBox = new TextField();
        newNameBox.setPreferredSize(new Dimension(150, 20));
        newNameLabel = new JLabel("Enter file name here: ");
        newNameLabel.setPreferredSize(new Dimension(150, 20));
        exportButton = new JButton("Export");
        exportButton.setPreferredSize(new Dimension(100, 20));
        exportButton.setFocusPainted(false);
        loadButton = new JButton("Load");
        loadButton.setPreferredSize(new Dimension(100, 20));
        loadButton.setFocusPainted(false);

        setLayout(new LayoutHelper(new Dimension(500, 500), p -> {
            int parentWidth = getRootPane().getWidth();
            int parentHeight = getRootPane().getHeight();

            displayArea.setBounds(0, 150, parentWidth, parentHeight);
            editPanel.setBounds(0, 0, parentWidth, 150);
            {
                Dimension s = nameLabel.getPreferredSize();
                nameLabel.setBounds(5, 5, s.width, s.height);
            }
            {
                Dimension s = imageLabel.getPreferredSize();
                imageLabel.setBounds(5, 5, s.width, s.height);
            }
            {
                Dimension s = newNameBox.getPreferredSize();
                newNameBox.setBounds(200, 25, s.width, s.height);
            }
            {
                Dimension s = newNameLabel.getPreferredSize();
                newNameLabel.setBounds(200, 5, s.width, s.height);
            }
            {
                Dimension s = exportButton.getPreferredSize();
                exportButton.setBounds(parentWidth - s.width - 10, 150 - s.height - 10, s.width, s.height);
            }
            {
                Dimension s = loadButton.getPreferredSize();
                int yOff = 150 - s.height - 10 - exportButton.getPreferredSize().height - 10;
                loadButton.setBounds(parentWidth - s.width - 10, yOff, s.width, s.height);
            }
        }));
        editPanel.setLayout(new LayoutHelper(editPanel.getPreferredSize(), e -> {
        }));
        newNameBox.addTextListener(e -> {
            if (editingElement != null) {
                editingElement.fileName = newNameBox.getText();
            }
        });
        exportButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            if (!displayArea.exportElements.stream().anyMatch(el -> el.export)) {
                JOptionPane.showMessageDialog(this, "You must select elements to export first.");
                return;
            }
            confirmGui.setElements(displayArea.exportElements.stream().filter(el -> el.export));
            confirmGui.setVisible(true);
        }));
        loadButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (!displayArea.exportElements.isEmpty()) {
                    String msg = "You currently have a texture sheet open.\nYou will loose all progress if you continue.\nDo you wish to continue?";
                    int ret = JOptionPane.showConfirmDialog(this, msg, "Progress will be lost", JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                Launcher.addRunnable(() -> loadGui.setVisible(true));
            });
        });

        add(displayArea);
        add(editPanel);
        editPanel.add(nameLabel);
        editPanel.add(imageLabel);
        editPanel.add(newNameBox);
        editPanel.add(newNameLabel);
        editPanel.add(exportButton);
        editPanel.add(loadButton);
        pack();
        Utils.centerOnTheFuckingScreen(this);
        setTitle("Texture Exporter");
        setVisible(true);
        SwingUtilities.invokeLater(new Thread("Render Thread") {
            @Override
            public void run() {
                while (true) {
                    if (!isVisible()) {
                        return;
                    }
                    update();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }::start);
        newNameBox.setVisible(false);
        newNameLabel.setVisible(false);
        loadGui = new LoadTextureSheetGui(this);
        Launcher.addRunnable(() -> loadGui.setVisible(true));
        //displayArea.loadImage(readImage("/enderpouch.png"), 16, 16);
        //displayArea.loadImage(readImage("/tex.png"), 2, 64);
    }

    public void update() {
        repaint();
    }

    public void setEditingElement(ExportElement newEditing) {
        if (newEditing == null || newEditing.editing) {
            if (newEditing != null) {
                newEditing.editing = false;
            }
            editingElement = null;
            nameLabel.setText("");
            imageLabel.setIcon(null);
            newNameBox.setText("");
            newNameBox.setVisible(false);
            newNameLabel.setVisible(false);
            return;
        }
        if (editingElement != null) {
            editingElement.editing = false;
        }
        editingElement = newEditing;
        editingElement.editing = true;
        nameLabel.setText("Editing: " + editingElement.posName);
        imageLabel.setIcon(new ImageIcon(scaleImage(editingElement.image, 128, 128)));
        newNameBox.setText(editingElement.fileName);
        newNameBox.setVisible(true);
        newNameLabel.setVisible(true);
    }

    public void doExport() {
        confirmGui.dispose();

        JDialog dialog = DoingStuffDialog.create("Exporting", this, "Currently exporting, be patient.");
        Launcher.addRunnable(() -> {
            File folder = confirmGui.getExportFolder();
            displayArea.exportElements.stream().filter(e -> e.export).forEach(e -> {
                File file = new File(folder, e.fileName + ".png");
                saveImage(e.image, file);
            });
            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    public class DisplayArea extends GLLikeJPanel implements MouseMotionListener, MouseWheelListener, IInputHandler {

        //Context
        public final InputShim inputShim = new InputShim();
        public final List<ExportElement> exportElements = new ArrayList<>();
        public Point center;
        public Point prevSize = new Point(0, 0);
        public double scale = 1;

        //Mouse related stuff
        public int pressedButton;
        public Point centerClick;
        public Point mouseClick;
        public boolean dragging;

        public DisplayArea() {
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            inputShim.addHandler(this);
            addMouseListener(inputShim);
            addKeyListener(inputShim);
        }

        public void loadImage(BufferedImage image, int rows, int columns) {
            synchronized (exportElements) {
                exportElements.clear();
                setEditingElement(null);
            }
            int offset = 8;
            List<ExportElement> newElements = new ArrayList<>();
            //File dumps = new File("dump");
            int xSize = image.getWidth() / columns;
            int ySize = image.getHeight() / rows;
            System.out.println("Sheet size: " + image.getWidth() + "/" + image.getHeight());
            System.out.println("Sprite size: " + xSize + "/" + ySize);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    //File dumpFile = new File(dumps, row + "_" + col + ".png");
                    int x1 = xSize * col;
                    int x2 = x1 + xSize;
                    int y1 = ySize * row;
                    int y2 = y1 + ySize;
                    int xOff = offset * col;
                    int yOff = offset * row;
                    BufferedImage cut = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics g = cut.getGraphics();
                    g.drawImage(image, 0, 0, xSize, ySize, x1, y1, x2, y2, null);
                    g.dispose();
                    //saveImage(cut, dumpFile);
                    int px = x1 + xOff - ((image.getWidth() + (offset * (columns - 1))) / 2);
                    int py = y1 + yOff - ((image.getHeight() + (offset * (rows - 1))) / 2);
                    newElements.add(new ExportElement(cut, row + "_" + col, px, py));
                }
            }
            newElements.forEach(inputShim::addHandler);
            synchronized (exportElements) {
                exportElements.addAll(newElements);
            }
        }

        @Override
        public void paintComponent(Graphics g1) {
            super.paintComponent(g1);
            Graphics2D g = setGraphics(g1);
            g.setBackground(Color.DARK_GRAY);
            Dimension size = getSize();
            g.clearRect(0, 0, size.width, size.height);

            pushMatrix();

            g.translate(center.x, center.y);
            g.scale(scale, scale);

            synchronized (exportElements) {
                exportElements.forEach(element -> {
                    pushMatrix();
                    int e_xCenter = (element.rec.w / 2);
                    int e_yCenter = (element.rec.h / 2);
                    g.translate(-e_xCenter, -e_yCenter);
                    element.draw(g);
                    popMatrix();
                });
            }

            popMatrix();

            g.setColor(Color.WHITE);
            g.drawString("Scale: " + scale, 5, 20);

            //Visualize hit boxes.
            g.setColor(Color.WHITE);
            if (false) {
                exportElements.forEach(e -> g.drawRect(e.getX(), e.getY(), e.getWidth(), e.getHeight()));
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            scale = Utils.clip(scale + (e.getWheelRotation() * -1) * 0.25, 0.25, 20);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            centerClick = new Point(center);
            mouseClick = e.getPoint();
            pressedButton = e.getButton();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pressedButton == 3) {
                dragging = true;
                center.x = -(mouseClick.x - e.getX()) + centerClick.x;
                center.y = -(mouseClick.y - e.getY()) + centerClick.y;
            }

        }

        @Override
        public boolean onMouseReleased(MouseEvent event) {
            if (dragging) {
                dragging = false;
                return true;
            }
            return false;
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            if (prevSize.x != width && prevSize.y != height) {
                prevSize = new Point(width, height);
                center = new Point(width / 2, height / 2);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    public class ExportElement extends Element implements IInputHandler {

        public String posName;
        public String fileName;
        public BufferedImage image;
        public boolean export;
        public boolean editing;

        public ExportElement(BufferedImage image, String s, int xOff, int yOff) {
            super(new Rectangle4i(xOff, yOff, image.getWidth(), image.getHeight()));
            this.image = image;
            posName = s;
            fileName = s;
        }

        @Override
        public void draw(Graphics2D graphics) {
            graphics.setColor(Color.CYAN);
            graphics.fillRect(rec.x, rec.y, rec.w, rec.h);
            graphics.drawImage(image, rec.x, rec.y, rec.w, rec.h, null);
            if (export || editing) {
                if (editing) {
                    graphics.setColor(Color.GREEN);
                } else {
                    graphics.setColor(Color.RED);
                }
                Stroke stroke = graphics.getStroke();
                graphics.setStroke(new BasicStroke(2));
                graphics.drawRect(rec.x - 2, rec.y - 2, rec.w + 4, rec.h + 4);
                graphics.setStroke(stroke);
            }
        }

        @Override
        public boolean onMouseReleased(MouseEvent event) {
            if (isMouseInside(event.getPoint())) {
                if (event.getButton() == 1) {
                    export = !export;
                    return true;
                } else if (event.getButton() == 3) {
                    setEditingElement(this);
                    return true;
                }
            }
            return false;
        }

        @Override
        public Rectangle4i getScreenSpaceRec() {
            AffineTransform transform = new AffineTransform();
            transform.translate(displayArea.center.x, displayArea.center.y);
            transform.scale(displayArea.scale, displayArea.scale);
            int e_xCenter = (rec.w / 2);
            int e_yCenter = (rec.h / 2);
            transform.translate(-e_xCenter, -e_yCenter);
            return rec.copy().apply(transform);
        }

        @Override
        public int getX() {
            return getScreenSpaceRec().x;
        }

        @Override
        public int getY() {
            return getScreenSpaceRec().y;
        }

        @Override
        public int getWidth() {
            return getScreenSpaceRec().w;
        }

        @Override
        public int getHeight() {
            return getScreenSpaceRec().h;
        }
    }

    public class Element {

        public Rectangle4i rec;
        public Element parent;

        public Element(Rectangle4i rec) {
            this.rec = rec;
        }

        public void draw(Graphics2D g) {
        }

        public int getX() {
            int x = rec.x;
            if (parent != null) {
                x += parent.getX();
            }
            return x;
        }

        public int getY() {
            int y = rec.y;
            if (parent != null) {
                y += parent.getY();
            }
            return y;
        }

        public int getWidth() {
            return rec.w;
        }

        public int getHeight() {
            return rec.h;
        }

        public boolean isMouseInside(Point mouse) {
            return getScreenSpaceRec().contains(mouse);
        }

        public Rectangle4i getScreenSpaceRec() {
            return rec;
        }
    }

    public static BufferedImage readImage(String resource) {
        try {
            return ImageIO.read(DisplayArea.class.getResourceAsStream(resource));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read image: " + resource, e);
        }
    }

    public static void saveImage(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "png", Utils.tryCreateFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, image.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return newImage;
    }

}
