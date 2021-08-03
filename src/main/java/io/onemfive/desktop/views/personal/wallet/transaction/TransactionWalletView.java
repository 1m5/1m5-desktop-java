package io.onemfive.desktop.views.personal.wallet.transaction;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ra.btc.Transaction;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.tx.GetRawTransaction;
import ra.common.Envelope;
import ra.util.Resources;
import ra.util.Wait;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class TransactionWalletView extends ActivatableView implements TopicListener {

    private static final String GET_TRANSACTION_INFO_OP = "GetTransactionInfo";

    private GridPane pane;
    private int gridRow = 0;
    private TableView txView;

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
        txView.setItems(DesktopClient.getBitcoinTransactions());

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        lookupTransactions();
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
            if (GetRawTransaction.NAME.equals(topic)) {
                Transaction tx = new Transaction();
                tx.fromMap((Map<String,Object>)response.result);
                DesktopClient.addBitcoinTransaction(tx);

            } else {
                LOG.warning(topic + " topic not supported.");
            }
        } else {
            LOG.warning("Response.result was null!");
        }
    }

    private void lookupTransactions() {
        if(DesktopClient.getBitcoinTransactions().size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Transaction> txs = DesktopClient.getBitcoinTransactions();
                    while(txs.size() > 0) {
                        // Check for Transactions older than 6 hours and remove
                        long now = new Date().getTime();
                        List<Integer> remove = new ArrayList<>();
                        int i = 0;
                        for(Transaction tx : txs) {
                            if(now > (tx.time * 6 * 60 * 60 * 1000)) { // kick out transactions older than 6 hours
                                remove.add(i);
                            }
                            i++;
                        }
                        for(Integer j : remove) {
                            txs.remove(j);
                        }
                        for(Transaction tx : txs) {
                            sendRequest(new GetRawTransaction(tx.txid));
                        }
                        Wait.aMin(10);
                    }
                }
            }).start();
        }
    }

}
