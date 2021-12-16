package io.onemfive.desktop.views.personal.wallet.sweep;

import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.BaseWalletView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import ra.btc.rpc.wallet.ImportPrivKey;
import ra.common.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SweepWalletView extends BaseWalletView implements TopicListener {

    private static final String DEFAULT_WALLET_NAME = "Default";

    private GridPane pane;
    private int gridRow = 0;

    private TitledGroupBg importKeysGroup;
    private InputTextField importTxt;
//    private CheckBox sweepCheckBox;
    private Button importButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // 1st Row Left: Sweep
        importKeysGroup = addTitledGroupBg(pane, gridRow, 4, Resources.get("personalView.wallet.import"));
        importTxt = addInputTextField(pane, gridRow++, Resources.get("personalView.wallet.import.key"), Layout.FIRST_ROW_DISTANCE);
//        sweepCheckBox = addCheckBox(pane, gridRow++, Resources.get("personalView.wallet.import.sweep"), Layout.FIRST_ROW_DISTANCE);
//        sweepCheckBox.setSelected(true);
        importButton = addPrimaryActionButton(pane, gridRow++, Resources.get("personalView.wallet.import.import"), Layout.LIST_ROW_HEIGHT);
        importButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
//        sweepCheckBox.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                if(sweepCheckBox.isSelected()) {
//                    importButton.setText(Resources.get("personalView.wallet.import.sweep"));
//                } else {
//                    importButton.setText(Resources.get("personalView.wallet.import.import"));
//                }
//            }
//        });
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                if(sweepCheckBox.isSelected()) {
//                    if(importTxt.getText()!=null) {
//                        sendRequest(new SweepPrivKey(importTxt.getText()));
//                        LOG.warning("Sweep not yet supported");
//                    }
//                } else {
                    if(importTxt.getText()!=null)
                        sendBTCRequest(new ImportPrivKey(importTxt.getText()));
//                }
            }
        });
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        importButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");

        LOG.info("Model updated.");
    }

}

