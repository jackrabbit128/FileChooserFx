package com.chainstaysoftware.filechooser;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Helper to set a cursor on an object (scene, node), possibly retrieved from a relation (stage, scene, node).
 * Instances can only be created using one of the factory methods (named {@code forXxx}.
 */
final class CursorHelper {
    private static final CursorHelper NOOP_HELPER = new CursorHelper();

    private final Consumer<Cursor>[] cursored;

    @SafeVarargs
    private CursorHelper(Consumer<Cursor>... cursored) {
        this.cursored = cursored;
    }

    public static CursorHelper forNodeAndSceneOf(Node node) {
        Scene scene = node.getScene();
        if (scene == null) {
            return forNode(node);
        }

        return new CursorHelper(scene::setCursor, node::setCursor);
    }

    public static CursorHelper forSceneOf(Node node) {
        Scene scene = node.getScene();
        if (scene == null) {
            return NOOP_HELPER;
        }

        return forScene(node.getScene());
    }

    public static CursorHelper forSceneOf(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null) {
            return NOOP_HELPER;
        }

        return forScene(stage.getScene());
    }

    public static CursorHelper forScene(Scene scene) {
        return new CursorHelper(scene::setCursor);
    }

    public static CursorHelper forNode(Node node) {
        return new CursorHelper(node::setCursor);
    }

    /**
     * Activates the given cursor on the requested components.
     *
     * @param cursor the cursor to activate, {@code null} is allowed
     * @see Node#setCursor(Cursor)
     * @see Scene#setCursor(Cursor)
     */
    public void setCursor(Cursor cursor) {
        Stream.of(cursored).forEach(item -> item.accept(cursor));
    }
}
