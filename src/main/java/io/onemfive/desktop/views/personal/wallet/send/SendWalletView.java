package io.onemfive.desktop.views.personal.wallet.send;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.BaseWalletView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.SendToAddress;
import ra.common.Envelope;
import ra.common.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SendWalletView extends BaseWalletView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private InputTextField publicKeyTxt;
    private InputTextField receiverAmountTxt;
    private CheckBox subtractFeeFromAmountCheck;
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
        subtractFeeFromAmountCheck = addCheckBox(pane, gridRow++, Resources.get("personalView.wallet.subtractFee"), Layout.FIRST_ROW_DISTANCE);
        subtractFeeFromAmountCheck.setSelected(false);
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
                SendToAddress req = new SendToAddress(DesktopClient.getCache().getActiveWallet().getName(),
                        publicKeyTxt.getText(),
                        Double.parseDouble(receiverAmountTxt.getText()));
                req.subtractFeeFromAmount = subtractFeeFromAmountCheck.isSelected();
                sendBTCRequest(req);
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
                LOG.info("txid: "+txid);
                publicKeyTxt.setText(null);
                receiverAmountTxt.setText(null);
            }
        }
        LOG.info("Model updated.");
    }

}

