package io.onemfive.desktop.views.personal.wallet.details;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.common.Envelope;
import ra.util.Resources;

import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class DetailsWalletView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private BTCWallet activeWallet;
    private TextField walletNameTxt;
    private TextField walletVersionTxt;
    private TextField walletBalanceSatsTxt;
    private TextField walletBalanceBTCTxt;
    private TextField walletUnconfirmedBalanceTxt;
//    private TextField walletImmatureBalanceTxt;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("personalView.wallet.active"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletNameTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), "").second;
        walletNameTxt.setMaxWidth(300);
        walletVersionTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.version"), "").second;
        walletVersionTxt.setMaxWidth(300);
        walletBalanceSatsTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance"), "").second;
        walletBalanceSatsTxt.setMaxWidth(300);
        walletBalanceBTCTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance.btc"), "").second;
        walletBalanceBTCTxt.setMaxWidth(300);
        walletUnconfirmedBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.unconfirmedBalance"), "").second;
        walletUnconfirmedBalanceTxt.setMaxWidth(300);
//        walletImmatureBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.immatureBalance"), "").second;

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        activeWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
        if(activeWallet==null) {
            sendRequest(new GetWalletInfo());
        } else {
            updateWalletView();
        }
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");

        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");
        Envelope e = (Envelope)object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(response.error!=null) {
            if(response.error.code == -1) {
                LOG.warning("Incorrect request: "+response.error.message);
            } else {
                LOG.warning(response.error.toJSON());
            }
        }
        if(response.result!=null) {
            activeWallet = new BTCWallet();
            activeWallet.fromMap((Map<String,Object>)response.result);
        }
//        Object cmdObj = e.getValue(RPCCommand.NAME);
//        GetWalletInfo request = new GetWalletInfo();
//        if(cmdObj instanceof String)
//            request.fromJSON((String)cmdObj);
//        else if(cmdObj instanceof Map)
//            request.fromMap((Map<String,Object>)cmdObj);
//        if(request.wallet.getName()!=null) {
//            activeWallet = request.wallet;
//        }
        updateWalletView();
        LOG.info("Model updated.");
    }

    private void updateWalletView() {
        if(activeWallet!=null) {
            if (activeWallet.getName().isEmpty())
                walletNameTxt.setText("Default");
            else
                walletNameTxt.setText(activeWallet.getName());
            walletVersionTxt.setText(activeWallet.getVersion().toString());
            walletBalanceSatsTxt.setText(activeWallet.getBalance().value().toString());
            walletBalanceBTCTxt.setText(((double)(activeWallet.getBalance().value().longValue()/100000000))+"");
            walletUnconfirmedBalanceTxt.setText(activeWallet.getUnconfirmedBalance().value().toString());
//            walletImmatureBalanceTxt.setText(activeWallet.getImmatureBalance().value().toString());
        }
    }

    @Override
    public void afterLoad() {
        BTCWallet globalWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
        if(globalWallet!=null && activeWallet!=null && !globalWallet.getName().equals(activeWallet.getName())){
            activeWallet = globalWallet;
            updateWalletView();
        }
    }
}

