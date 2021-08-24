package io.onemfive.desktop.views.personal.wallet.info;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.common.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class InfoWalletView extends ActivatableView implements TopicListener {

    private static final String DEFAULT_WALLET_NAME = "Default";

    private GridPane pane;
    private int gridRow = 0;

    private List<String> wallets;
    private Button refreshButton;
    private final ObservableList<String> walletsObservable = FXCollections.observableArrayList();
    private ComboBox<String> walletsListView;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.active"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletsListView = addComboBox(pane, ++gridRow, Resources.get("personalView.wallet.active"));
        walletsListView.setItems(walletsObservable);
        walletsListView.setMaxWidth(300);
        wallets = new ArrayList<>();
        refreshButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("personalView.wallet.info.refresh"), Layout.FIRST_ROW_DISTANCE);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        walletsListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String walletName = walletsListView.getSelectionModel().getSelectedItem();
                if(DEFAULT_WALLET_NAME.equals(walletName))
                    walletName = "";
                sendRequest(new GetWalletInfo(walletName));
            }
        });
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new ListWallets());
            }
        });
        sendRequest(new ListWallets());
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        walletsObservable.clear();
        wallets.clear();
        walletsListView.setOnAction(null);
        refreshButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        // TODO: Wallet info not completely showing up...few bugs here needing cleaned up
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(response.result!=null) {
            if (ListWallets.NAME.equals(topic)) {
                List<String> wallets = (List<String>) response.result;
                walletsObservable.clear();
                this.wallets.clear();
                for (String walletName : wallets) {
                    if (walletName.isEmpty()) {
                        walletsObservable.add(DEFAULT_WALLET_NAME);
                        this.wallets.add(DEFAULT_WALLET_NAME);
                    } else {
                        walletsObservable.add(walletName);
                        this.wallets.add(walletName);
                    }
                }
            } else if (GetWalletInfo.NAME.equals(topic)) {
                BTCWallet activeWallet = new BTCWallet();
                Map<String, Object> m = (Map<String, Object>) response.result;
                activeWallet.fromMap(m);
                DesktopClient.setActiveWallet(activeWallet);
            }
        } else {
            LOG.warning("Response.result was null!");
        }
        if(DesktopClient.getActiveWallet()==null) {
            // Load default wallet
            sendRequest(new GetWalletInfo(""));
        } else {
            if(DesktopClient.getActiveWallet().getName().isEmpty() || DesktopClient.getActiveWallet().getName().equals(DEFAULT_WALLET_NAME))
                walletsListView.getSelectionModel().select(DEFAULT_WALLET_NAME);
            else
                walletsListView.getSelectionModel().select(DesktopClient.getActiveWallet().getName());
        }
        LOG.info("Model updated.");
    }

}

