package io.onemfive.desktop.views.ops.services.keyring;

import io.onemfive.desktop.views.ActivatableView;
import javafx.scene.layout.GridPane;

public class KeyringOpsView extends ActivatableView {

    private GridPane pane;
    private int gridRow = 0;

    public KeyringOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {

    }

    @Override
    protected void deactivate() {

    }

}
