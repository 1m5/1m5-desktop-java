package io.onemfive.desktop.util;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;

import java.util.*;
import java.util.logging.Logger;

public class GUIUtil {

    private static final Logger LOG = Logger.getLogger(GUIUtil.class.getName());

    public static void copyToClipboard(String content) {
        try {
            if (content != null && content.length() > 0) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(content);
                clipboard.setContent(clipboardContent);
            }
        } catch (Throwable e) {
            LOG.severe("copyToClipboard failed " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static double getScrollbarWidth(Node scrollablePane) {
        Node node = scrollablePane.lookup(".scroll-bar");
        if (node instanceof ScrollBar) {
            final ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation().equals(Orientation.VERTICAL))
                return bar.getWidth();
        }
        return 0;
    }

    public static void focusWhenAddedToScene(Node node) {
        node.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                node.requestFocus();
            }
        });
    }

    public static <T> T getParentOfType(Node node, Class<T> t) {
        Node parent = node.getParent();
        while (parent != null) {
            if (parent.getClass().isAssignableFrom(t)) {
                break;
            } else {
                parent = parent.getParent();
            }
        }
        return t.cast(parent);
    }

    public static void requestFocus(Node node) {
        Platform.runLater(node::requestFocus);
    }

    public static void showSelectableTextModal(String title, String text) {
        JFXTextArea textArea = new JFXTextArea();
        textArea.setText(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(800, 600);

        Scene scene = new Scene(textArea);
        Stage stage = new Stage();
        if (null != title) {
            stage.setTitle(title);
        }
        stage.setScene(scene);
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    public static void removeChildrenFromGridPaneRows(GridPane gridPane, int start, int end) {
        Map<Integer, List<Node>> childByRowMap = new HashMap<>();
        gridPane.getChildren().forEach(child -> {
            final Integer rowIndex = GridPane.getRowIndex(child);
            childByRowMap.computeIfAbsent(rowIndex, key -> new ArrayList<>());
            childByRowMap.get(rowIndex).add(child);
        });

        for (int i = Math.min(start, childByRowMap.size()); i < Math.min(end + 1, childByRowMap.size()); i++) {
            List<Node> nodes = childByRowMap.get(i);
            if (nodes != null) {
                nodes.stream()
                        .filter(Objects::nonNull)
                        .filter(node -> gridPane.getChildren().contains(node))
                        .forEach(node -> gridPane.getChildren().remove(node));
            }
        }
    }

    public static void setFitToRowsForTableView(TableView tableView,
                                                int rowHeight,
                                                int headerHeight,
                                                int minNumRows,
                                                int maxNumRows) {
        int size = tableView.getItems().size();
        int minHeight = rowHeight * minNumRows + headerHeight;
        int maxHeight = rowHeight * maxNumRows + headerHeight;
        int height = Math.min(maxHeight, Math.max(minHeight, size * rowHeight + headerHeight));

        tableView.setPrefHeight(-1);
        tableView.setVisible(false);
        // We need to delay the setter to the next render frame as otherwise views don' get updated in some cases
        // Not 100% clear what causes that issue, but seems the requestLayout method is not called otherwise.
        // We still need to set the height immediately, otherwise some views render an incorrect layout.
        tableView.setPrefHeight(height);

        Platform.runLater(() -> {
            tableView.setPrefHeight(height);
            tableView.setVisible(true);
        });
    }

    public static <T> ListCell<T> getComboBoxButtonCell(String title, ComboBox<T> comboBox) {
        return getComboBoxButtonCell(title, comboBox, true);
    }

    public static <T> ListCell<T> getComboBoxButtonCell(String title,
                                                        ComboBox<T> comboBox,
                                                        Boolean hideOriginalPrompt) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                // See https://github.com/jfoenixadmin/JFoenix/issues/610
                if (hideOriginalPrompt)
                    this.setVisible(item != null || !empty);

                if (empty || item == null) {
                    setText(title);
                } else {
                    setText(comboBox.getConverter().toString(item));
                }
            }
        };
    }

}
