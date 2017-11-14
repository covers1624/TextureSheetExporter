package net.covers1624.tsexporter.gui;

import net.covers1624.tsexporter.util.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

/**
 * Created by covers1624 on 19/02/2017.
 */
public class DoingStuffDialog {

    public static JDialog create(String title, Frame parent, String... data) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setPreferredSize(new Dimension(100, 100));
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (String line : data) {
            builder.append("<br><center>");
            builder.append(line);
        }
        builder.append("</html>");
        JEditorPane pane = new JEditorPane("text/html", builder.toString());
        pane.setAutoscrolls(true);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.addHyperlinkListener(e -> {
            try {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                }
            } catch (Exception ignored) {
            }
        });
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.add(pane);
        dialog.pack();
        Utils.centerOnTheFuckingScreen(dialog);
        return dialog;
    }

}
