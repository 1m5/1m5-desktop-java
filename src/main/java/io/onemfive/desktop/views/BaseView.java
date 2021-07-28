package io.onemfive.desktop.views;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Transitions;
import io.onemfive.desktop.views.personal.wallet.info.InfoWalletView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.wallet.ListWallets;
import ra.common.Envelope;
import ra.common.network.ControlCommand;

import java.util.logging.Logger;

public abstract class BaseView implements View {

    protected final Logger LOG = Logger.getLogger(this.getClass().getName());

    @FXML
    protected Node root;
    protected Transitions transitions;

    public BaseView() {}

    @Override
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void afterLoad() {}

    protected void sendRequest(RPCRequest request) {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(DesktopClient.VIEW_NAME, getClass().getName());
        e.addNVP(DesktopClient.VIEW_OP, request.method);
        e.addNVP(RPCCommand.NAME, request.toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopClient.deliver(e);
    }

}
