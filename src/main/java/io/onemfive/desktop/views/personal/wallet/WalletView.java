package io.onemfive.desktop.views.personal.wallet;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class WalletView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private BTCWallet wallet;

    private String overview = Resources.get("personalView.wallet.overview");
    private Label overviewLabel;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        overviewLabel = addLabel(pane, ++gridRow, overview);
        overviewLabel.setWrapText(true);

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

