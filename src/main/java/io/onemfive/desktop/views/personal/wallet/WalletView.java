package io.onemfive.desktop.views.personal.wallet;

import io.onemfive.desktop.DesktopBusClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.CreateWallet;
import ra.common.Envelope;
import ra.common.currency.crypto.BTC;
import ra.util.Resources;

import java.math.BigInteger;

import static io.onemfive.desktop.util.FormBuilder.*;

public class WalletView extends ActivatableView implements TopicListener {

    private static final String CREATE_WALLET_OP = "CreateWallet";

    private GridPane pane;
    private int gridRow = 0;

    private BTCWallet wallet;
    private Boolean disablePrivateKeys = false;
    private Boolean blank = false;

    private final String overview = Resources.get("personalView.wallet.overview");
    private final String walletName = Resources.get("personalView.wallet.name");
    private final String disablePrivateKeysTxt = Resources.get("personalView.wallet.disablePriveKeys");
    private final String blankTxt = Resources.get("personalView.wallet.blank");
    private final String create = Resources.get("shared.create");

    private Label overviewLabel;
    private InputTextField walletNameTxt;
    private CheckBox disablePrivateKeysOpt;
    private CheckBox blankOpt;
    private Button createWalletButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        overviewLabel = addLabel(pane, ++gridRow, overview);
        overviewLabel.setWrapText(true);

        walletNameTxt = new InputTextField();
        walletNameTxt.setPromptText(walletName);
        disablePrivateKeysOpt = new CheckBox();
        disablePrivateKeysOpt.setSelected(false);
        disablePrivateKeysOpt.setText(disablePrivateKeysTxt);
        blankOpt = new CheckBox();
        blankOpt.setSelected(false);
        blankOpt.setText(blankTxt);
        createWalletButton = new AutoTooltipButton(create);
        createWalletButton.setDefaultButton(true);
        createWalletButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        disablePrivateKeysOpt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                disablePrivateKeys = disablePrivateKeysOpt.isSelected();
            }
        });

        blankOpt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                blank = blankOpt.isSelected();
            }
        });

        createWalletButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Envelope e = Envelope.documentFactory();
                e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
                e.addNVP(DesktopBusClient.VIEW_OP, CREATE_WALLET_OP);
                e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
                CreateWallet cmd = new CreateWallet(walletName, disablePrivateKeys, blank);
                e.addNVP(RPCCommand.NAME, cmd);
                e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                DesktopBusClient.deliver(e);
            }
        });

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        disablePrivateKeysOpt.setOnAction(null);
        blankOpt.setOnAction(null);
        createWalletButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");
        Envelope e = (Envelope)object;
        switch (topic) {
            case CREATE_WALLET_OP: {
                CreateWallet request = (CreateWallet) e.getValue(RPCCommand.NAME);
                RPCResponse response = (RPCResponse) e.getValue(RPCCommand.RESPONSE);
                if(response.error!=null) {
                    LOG.warning(response.error.toString());
                    // TODO: Show in UI
                } else {
                    wallet = new BTCWallet();
                    wallet.setName(request.walletName);
                    BTC balance = new BTC();
                    balance.setValue(BigInteger.ZERO);
                    wallet.setBalance(balance);
                }
            }
        }
        LOG.info("Model updated.");
    }
}

