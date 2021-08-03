package io.onemfive.desktop.views.personal.wallet.send;

import io.onemfive.desktop.DesktopClient;
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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.Transaction;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.CreateWallet;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.btc.rpc.wallet.ListWallets;
import ra.btc.rpc.wallet.SendToAddress;
import ra.btc.uses.SendBTC;
import ra.common.Envelope;
import ra.common.currency.crypto.BTC;
import ra.common.network.ControlCommand;
import ra.util.Resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SendWalletView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private InputTextField publicKeyTxt;
    private InputTextField receiverAmountTxt;
    private Button sendButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        addTitledGroupBg(pane, gridRow, 4, Resources.get("personalView.wallet.send"));
        publicKeyTxt = addInputTextField(pane, gridRow++,Resources.get("personalView.wallet.receiver.pubkey"), Layout.FIRST_ROW_DISTANCE);
        publicKeyTxt.setMaxWidth(500);
        receiverAmountTxt = addInputTextField(pane, gridRow++, Resources.get("personalView.wallet.amount"), Layout.FIRST_ROW_DISTANCE);
        receiverAmountTxt.setMaxWidth(300);
        sendButton = addPrimaryActionButton(pane, gridRow++, Resources.get("personalView.wallet.send"), Layout.FIRST_ROW_DISTANCE);
        sendButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new SendToAddress(DesktopClient.getActiveWallet().getName(),
                                publicKeyTxt.getText(),
                                Double.parseDouble(receiverAmountTxt.getText())));
            }
        });

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        sendButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");
        Envelope e = (Envelope)object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(SendToAddress.NAME.equals(topic)) {
            if(response.result!=null) {
                String txid = (String)response.result;
                Transaction tx = new Transaction();
                tx.txid = txid;
                tx.time = new Date().getTime();
                tx.confirmations = 0;
                DesktopClient.addBitcoinTransaction(tx);
                LOG.info("txid: "+txid);
                publicKeyTxt.setText(null);
                receiverAmountTxt.setText(null);
            }
        }
        LOG.info("Model updated.");
    }

}

