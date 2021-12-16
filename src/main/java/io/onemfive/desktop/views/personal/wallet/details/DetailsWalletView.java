package io.onemfive.desktop.views.personal.wallet.details;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.BaseWalletView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.btc.BTCWallet;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.currency.crypto.BTC;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;

public class DetailsWalletView extends BaseWalletView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private BTCWallet activeWallet;
    private TextField walletNameTxt;
    private TextField walletVersionTxt;
    private TextField walletBalanceSatsTxt;
    private TextField walletBalanceBTCTxt;
    private TextField walletUnconfirmedBalanceTxt;
    private TextField walletImmatureBalanceTxt;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg walletGroup = addTitledGroupBg(pane, ++gridRow, 7, Resources.get("personalView.wallet.active"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(walletGroup, 1);
        walletNameTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.name"), "").second;
        walletNameTxt.setMaxWidth(300);
        walletVersionTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.version"), "").second;
        walletVersionTxt.setMaxWidth(300);
        walletBalanceBTCTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance.btc"), "").second;
        walletBalanceBTCTxt.setMaxWidth(300);
        walletBalanceSatsTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.balance"), "").second;
        walletBalanceSatsTxt.setMaxWidth(300);
        walletUnconfirmedBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.unconfirmedBalance"), "").second;
        walletUnconfirmedBalanceTxt.setMaxWidth(300);
        walletImmatureBalanceTxt = addTopLabelReadOnlyTextField(pane, ++gridRow, Resources.get("personalView.wallet.immatureBalance"), "").second;
        walletImmatureBalanceTxt.setMaxWidth(300);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        activeWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
        if(activeWallet==null) {
            sendBTCRequest(new GetWalletInfo());
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
            walletBalanceSatsTxt.setText(activeWallet.getBalance().toString());
            walletBalanceBTCTxt.setText(new BigDecimal(activeWallet.getBalance())
                    .divide(new BigDecimal(BTC.SATS_PER_BITCOIN),
                            new MathContext(BTC.PRECISION, RoundingMode.HALF_EVEN)).toString());
            walletUnconfirmedBalanceTxt.setText(activeWallet.getUnconfirmedBalance().toString());
            walletImmatureBalanceTxt.setText(activeWallet.getImmatureBalance().toString());
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

