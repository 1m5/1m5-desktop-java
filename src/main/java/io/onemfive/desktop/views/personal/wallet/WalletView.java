package io.onemfive.desktop.views.personal.wallet;

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
import org.neo4j.cypher.internal.v3_5.ast.Create;
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
import ra.common.network.ControlCommand;
import ra.util.Resources;
import ra.util.Wait;

import java.math.BigInteger;
import java.util.ArrayList;
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

    // Create Wallet
    private InputTextField newWalletNameTxt;
    private PasswordTextField passphraseTxt;
    private PasswordTextField passphrase2Txt;
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
        walletsListView = addComboBox(pane, ++gridRow, Resources.get("personalView.wallet.select"));
        walletsListView.setItems(walletsObservable);
        walletsListView.setMaxWidth(300);
        wallets = new ArrayList<>();

        Envelope e = Envelope.documentFactory();
        e.setClient(DesktopClient.class.getName());
        e.addRoute(BitcoinService.class.getName(), BitcoinService.OPERATION_RPC_REQUEST);
        e.addNVP(RPCCommand.NAME, new ListWallets());
        DesktopClient.deliver(e);

        activeWallet = new BTCWallet();
        activeWallet.setName("Default");
        activeWallet.setVersion(1);
        BTC balance = new BTC();
        balance.setValue(BigInteger.ZERO);
        activeWallet.setBalance(balance);
        BTC uncomfBalance = new BTC();
        uncomfBalance.setValue(BigInteger.ZERO);
        activeWallet.setUnconfirmedBalance(uncomfBalance);

        // Wallet Info
        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.active"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletNameTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), "", Layout.TWICE_FIRST_ROW_DISTANCE).second;
        walletNameTxt.setMaxWidth(300);
        walletVersionTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.version"), "").second;
        walletVersionTxt.setMaxWidth(300);
        walletBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance"), "").second;
        walletBalanceTxt.setMaxWidth(300);
        walletUnconfirmedBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.unconfirmedBalance"), "").second;
        walletUnconfirmedBalanceTxt.setMaxWidth(300);
//        walletImmatureBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.immatureBalance"), "").second;

        // Create Wallet
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

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        createWalletButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // TODO: Error checking
                Envelope e = Envelope.documentFactory();
                e.setCommandPath(ControlCommand.Send.name());
                e.addNVP(DesktopClient.VIEW_NAME, WalletView.class.getName());
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

        walletsListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(walletsObservable.size() > 0) {
                    String walletName = walletsListView.getSelectionModel().getSelectedItem();
                    LOG.info("Selected wallet: "+walletName);
                    Envelope e = Envelope.documentFactory();
                    e.setCommandPath(ControlCommand.Send.name());
                    e.addNVP(DesktopClient.VIEW_NAME, WalletView.class.getName());
                    e.addNVP(DesktopClient.VIEW_OP, GET_WALLET_INFO_OP);
                    e.addNVP(RPCCommand.NAME, new GetWalletInfo(walletName).toMap());
                    e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
                    DesktopClient.deliver(e);
                }
            }
        });

        // List Wallets
        listWallets();

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
        String json = new String((byte[])e.getContent());
        switch (topic) {
            case CREATE_WALLET_OP: {
                CreateWallet request = new CreateWallet();
                request.fromJSON(json);
                if(request.error==null) {
                    LOG.info("Successful wallet creation.");
                    listWallets();
                    newWalletNameTxt.setText(null);
                    passphraseTxt.setText(null);
                    passphrase2Txt.setText(null);
                }
                break;
            }
            case LIST_WALLETS_OP: {
                if("{200}".equals(json)) {
                    LOG.warning("Bitcoin node not running.");
                    return;
                }
                ListWallets request = new ListWallets();
                request.fromJSON(json);
                if(request.wallets!=null) {
                    wallets = new ArrayList<>();
                    for(String wallet : request.wallets) {
                        if(!wallet.isEmpty()) wallets.add(wallet);
                    }
                    walletsObservable.clear();
                    walletsObservable.addAll(wallets);
                }
                if(wallets.contains("Default"))
                    walletsListView.getSelectionModel().select("Default");
                else
                    walletsListView.getSelectionModel().selectFirst();
                break;
            }
            case GET_WALLET_INFO_OP: {
                if("{200}".equals(json)) {
                    LOG.warning("Bitcoin node not running.");
                    return;
                }
                GetWalletInfo request = new GetWalletInfo();
                request.fromJSON(json);
                if(request.wallet.getName()!=null) {
                    activeWallet = request.wallet;
                    walletNameTxt.setText(activeWallet.getName());
                    walletVersionTxt.setText(activeWallet.getVersion().toString());
                    walletBalanceTxt.setText(activeWallet.getBalance().value().toString());
                    walletUnconfirmedBalanceTxt.setText(activeWallet.getUnconfirmedBalance().value().toString());
//                    walletImmatureBalanceTxt.setText(activeWallet.getImmatureBalance().value().toString());
                }
                break;
            }
        }
        LOG.info("Model updated.");
    }

    private void listWallets() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(DesktopClient.VIEW_NAME, WalletView.class.getName());
        e.addNVP(DesktopClient.VIEW_OP, LIST_WALLETS_OP);
        e.addNVP(RPCCommand.NAME, new ListWallets().toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopClient.deliver(e);
    }
}

