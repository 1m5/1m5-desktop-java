package io.onemfive.desktop.views.personal.wallet.receive;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TextFieldWithCopyIcon;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ra.btc.*;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.AddressType;
import ra.btc.rpc.wallet.GetNewAddress;
import ra.common.Envelope;
import ra.util.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;

import static io.onemfive.desktop.util.FormBuilder.*;

public class ReceiveWalletView extends ActivatableView implements TopicListener {

    private static final String GENERATE_ADDRESS_OP = "GenerateAddress";

    private GridPane pane;
    private int gridRow = 0;

    private ImageView qrcodeImageView;
    private TextFieldWithCopyIcon addressForReceiving;
    private Button generateAddressButton;
    private final int qrCodeWidth = 200;
    private final int qrCodeHeight = 200;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        addTitledGroupBg(pane, gridRow, 3, Resources.get("personalView.wallet.receive"));
        // TODO: QR Code not generating correctly - wallets do not recognize it
        qrcodeImageView = new ImageView();
        qrcodeImageView.setFitHeight(qrCodeHeight);
        qrcodeImageView.setFitWidth(qrCodeWidth);
        qrcodeImageView.setPreserveRatio(true);
        qrcodeImageView.setSmooth(true);
        qrcodeImageView.setCache(true);
        qrcodeImageView.setVisible(false);
        pane.add(qrcodeImageView, 0, gridRow++);
        addressForReceiving = addCompactTopLabelTextFieldWithCopyIcon(pane, gridRow++,Resources.get("personalView.wallet.receive.address"), "").second;
        addressForReceiving.setMaxWidth(500);
        generateAddressButton = addPrimaryActionButton(pane, gridRow++, Resources.get("personalView.wallet.receive.generate"), Layout.FIRST_ROW_DISTANCE);
        generateAddressButton.getStyleClass().add("action-button");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        generateAddressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sendRequest(new GetNewAddress((DesktopClient.getActiveWallet()).getName(), "", AddressType.BECH32));
            }
        });

        sendRequest(new GetNewAddress((DesktopClient.getActiveWallet()).getName(), "", AddressType.BECH32));

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
//        Object cmdObj = e.getValue(RPCCommand.NAME);
        if (GENERATE_ADDRESS_OP.equals(topic)) {
            RPCResponse response = DesktopClient.getResponse(e);
            if(response.error!=null) {
                if(response.error.code == -1) {
                    LOG.warning("Incorrect request: "+response.error.message);
                } else {
                    LOG.warning(response.error.toJSON());
                }
            }
            if(response.result!=null) {
                String address = (String)response.result;
                addressForReceiving.setText(address);
                // bitcoin:<address>?amount=<amount>&label=<label>&message=<message>
                // with all parameters after address being optional and/or interchangeable,
                // with amount being in BTC units not Satoshis. All <parameters> should be
                // URI encoded (e.g. spaces become %20 etc).
                String qrCodeFormat = "bitcoin:"+address.toUpperCase();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BufferedImage bufferedImage = null;
                try {
                    BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeFormat, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);
                    bufferedImage = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
                    bufferedImage.createGraphics();

                    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, qrCodeWidth, qrCodeHeight);
                    graphics.setColor(Color.BLACK);

                    for (int i = 0; i < qrCodeHeight; i++) {
                        for (int j = 0; j < qrCodeWidth; j++) {
                            if (byteMatrix.get(i, j)) {
                                graphics.fillRect(i, j, 1, 1);
                            }
                        }
                    }
                    qrcodeImageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                    qrcodeImageView.setVisible(true);
                } catch (WriterException ex) {
                    LOG.warning(ex.getLocalizedMessage());
                    return;
                }
            }
        } else {
            LOG.warning(topic + " topic not supported in "+ReceiveWalletView.class.getName());
        }
        LOG.info("Model updated.");
    }

}

