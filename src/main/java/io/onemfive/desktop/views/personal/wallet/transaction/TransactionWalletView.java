package io.onemfive.desktop.views.personal.wallet.transaction;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.info.InfoWalletView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.btc.Transaction;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.tx.GetRawTransaction;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.util.Resources;

import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class TransactionWalletView extends ActivatableView implements TopicListener {

    private static final String GET_TRANSACTION_INFO_OP = "GetTransactionInfo";

    private GridPane pane;
    private int gridRow = 0;

    private final ObservableList<String> txIdsObservable = FXCollections.observableArrayList(DesktopClient.getBitcoinTransactions());
    private ComboBox<String> txComboView;
    private Button refreshButton;
    private TextField confirmationsTxtField;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;
        addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.transactions"), Layout.FIRST_ROW_DISTANCE);
        txComboView = addComboBox(pane, ++gridRow, Resources.get("personalView.wallet.transactions.select"));
        txComboView.setItems(txIdsObservable);
        txComboView.setMaxWidth(500);
        refreshButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("personalView.wallet.transactions.refresh"), Layout.FIRST_ROW_DISTANCE);
        confirmationsTxtField = addCompactTopLabelTextField(pane, gridRow++, Resources.get("personalView.wallet.transactions.confirmations"), "").second;
        confirmationsTxtField.setMaxWidth(200);
        confirmationsTxtField.setEditable(false);
        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        txComboView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new GetRawTransaction(txComboView.getSelectionModel().getSelectedItem(), true));
            }
        });
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new GetRawTransaction(txComboView.getSelectionModel().getSelectedItem(), true));
            }
        });
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");

        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        Envelope e = (Envelope) object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(response.result!=null) {
            if (GET_TRANSACTION_INFO_OP.equals(topic)) {
                Transaction tx = new Transaction();
                tx.fromMap((Map<String,Object>)response.result);
            } else {
                LOG.warning(topic + " topic not supported.");
            }
        } else {
            LOG.warning("Response.result was null!");
        }
    }

}
