/*
 * (C) 2017 covers1624
 * All Rights Reserved
 */
package net.covers1624.tsexporter.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Random utilities.
 * Feel free to ignore the license on these :D
 * Created by covers1624 on 23/10/2017.
 */
public class Utils {

    /**
     * Attempts to create a file and all parent directories.
     *
     * @param file The file.
     * @return The same file.
     */
    public static File tryCreateFile(File file) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Unable to create new file.", e);
        }
    }

    public static void centerOnTheFuckingScreen(Component component) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();

        int centerX = (int) centerPoint.getX();
        int centerY = (int) centerPoint.getY();
        component.setLocation(centerX - (component.getWidth() / 2), Math.max(0, centerY - (component.getHeight() / 2)));
    }

    public static double clip(double value, double min, double max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }
}
