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
import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SendWalletView extends ActivatableView implements TopicListener {

    private static final String SEND_OP = "Send";

    private GridPane pane;
    private int gridRow = 0;

    private InputTextField publicKeyTxt;
    private InputTextField receiverAmountTxt;
    private Button sendButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        addTitledGroupBg(pane, gridRow, 4, Resources.get("personalView.wallet.import"));
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
                // TODO: Error checking
                Envelope e = Envelope.documentFactory();
                e.setCommandPath(ControlCommand.Send.name());
                e.addNVP(DesktopClient.VIEW_NAME, SendWalletView.class.getName());
                e.addNVP(DesktopClient.VIEW_OP, SEND_OP);
//                SendBTC sendBTC = new SendBTC();
                double receiverAmount = Double.parseDouble(receiverAmountTxt.getText());
//                sendBTC.receiverAmount = new BTC(receiverAmount);
                double devFee = 0.01;
                // TODO: Send Dev Fee
//                sendBTC.devFee = new BTC(devFee);
                double estMinerFee = 0.0000015;
//                sendBTC.estimatedMinerFee = new BTC(estMinerFee);
//                sendBTC.receiverAddress = publicKeyTxt.getText();
//                sendBTC.totalAmount = new BTC(receiverAmount+devFee+estMinerFee);
                BTCWallet activeWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
                e.addNVP(RPCCommand.NAME, new SendToAddress(activeWallet.getName(), publicKeyTxt.getText(), receiverAmount).toMap());
                e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                DesktopClient.deliver(e);
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
        Object cmdObj = e.getValue(RPCCommand.NAME);
        if (SEND_OP.equals(topic)) {
            SendBTC request = new SendBTC();
//            if(cmdObj instanceof String)
//                request.fromJSON((String)cmdObj);
//            else if(cmdObj instanceof Map)
//                request.fromMap((Map<String,Object>)cmdObj);
        }
        LOG.info("Model updated.");
    }

}

