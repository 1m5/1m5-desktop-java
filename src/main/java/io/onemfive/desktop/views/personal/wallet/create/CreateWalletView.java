package io.onemfive.desktop.views.personal.wallet.create;

import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.BaseWalletView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.CreateWallet;
import ra.common.Envelope;
import ra.common.Resources;

import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class CreateWalletView extends BaseWalletView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private InputTextField newWalletNameTxt;
    private PasswordTextField passphraseTxt;
    private PasswordTextField passphrase2Txt;
    private Button createWalletButton;
    private Label errorLabel;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg createWalletGroup = addTitledGroupBg(pane, ++gridRow, 5, Resources.get("personalView.wallet.create"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(createWalletGroup, 1);
        newWalletNameTxt = addInputTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), Layout.TWICE_FIRST_ROW_DISTANCE);
        newWalletNameTxt.setMaxWidth(300);
        passphraseTxt = addPasswordTextField(pane, ++gridRow, Resources.get("personalView.wallet.passphrase"), Layout.FIRST_ROW_DISTANCE);
        passphraseTxt.setMaxWidth(300);
        passphrase2Txt = addPasswordTextField(pane, ++gridRow, Resources.get("personalView.wallet.passphrase2"));
        passphrase2Txt.setMaxWidth(300);
        createWalletButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("shared.create"), Layout.FIRST_ROW_DISTANCE);
        createWalletButton.getStyleClass().add("action-button");
        errorLabel = addLabel(pane, ++gridRow, "");
        errorLabel.setVisible(false);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        createWalletButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                errorLabel.setText("");
                errorLabel.setVisible(false);
                // TODO: Error checking
                // TODO: Support options in UI
                sendBTCRequest(new CreateWallet(
                        newWalletNameTxt.getText(),
                        false,
                        false,
                        passphraseTxt.getText(),
                        false,
                        false,
                        false));
            }
        });

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        createWalletButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        if (CreateWallet.NAME.equals(topic)) {
            RPCResponse response = new RPCResponse();
            Map<String,Object> responseMap = (Map<String,Object>)e.getValue(RPCCommand.RESPONSE);
            response.fromMap(responseMap);
            if(response.error==null) {
                LOG.info("Successful wallet creation.");
                newWalletNameTxt.setText(null);
                passphraseTxt.setText(null);
                passphrase2Txt.setText(null);
            } else {
                errorLabel.setText(response.error.message);
                errorLabel.setVisible(true);
            }
        }
        LOG.info("Model updated.");
    }

}

