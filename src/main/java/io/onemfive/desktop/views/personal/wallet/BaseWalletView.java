package io.onemfive.desktop.views.personal.wallet;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.views.ActivatableView;
import ra.btc.BitcoinService;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCRequest;
import ra.btc.uses.UseRequest;
import ra.common.Envelope;
import ra.common.network.ControlCommand;

public class BaseWalletView extends ActivatableView {

    protected void sendBTCRequest(RPCRequest request) {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(DesktopClient.VIEW_NAME, getClass().getName());
        e.addNVP(DesktopClient.VIEW_OP, request.method);
        e.addNVP(RPCCommand.NAME, request.toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
        DesktopClient.deliver(e);
    }

    protected void sendBTCRequest(UseRequest useRequest) {
        Envelope e = Envelope.documentFactory();
        e.addNVP(DesktopClient.VIEW_NAME, getClass().getName());
        e.addNVP(UseRequest.class.getName(), useRequest.toMap());
        e.addRoute(BitcoinService.class, BitcoinService.OPERATION_USE_REQUEST);
        DesktopClient.deliver(e);
    }

}
