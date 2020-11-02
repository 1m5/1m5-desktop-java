package io.onemfive.desktop.views;

import io.onemfive.desktop.util.Transitions;
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.util.logging.Logger;

public abstract class BaseView implements View {

    protected final Logger LOG = Logger.getLogger(this.getClass().getName());

    @FXML
    protected Node root;
    protected Transitions transitions;

    public BaseView() {}

    @Override
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

}
