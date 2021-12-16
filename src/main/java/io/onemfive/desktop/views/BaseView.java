package io.onemfive.desktop.views;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Transitions;
import javafx.fxml.FXML;
import javafx.scene.Node;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCRequest;
import ra.btc.uses.UseRequest;
import ra.common.Envelope;
import ra.common.network.ControlCommand;

import java.util.logging.Logger;

public abstract class BaseView implements View {

    protected final Logger LOG = Logger.getLogger(this.getClass().getName());

    @FXML
    protected Node root;
    protected Transitions transitions;
    protected DesktopClient desktopClient;

    public BaseView() {}

    public void setDesktopClient(DesktopClient client) {
        desktopClient = client;
    }

    @Override
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void afterLoad() {}

}
