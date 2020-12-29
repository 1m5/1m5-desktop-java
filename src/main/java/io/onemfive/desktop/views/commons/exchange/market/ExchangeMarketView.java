package io.onemfive.desktop.views.commons.exchange.market;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ExchangeMarketView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private static Double satsPerBtc = 100000000d;

    private String marketLabelText = Resources.get("commonsView.exchange.market.to");
    private Label usdBtcLabel;
    private Label lbpBtcLabel;
    private Label vesBtcLabel;

    private Double usdBTCPrice = 26456.92;
    private Double lbpBTCPrice = 202142532.00;
    private Double vesBTCPrice = 31748304000.00;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg marketGroup = addTitledGroupBg(pane, gridRow, 1, marketLabelText);
        usdBtcLabel = addLabel(pane, ++gridRow, "USD: "+String.format("%,.2f", usdBTCPrice)+" / "+String.format("%,.8f", usdBTCPrice/satsPerBtc), Layout.FIRST_ROW_DISTANCE);
        lbpBtcLabel = addLabel(pane, ++gridRow, "LBP: "+String.format("%,.2f", lbpBTCPrice)+" / "+String.format("%,.2f", lbpBTCPrice/satsPerBtc));
        vesBtcLabel = addLabel(pane, ++gridRow, "VES: "+String.format("%,.2f", vesBTCPrice)+" / "+String.format("%,.2f", vesBTCPrice/satsPerBtc));

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
