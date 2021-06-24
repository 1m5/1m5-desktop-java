package io.onemfive.desktop.views.personal.wallet.create;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.wallet.CreateWallet;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class CreateWalletView extends ActivatableView implements TopicListener {

    private static final String CREATE_WALLET_OP = "CreateWallet";

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
                Envelope e = Envelope.documentFactory();
                e.setCommandPath(ControlCommand.Send.name());
                e.addNVP(DesktopClient.VIEW_NAME, CreateWalletView.class.getName());
                e.addNVP(DesktopClient.VIEW_OP, CREATE_WALLET_OP);
                CreateWallet cmd = new CreateWallet(
                        newWalletNameTxt.getText(),
                        false,
                        false,
                        passphraseTxt.getText(),
                        false,
                        false,
                        false);
                e.addNVP(RPCCommand.NAME, cmd.toMap());
                e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                DesktopClient.deliver(e);
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
        LOG.info("Updating model...");
        Envelope e = (Envelope)object;
        String json = new String((byte[])e.getContent());
        if (CREATE_WALLET_OP.equals(topic)) {
            CreateWallet request = new CreateWallet();
            request.fromJSON(json);
            if(request.error==null) {
                LOG.info("Successful wallet creation.");
                newWalletNameTxt.setText(null);
                passphraseTxt.setText(null);
                passphrase2Txt.setText(null);
            } else {
                errorLabel.setText(request.warning);
                errorLabel.setVisible(true);
            }
        }
        LOG.info("Model updated.");
    }

}

