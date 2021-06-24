package io.onemfive.desktop.views.personal.wallet.transaction;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.receive.ReceiveWalletView;
import javafx.scene.layout.GridPane;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.common.network.ControlCommand;

public class TransactionWalletView extends ActivatableView implements TopicListener {

    private static final String LIST_TRANSACTIONS_OP = "ListTransactions";

    private GridPane pane;
    private int gridRow = 0;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

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
