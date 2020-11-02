package io.onemfive.desktop.views.settings.services.identity;

import io.onemfive.desktop.views.ActivatableView;
import javafx.scene.layout.GridPane;


public class IdentitySettingsView extends ActivatableView {

    private GridPane pane;
    private int gridRow = 0;

    public IdentitySettingsView() {
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
