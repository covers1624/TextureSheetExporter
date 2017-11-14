package net.covers1624.tsexporter;

import net.covers1624.tsexporter.gui.TextureExporterGui;
import net.covers1624.tsexporter.util.ThrowingRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 10/11/2017.
 */
public class Launcher {

    private static List<Runnable> runnables = new LinkedList<>();

    public static void main(String[] args) {
        TextureExporterGui gui = new TextureExporterGui();

        while (true) {
            if (!gui.isVisible()) {
                break;
            }
            if (!runnables.isEmpty()) {
                List<Runnable> toRun = synchronizedCopy(runnables, LinkedList::new);
                toRun.forEach(Runnable::run);
                synchronized (runnables) {
                    runnables.removeAll(toRun);
                }
            }
            tryQuietly(() -> Thread.sleep(50));
        }
    }

    public static void addRunnable(Runnable toRun) {
        synchronized (runnables) {
            runnables.add(toRun);
        }
    }

    public static void tryQuietly(ThrowingRunnable run) {
        try {
            run.run();
        } catch (Throwable ignored) {
        }
    }

    public static <E> List<E> synchronizedCopy(List<E> input, Supplier<List<E>> newList) {
        List<E> to = newList.get();
        synchronized (input) {
            to.addAll(input);
        }
        return to;
    }

}
