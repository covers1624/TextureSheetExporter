package net.covers1624.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Stack;

/**
 * Created by covers1624 on 11/11/2017.
 */
public class GLLikeJPanel extends JComponent {

    protected Graphics2D g;
    private Stack<AffineTransform> matrixStack = new Stack<>();

    protected Graphics2D setGraphics(Graphics g) {
        return this.g = (Graphics2D) g;
    }

    public void pushMatrix() {
        matrixStack.push(g.getTransform());
    }

    public void popMatrix() {
        g.setTransform(matrixStack.pop());
    }

}
