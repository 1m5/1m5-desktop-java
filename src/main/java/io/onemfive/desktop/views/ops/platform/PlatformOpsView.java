package io.onemfive.desktop.views.ops.platform;

import io.onemfive.desktop.views.ActivatableView;
import javafx.scene.layout.GridPane;

public class PlatformOpsView extends ActivatableView {

    private GridPane pane;

    private int gridRow = 0;

    public PlatformOpsView() {

    }

    @Override
    public void initialize() {
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
