package io.onemfive.desktop.views.personal.wallet.details;

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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.wallet.CreateWallet;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.util.Resources;

import java.util.ArrayList;
import java.util.List;

import static io.onemfive.desktop.util.FormBuilder.*;

public class DetailsWalletView extends ActivatableView implements TopicListener {

    private static final String DEFAULT_WALLET_NAME = "Default";

    private static final String GET_WALLET_INFO_OP = "GetWalletInfo";

    private GridPane pane;
    private int gridRow = 0;

    private BTCWallet activeWallet;
    private TextField walletNameTxt;
    private TextField walletVersionTxt;
    private TextField walletBalanceTxt;
    private TextField walletUnconfirmedBalanceTxt;
//    private TextField walletImmatureBalanceTxt;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.active"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletNameTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), "").second;
        walletNameTxt.setMaxWidth(300);
        walletVersionTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.version"), "").second;
        walletVersionTxt.setMaxWidth(300);
        walletBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance"), "").second;
        walletBalanceTxt.setMaxWidth(300);
        walletUnconfirmedBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.unconfirmedBalance"), "").second;
        walletUnconfirmedBalanceTxt.setMaxWidth(300);
//        walletImmatureBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.immatureBalance"), "").second;

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        activeWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
        if(activeWallet==null) {
            loadWallet();
        }
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
        Envelope e = (Envelope)object;
        String json = new String((byte[])e.getContent());
        if("{200}".equals(json)) {
            // TODO: Popup
            LOG.warning("Bitcoin node not running.");
            return;
        }
        GetWalletInfo request = new GetWalletInfo();
        request.fromJSON(json);
        if(request.wallet.getName()!=null) {
            activeWallet = request.wallet;
            if(activeWallet.getName().isEmpty())
                walletNameTxt.setText("Default");
            else
                walletNameTxt.setText(activeWallet.getName());
            walletVersionTxt.setText(activeWallet.getVersion().toString());
            walletBalanceTxt.setText(activeWallet.getBalance().value().toString());
            walletUnconfirmedBalanceTxt.setText(activeWallet.getUnconfirmedBalance().value().toString());
//                    walletImmatureBalanceTxt.setText(activeWallet.getImmatureBalance().value().toString());
        }
        LOG.info("Model updated.");
    }

    private void loadWallet() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(DesktopClient.VIEW_NAME, DetailsWalletView.class.getName());
        e.addNVP(DesktopClient.VIEW_OP, GET_WALLET_INFO_OP);
        e.addNVP(RPCCommand.NAME, new GetWalletInfo().toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopClient.deliver(e);
    }
}

