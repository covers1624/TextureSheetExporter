package net.covers1624.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class InputShim implements MouseListener, KeyListener {

    private List<IInputHandler> handlers = new ArrayList<>();
    private boolean mouseHandled;
    private boolean keyHandled;

    public void addHandler(Object object) {
        if (object instanceof IInputHandler) {
            handlers.add((IInputHandler) object);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandled = false;
        handlers.forEach(h -> h.mousePressed(e));
        if (!mouseHandled) {
            for (IInputHandler h : handlers) {
                if (h.onMousePressed(e)) {
                    mouseHandled = true;
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handlers.forEach(h -> h.mouseReleased(e));
        if (!mouseHandled) {
            for (IInputHandler h : handlers) {
                if (h.onMouseReleased(e)) {
                    mouseHandled = true;
                    return;
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handlers.forEach(h -> h.mouseClicked(e));
        if (!mouseHandled) {
            for (IInputHandler h : handlers) {
                if (h.onMouseClicked(e)) {
                    mouseHandled = true;
                    return;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyHandled = false;
        handlers.forEach(h -> h.keyPressed(e));
        if (!keyHandled) {
            for (IInputHandler h : handlers) {
                if (h.onKeyPressed(e)) {
                    keyHandled = true;
                    return;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handlers.forEach(h -> h.keyReleased(e));
        if (!keyHandled) {
            for (IInputHandler h : handlers) {
                if (h.onKeyReleased(e)) {
                    keyHandled = true;
                    return;
                }
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        handlers.forEach(h -> h.keyTyped(e));
        if (!keyHandled) {
            for (IInputHandler h : handlers) {
                if (h.onKeyTyped(e)) {
                    keyHandled = true;
                    return;
                }
            }
        }
    }

    //@formatter:off
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    //@formatter:on
}
