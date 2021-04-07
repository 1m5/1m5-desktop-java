package io.onemfive.desktop.views.personal.wallet;

import io.onemfive.desktop.DesktopBusClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.btc.BTCWallet;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.CreateWallet;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.btc.rpc.wallet.ListWallets;
import ra.btc.rpc.wallet.LoadWallet;
import ra.common.Envelope;
import ra.common.currency.crypto.BTC;
import ra.util.Resources;

import java.math.BigInteger;
import java.util.List;

import static io.onemfive.desktop.util.FormBuilder.*;

public class WalletView extends ActivatableView implements TopicListener {

    private static final String DEFAULT_WALLET_NAME = "Default";

    private static final String CREATE_WALLET_OP = "CreateWallet";
    private static final String GET_WALLET_INFO_OP = "GetWalletInfo";
    private static final String LIST_WALLETS_OP = "ListWallets";
    private static final String LOAD_WALLET_OP = "LoadWallet";

    private GridPane pane;
    private int gridRow = 0;

    private List<String> wallets;
    private BTCWallet activeWallet;

    private TextField walletNameTxt;
    private TextField walletVersionTxt;
    private TextField walletBalanceTxt;
    private TextField walletUnconfirmedBalanceTxt;
//    private TextField walletImmatureBalanceTxt;

    private InputTextField newWalletNameTxt;
    private CheckBox disablePrivateKeysOpt;
    private CheckBox blankOpt;
    private Button createWalletButton;

    private ObservableList<String> walletsObservable = FXCollections.observableArrayList();
    private ComboBox<String> walletsListView;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // Wallets List
        TitledGroupBg listWalletGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("personalView.wallet.list"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(listWalletGroup, 1);
        walletsListView = addComboBox(pane, ++gridRow);
        walletsListView.setItems(walletsObservable);
        walletsListView.setMaxWidth(300);

        // Wallet Info
        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.active"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletNameTxt = addTopLabelTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), "", Layout.TWICE_FIRST_ROW_DISTANCE).second;
        walletVersionTxt = addTopLabelTextField(pane, ++gridRow, Resources.get("personalView.wallet.version"), "").second;
        walletBalanceTxt = addTopLabelTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance"), "").second;
        walletUnconfirmedBalanceTxt = addTopLabelTextField(pane, ++gridRow, Resources.get("personalView.wallet.unconfirmedBalance"), "").second;
//        walletImmatureBalanceTxt = addTopLabelTextField(pane, ++gridRow, Resources.get("personalView.wallet.immatureBalance"), "").second;

        // Create Wallet
        TitledGroupBg createWalletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.create"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(createWalletGroup, 1);
        newWalletNameTxt = addInputTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), Layout.TWICE_FIRST_ROW_DISTANCE);
        newWalletNameTxt.setMaxWidth(300);
        disablePrivateKeysOpt = addCheckBox(pane, ++gridRow, Resources.get("personalView.wallet.disablePriveKeys"), Layout.FIRST_ROW_DISTANCE);
        disablePrivateKeysOpt.setSelected(false);
        blankOpt = addCheckBox(pane, ++gridRow, Resources.get("personalView.wallet.blank"), Layout.FIRST_ROW_DISTANCE);
        blankOpt.setSelected(false);
        createWalletButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("shared.create"), Layout.FIRST_ROW_DISTANCE);
        createWalletButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        createWalletButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Envelope e = Envelope.documentFactory();
                e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
                e.addNVP(DesktopBusClient.VIEW_OP, CREATE_WALLET_OP);
                e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
                CreateWallet cmd = new CreateWallet(newWalletNameTxt.getText(), disablePrivateKeysOpt.isSelected(), blankOpt.isSelected());
                e.addNVP(RPCCommand.NAME, cmd);
                e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                DesktopBusClient.deliver(e);
            }
        });

        walletsListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(walletsObservable.size() > 0) {
                    String walletName = walletsListView.getSelectionModel().getSelectedItem();
                    Envelope e = Envelope.documentFactory();
                    e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
                    e.addNVP(DesktopBusClient.VIEW_OP, LOAD_WALLET_OP);
                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
                    LoadWallet cmd = new LoadWallet(walletName);
                    e.addNVP(RPCCommand.NAME, cmd);
                    e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                    DesktopBusClient.deliver(e);
                }
            }
        });

        // List Wallets
        Envelope e = Envelope.documentFactory();
        e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
        e.addNVP(DesktopBusClient.VIEW_OP, LIST_WALLETS_OP);
        e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
        e.addNVP(RPCCommand.NAME, new ListWallets());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopBusClient.deliver(e);

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        createWalletButton.setOnAction(null);
        walletsListView.setOnAction(null);
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
                    activeWallet = new BTCWallet();
                    activeWallet.setName(request.walletName);
                    BTC balance = new BTC();
                    balance.setValue(BigInteger.ZERO);
                    activeWallet.setBalance(balance);
                    BTC unconfirmedBalance = new BTC();
                    unconfirmedBalance.setValue(BigInteger.ZERO);
                    activeWallet.setUnconfirmedBalance(unconfirmedBalance);
                    BTC immatureBalance = new BTC();
                    immatureBalance.setValue(BigInteger.ZERO);
                    activeWallet.setImmatureBalance(immatureBalance);
                    walletsObservable.add(request.walletName);
                }
                break;
            }
            case LIST_WALLETS_OP: {
                ListWallets request = (ListWallets) e.getValue(RPCCommand.NAME);
                RPCResponse response = (RPCResponse) e.getValue(RPCCommand.RESPONSE);
                if(response.error!=null) {
                    LOG.warning(response.error.toString());
                    // TODO: Show in UI
                } else if(request.wallets==null || request.wallets.size()==0) {
                    // Create Default
                    e = Envelope.documentFactory();
                    e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
                    e.addNVP(DesktopBusClient.VIEW_OP, CREATE_WALLET_OP);
                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
                    CreateWallet cmd = new CreateWallet("Default", false, false);
                    e.addNVP(RPCCommand.NAME, cmd);
                    e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                    DesktopBusClient.deliver(e);
                } else {
                    wallets = request.wallets;
                }
                break;
            }
            case LOAD_WALLET_OP: {
                RPCResponse response = (RPCResponse) e.getValue(RPCCommand.RESPONSE);
                if(response.error!=null) {
                    LOG.warning(response.error.toString());
                    // TODO: Show in UI
                } else {
                    // Wallet Loaded in Bitcoin Node successfully so get its information for the View
                    e = Envelope.documentFactory();
                    e.addNVP(DesktopBusClient.VIEW_NAME, WalletView.class.getName());
                    e.addNVP(DesktopBusClient.VIEW_OP, GET_WALLET_INFO_OP);
                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
                    e.addNVP(RPCCommand.NAME, new GetWalletInfo());
                    e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                    DesktopBusClient.deliver(e);
                }
                break;
            }
            case GET_WALLET_INFO_OP: {
                GetWalletInfo request = (GetWalletInfo) e.getValue(RPCCommand.NAME);
                RPCResponse response = (RPCResponse) e.getValue(RPCCommand.RESPONSE);
                if(response.error!=null) {
                    LOG.warning(response.error.toString());
                    // TODO: Show in UI
                } else {
                    activeWallet = request.wallet;
                    walletNameTxt.setText(activeWallet.getName());
                    walletNameTxt.setVisible(true);
                    walletVersionTxt.setText(activeWallet.getVersion().toString());
                    walletVersionTxt.setVisible(true);
                    walletBalanceTxt.setText(activeWallet.getBalance().value().toString());
                    walletBalanceTxt.setVisible(true);
                    walletUnconfirmedBalanceTxt.setText(activeWallet.getUnconfirmedBalance().value().toString());
                    walletUnconfirmedBalanceTxt.setVisible(true);
//                    walletImmatureBalanceTxt.setText(activeWallet.getImmatureBalance().value().toString());
//                    walletImmatureBalanceTxt.setVisible(true);
                }
                break;
            }
        }
        LOG.info("Model updated.");
    }
}

