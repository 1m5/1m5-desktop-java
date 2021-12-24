package io.onemfive.desktop.views.community.social;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.wallet.transaction.TransactionRenderer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.btc.Transaction;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.ListTransactions;
import ra.common.Envelope;
import ra.common.identity.DID;
import ra.i2p.I2PService;
import ra.networkmanager.NetworkManagerService;
import ra.common.Resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.onemfive.desktop.util.FormBuilder.*;
import static java.util.Objects.nonNull;

public class SocialView extends ActivatableView implements TopicListener {

    private final ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private GridPane pane;
    private int gridRow = 0;

    private String contactAddress = Resources.get("communityView.social.contactAddress");
    private ComboBox<String> contactAddressList;

    private String messageTxt = Resources.get("communityView.social.messages");
//    private TextArea messagesTextArea;
    private List<Label> messages = new ArrayList<>();
    private VBox chatBox = new VBox(5);

    private String enterText = Resources.get("communityView.social.enterText");
    private TextField textTextField;

    private String sendText = Resources.get("shared.send");
    private Button sendTextButton = new Button(sendText);

    private Label notes;
    private int index = 0;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        contactAddressList = addComboBox(pane, ++gridRow, Resources.get("communityView.social.contactAddress"));
        contactAddressList.setItems(contactAddresses);
        contactAddressList.maxWidth(250d);
        // TODO: Load past messages for last selected conversation from personal drive
        chatBox.setMinHeight(400d);
        chatBox.setMinWidth(200d);
//        messagesTextArea = addCompactTopLabelTextAreaWithText(pane, "", ++gridRow, Resources.get("communityView.social.messages"), true).second;
//        messagesTextArea.maxWidth(250d);

        textTextField = addInputTextField(pane, ++gridRow, Resources.get("communityView.social.enterText"));
        textTextField.setMinWidth(100d);
        sendTextButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("shared.send"), Layout.FIRST_ROW_DISTANCE);

        TitledGroupBg notesGroup = addTitledGroupBg(pane, ++gridRow, 1, Resources.get("shared.notes"), Layout.TWICE_FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(notesGroup, 1);
        String note = "Currently, only simple messaging is supported and only a remote peer I2P fingerprint for the address field. Future releases will move towards selecting DIDs from the local active DID's contact list using active DID as the originator.";
        notes = addMultilineLabel(pane, ++gridRow, note, Layout.FIRST_ROW_DISTANCE);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        sendTextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("sendTextButton clicked...");
                String txtToSend = textTextField.getText();
                LOG.info("Sending text: "+txtToSend);
                index++;
                Envelope e = Envelope.documentFactory();

                e.addRoute(I2PService.class.getName(), I2PService.OPERATION_SEND);
                e.addRoute(NetworkManagerService.class.getName(), NetworkManagerService.OPERATION_SEND);
                e.addContent(txtToSend);
                // DesktopClient.deliver(e);
                textTextField.setText(null);
            };
        });
    }

    @Override
    protected void deactivate() {
        sendTextButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        Envelope e = (Envelope) object;
        RPCResponse response = DesktopClient.getResponse(e);
        if(nonNull(response.error)) {
            if(response.error.code == -1) {
                LOG.warning("Incorrect request: "+response.error.message);
            } else {
                LOG.warning(response.error.toJSON());
            }
        }
        if(nonNull(response.result)) {
            if ("contacts".equals(topic)) {
                contactAddresses.clear();
                List<DID> contacts = (List<DID>) response.result;
                for (DID c : contacts) {
                    contactAddresses.add(c.getUsername() + ": " + c.getPublicKey().getFingerprint());
                }
                contactAddresses.sort(Comparator.naturalOrder());
            } else if("message".equals(topic)) {

            } else {
                LOG.warning(topic + " topic not supported.");
            }
        } else {
            LOG.warning("Response.result was null!");
        }
    }
}
