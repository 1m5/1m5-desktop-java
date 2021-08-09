package io.onemfive.desktop.views.personal.wallet.transaction;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ra.btc.Transaction;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.ListTransactions;
import ra.common.Envelope;
import ra.util.Resources;

import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class TransactionWalletView extends ActivatableView implements TopicListener {

    private static final String GET_TRANSACTION_INFO_OP = "GetTransactionInfo";

    private GridPane pane;
    private int gridRow = 0;
    private TableView txView;
    private Button refreshButton;
    private final ObservableList<Transaction> txListObservable = FXCollections.observableArrayList();

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;
        addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.transactions"), Layout.FIRST_ROW_DISTANCE);
        txView = addTableViewWithHeader(pane, ++gridRow, Resources.get("personalView.wallet.transactions"));
        txView.setEditable(false);
        TableColumn idCol = new TableColumn("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Transaction, String>("txid"));
        TableColumn timeCol = new TableColumn("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<Transaction, Long>("time"));
        TableColumn confCol = new TableColumn("Confirmations");
        confCol.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("confirmations"));
        txView.getColumns().addAll(idCol, timeCol, confCol);
        txView.setItems(txListObservable);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new ListTransactions());
            }
        });
        sendRequest(new ListTransactions());
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        refreshButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        Envelope e = (Envelope) object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(response.error!=null) {
            if(response.error.code == -1) {
                LOG.warning("Incorrect request: "+response.error.message);
            } else {
                LOG.warning(response.error.toJSON());
            }
        }
        if(response.result!=null) {
            if (ListTransactions.NAME.equals(topic)) {
                txListObservable.clear();
                List<Map<String,Object>> txListMaps = (List<Map<String,Object>>)response.result;
                Transaction tx;
                for(Map<String,Object> txM : txListMaps) {
                    tx = new Transaction();
                    tx.fromMap(txM);
                    txListObservable.add(tx);
                }
            } else {
                LOG.warning(topic + " topic not supported.");
            }
        } else {
            LOG.warning("Response.result was null!");
        }
    }

}
