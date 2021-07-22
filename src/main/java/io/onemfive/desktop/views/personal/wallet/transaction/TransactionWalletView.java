package io.onemfive.desktop.views.personal.wallet.transaction;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.receive.ReceiveWalletView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class TransactionWalletView extends ActivatableView implements TopicListener {

    private static final String LIST_TRANSACTIONS_OP = "ListTransactions";
    private static final String GET_TRANSACTION_INFO_OP = "GetTransactionInfo";

    private GridPane pane;
    private int gridRow = 0;

    private final ObservableList<String> txIdsObservable = FXCollections.observableArrayList(DesktopClient.getBitcoinTransactions());
    private ComboBox<String> txListView;


    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;
        addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.transactions"), Layout.FIRST_ROW_DISTANCE);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {

    }

    @Override
    protected void deactivate() {

    }

    @Override
    public void modelUpdated(String topic, Object object) {

    }

    private void listTransactions() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(DesktopClient.VIEW_NAME, ReceiveWalletView.class.getName());
        e.addNVP(DesktopClient.VIEW_OP, LIST_TRANSACTIONS_OP);
        e.addNVP(RPCCommand.NAME, new ListWallets().toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopClient.deliver(e);
    }
}
