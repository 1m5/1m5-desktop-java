package io.onemfive.desktop.views.personal.wallet.sweep;

import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.btc.rpc.wallet.ImportPrivKey;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SweepWalletView extends ActivatableView implements TopicListener {

    private static final String DEFAULT_WALLET_NAME = "Default";

    private GridPane pane;
    private int gridRow = 0;

    private TitledGroupBg importKeysGroup;
    private InputTextField importTxt;
    private CheckBox sweepCheckBox;
    private Button importButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // 1st Row Left: Sweep
        importKeysGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("personalView.wallet.import"));
        importTxt = addInputTextField(pane, gridRow++, Resources.get("personalView.wallet.import.privatekey"), Layout.FIRST_ROW_DISTANCE);
        sweepCheckBox = addCheckBox(pane, gridRow++, Resources.get("personalView.wallet.import.sweep"));
        sweepCheckBox.setSelected(false);
        importButton = addPrimaryActionButton(pane, gridRow++, Resources.get("personalView.wallet.import.send"), Layout.LIST_ROW_HEIGHT);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!sweepCheckBox.isSelected()) {
                    if(importTxt.getText()!=null)
                        sendRequest(new ImportPrivKey(importTxt.getText()));
                }
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

