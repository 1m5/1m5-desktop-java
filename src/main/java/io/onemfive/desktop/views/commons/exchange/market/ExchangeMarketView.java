package io.onemfive.desktop.views.commons.exchange.market;

import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import java.util.*;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ExchangeMarketView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private String marketLabelText = Resources.get("commonsView.exchange.market.to");

    private List<String> currencies = Arrays.asList("AFN","AUD","BZD","CAD","CHF","CNY","CRC","CUP","DJF","LBP","EGP","ERN","EUR","HKD","ILS","INR","ISK","JMD","LRD","MKD","MXN","NZD","PKR","RUB","SAR","SGD","SYP","THB","TMT","TND","USD","VES","VND","XCD","YER");
    private Map<String, Double> prices = new HashMap<>();
    private Map<String, Label> labels = new HashMap<>();

    private ObservableList<String> ratesPerBTC = FXCollections.observableArrayList("AFN: 2039911.73","AUD","BZD","CAD","CHF","CNY","CRC","CUP","DJF","LBP","EGP","ERN","EUR","HKD","ILS","INR","ISK","JMD","LRD","MKD","MXN","NZD","PKR","RUB","SAR","SGD","SYP","THB","TMT","TND","USD","VES","VND","XCD","YER");
    private ComboBox<String> ratesPerBTCCBox;
    private ObservableList<String> ratesPerSat = FXCollections.observableArrayList("AFN: 2039911.73","AUD","BZD","CAD","CHF","CNY","CRC","CUP","DJF","LBP","EGP","ERN","EUR","HKD","ILS","INR","ISK","JMD","LRD","MKD","MXN","NZD","PKR","RUB","SAR","SGD","SYP","THB","TMT","TND","USD","VES","VND","XCD","YER");
    private ComboBox<String> ratesPerSatCBox;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // hard-code prices for now
        prices.put("AFN", 2039911.73);
        prices.put("AUD", 34879.33);
        prices.put("BZD", 53298.02);
        prices.put("CAD", 33967.85);
        prices.put("CHF", 23550.02);
        prices.put("CNY", 173300.89);
        prices.put("CRC", 16082653.71);
        prices.put("CUP", 681581.61);
        prices.put("DJF", 4699889.67);
        prices.put("LBP", 202142532.00);
        prices.put("EGP", 415824.15);
        prices.put("ERN", 0.00);
        prices.put("EUR", 21419.08);
        prices.put("HKD", 205492.15);
        prices.put("ILS", 85344.22);
        prices.put("INR", 1947295.26);
        prices.put("ISK", 3384361.34);
        prices.put("JMD", 3780038.74);
        prices.put("LRD", 4314192.49);
        prices.put("MKD", 0.00);
        prices.put("MXN", 0.00);
        prices.put("NZD", 0.00);
        prices.put("PKR", 0.00);
        prices.put("RUB", 0.00);
        prices.put("SAR", 0.00);
        prices.put("SGD", 34847.98);
        prices.put("SYP", 0.00);
        prices.put("THB", 0.00);
        prices.put("TMT", 0.00);
        prices.put("TND", 0.00);
        prices.put("USD", 26456.92);
        prices.put("VES", 31748304000.00);
        prices.put("VND", 0.00);
        prices.put("XCD", 0.00);
        prices.put("YER", 0.00);

        addTitledGroupBg(pane, gridRow, 1, marketLabelText);
        ratesPerBTCCBox = addComboBox(pane, ++gridRow, "Exchange Rates per BTC", Layout.FIRST_ROW_DISTANCE);
        ratesPerBTCCBox.setItems(ratesPerBTC);
        ratesPerBTCCBox.setMaxWidth(250d);
//        boolean first = true;
//        for(String currency : currencies) {
//            if(first) {
//                labels.put(currency, addLabel(pane, ++gridRow, currency + ": " + String.format("%,.2f", prices.get(currency)), Layout.FIRST_ROW_DISTANCE));
//                first = false;
//            } else {
//                labels.put(currency, addLabel(pane, ++gridRow, currency + ": " + String.format("%,.2f", prices.get(currency))));
//            }
//        }

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
