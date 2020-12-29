package io.onemfive.desktop.views.commons.exchange.qa;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ExchangeQAView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private String title = Resources.get("commonsView.exchange.qa.title");

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg marketGroup = addTitledGroupBg(pane, gridRow, 1, title);
        GridPane.setColumnSpan(marketGroup, 1);

        Label a, q;
        int numberOfQAs = 8;
        for(int i=0; i<numberOfQAs; i++) {
            q = addLabel(pane, ++gridRow, Resources.get("commonsView.exchange.qa."+(i+1)+".question"), Layout.FIRST_ROW_DISTANCE);
            q.setStyle("-fx-font-weight: bold");
            a = addMultilineLabel(pane, ++gridRow, Resources.get(Resources.get("commonsView.exchange.qa."+(i+1)+".answer")));
            a.setMaxWidth(Layout.INITIAL_WINDOW_WIDTH);
        }

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
