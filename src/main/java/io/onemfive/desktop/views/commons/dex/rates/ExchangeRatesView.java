package io.onemfive.desktop.views.commons.dex.rates;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;

public class ExchangeRatesView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");

        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;

        LOG.info("Model updated.");
    }

}

