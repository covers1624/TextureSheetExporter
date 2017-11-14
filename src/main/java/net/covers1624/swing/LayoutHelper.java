package net.covers1624.swing;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Created by covers1624 on 12/11/2017.
 */
public class LayoutHelper implements LayoutManager {

    private final Dimension preferredSize;
    private final Consumer<Container> layoutConsumer;

    public LayoutHelper(Dimension preferredSize, Consumer<Container> layoutConsumer) {
        this.preferredSize = preferredSize;
        this.layoutConsumer = layoutConsumer;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return preferredSize;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public void layoutContainer(Container parent) {
        layoutConsumer.accept(parent);
    }
}
