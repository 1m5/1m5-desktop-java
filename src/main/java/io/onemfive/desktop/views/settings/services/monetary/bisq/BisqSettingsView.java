package io.onemfive.desktop.views.settings.services.monetary.bisq;

import io.onemfive.desktop.views.ActivatableView;
import javafx.scene.layout.GridPane;


public class BisqSettingsView extends ActivatableView {

    private GridPane pane;
    private int gridRow = 0;

    public BisqSettingsView() {
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
