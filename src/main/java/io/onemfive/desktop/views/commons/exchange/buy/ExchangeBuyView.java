package io.onemfive.desktop.views.commons.exchange.buy;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ExchangeBuyView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;


    private String buyLabelText = Resources.get("commonsView.exchange.tab.buy");

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane) root;

        TitledGroupBg marketGroup = addTitledGroupBg(pane, gridRow, 1, buyLabelText);
        GridPane.setColumnSpan(marketGroup, 1);

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
