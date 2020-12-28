package io.onemfive.desktop.views.commons.exchange.market;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.layout.VBox;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ExchangeMarketView extends ActivatableView implements TopicListener {

    private VBox pane;
    private int gridRow = 0;


    private String marketLabelText = Resources.get("commonsView.exchange.market.to");

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (VBox)root;



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
        LOG.info("Updating model...");

        LOG.info("Model updated.");
    }

}
