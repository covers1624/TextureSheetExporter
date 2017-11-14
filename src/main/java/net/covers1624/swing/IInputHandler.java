package net.covers1624.swing;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by covers1624 on 11/11/2017.
 */
public interface IInputHandler {

    //Always fired, on mouse pressed.
    default void mousePressed(MouseEvent event) {
    }

    //return true to cancel all events after this.
    default boolean onMousePressed(MouseEvent event) {
        return false;
    }

    default void mouseReleased(MouseEvent event) {
    }

    //return true to cancel all events after this.
    default boolean onMouseReleased(MouseEvent event) {
        return false;
    }

    //Always fired, on clicked (pressed -> released)
    default void mouseClicked(MouseEvent event) {
    }

    //return true to cancel all events after this.
    default boolean onMouseClicked(MouseEvent event) {
        return false;
    }

    default void keyPressed(KeyEvent event) {
    }

    default boolean onKeyPressed(KeyEvent event) {
        return false;
    }

    default void keyReleased(KeyEvent event) {
    }

    default boolean onKeyReleased(KeyEvent event) {
        return false;
    }

    default void keyTyped(KeyEvent event) {
    }

    default boolean onKeyTyped(KeyEvent event) {
        return false;
    }

}
