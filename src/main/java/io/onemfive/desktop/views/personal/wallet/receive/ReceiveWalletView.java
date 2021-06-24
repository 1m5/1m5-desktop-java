package io.onemfive.desktop.views.personal.wallet.receive;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TextFieldWithCopyIcon;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.ImageUtil;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ra.btc.*;
import ra.btc.rpc.wallet.GetNewAddress;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.util.Resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ReceiveWalletView extends ActivatableView implements TopicListener {

    private static final String GENERATE_ADDRESS_OP = "GenerateAddress";
    private static final String CHECK_TRANSACTION_OP = "CheckTransaction";

    private GridPane pane;
    private int gridRow = 0;

    private ImageView qrcodeImageView;
    private TextFieldWithCopyIcon addressForReceiving;
    private Button generateAddressButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg listWalletGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("personalView.wallet.receive"));
        GridPane.setColumnSpan(listWalletGroup, 3);

        qrcodeImageView = new ImageView();
        qrcodeImageView.setFitHeight(200);
        qrcodeImageView.setFitWidth(200);
        qrcodeImageView.setPreserveRatio(true);
        qrcodeImageView.setSmooth(true);
        qrcodeImageView.setCache(true);
        qrcodeImageView.setVisible(false);
        pane.add(qrcodeImageView, 0, gridRow++);
        addressForReceiving = addCompactTopLabelTextFieldWithCopyIcon(pane, gridRow++,Resources.get("personalView.wallet.receive.address"), "").second;
        addressForReceiving.setMaxWidth(300);
        generateAddressButton = addPrimaryActionButton(pane, gridRow++, 2, Resources.get("personalView.wallet.receive.generate"), Layout.FIRST_ROW_DISTANCE);
        generateAddressButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        generateAddressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                generateAddress();
            }
        });

        generateAddress();

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        generateAddressButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");
        Envelope e = (Envelope)object;
        String json = new String((byte[])e.getContent());
        if (GENERATE_ADDRESS_OP.equals(topic)) {
            GetNewAddress request = new GetNewAddress();
            request.fromJSON(json);
            if (request.error == null) {
                LOG.info("Successful address creation.");
                addressForReceiving.setText(request.getAddress());
                qrcodeImageView.setImage(ImageUtil.getQRCode(request.getAddress(), 200, 200, null));
                qrcodeImageView.setVisible(true);
            }
        }
        LOG.info("Model updated.");
    }

    private void generateAddress() {
//        BTCWallet activeWallet = (BTCWallet) DesktopClient.getGlobal("activeWallet");
//        if(activeWallet!=null) {
            Envelope e = Envelope.documentFactory();
            e.setCommandPath(ControlCommand.Send.name());
            e.addNVP(DesktopClient.VIEW_NAME, ReceiveWalletView.class.getName());
            e.addNVP(DesktopClient.VIEW_OP, GENERATE_ADDRESS_OP);
//            e.addNVP(RPCCommand.NAME, new GetNewAddress(activeWallet.getName(), "", AddressType.LEGACY).toMap());
        e.addNVP(RPCCommand.NAME, new GetNewAddress("", "", AddressType.LEGACY).toMap());
            e.addRoute(BitcoinService.class, BitcoinService.OPERATION_RPC_REQUEST);
            DesktopClient.deliver(e);
//        }
    }

}

