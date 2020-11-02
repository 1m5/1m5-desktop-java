package io.onemfive.desktop.views.settings.platform;

import io.onemfive.desktop.views.ActivatableView;
import javafx.scene.layout.GridPane;

public class PlatformSettingsView extends ActivatableView {

    private GridPane pane;

    private int gridRow = 0;

    public PlatformSettingsView() {

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
