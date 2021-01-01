package io.onemfive.desktop.views.community.social;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ra.common.identity.DID;
import ra.util.Resources;

import java.util.List;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SocialView extends ActivatableView implements TopicListener {

    private final ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private GridPane pane;
    private int gridRow = 0;

    private String contactAddress = Resources.get("communityView.social.contactAddress");
    private ComboBox<String> contactAddressList;

    private String messages = Resources.get("communityView.social.messages");
    private TextArea messagesTextArea;

    private String enterText = Resources.get("communityView.social.enterText");
    private TextField textTextField;

    private String sendText = Resources.get("shared.send");
    private Button sendTextButton = new Button(sendText);

    private Label notes;
    private int msgCount = 0;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        contactAddressList = addComboBox(pane, ++gridRow, Resources.get("communityView.social.contactAddress"));
        contactAddressList.setItems(contactAddresses);
        contactAddressList.maxWidth(250d);
        // TODO: Load past messages for last selected conversation
        // TODO: Change from text area to three columns
        messagesTextArea = addCompactTopLabelTextAreaWithText(pane, "", ++gridRow, Resources.get("communityView.social.messages"), true).second;
        messagesTextArea.maxWidth(250d);

        textTextField = addInputTextField(pane, ++gridRow, Resources.get("communityView.social.enterText"));
        textTextField.maxWidth(250d);
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
                msgCount++;
                if(msgCount%2>0)
                    modelUpdated("newLocalMessage", txtToSend);
                else
                    modelUpdated("newRemoteMessage", txtToSend);
//                Envelope e = Envelope.documentFactory();
//
//                DesktopBusClient.deliver(e);
                textTextField.setText(null);
            };
        });
    }

    @Override
    protected void deactivate() {
        contactAddresses.clear();
        sendTextButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        switch (topic) {
            case "contacts": {
                contactAddresses.clear();
                for(DID c : (List<DID>)object) {
                    contactAddresses.add(c.getUsername() + ": "+c.getPublicKey().getAddress());
                }
                break;
            }
            case "newRemoteMessage": {
                if(messagesTextArea.getText().isEmpty())
                    messagesTextArea.appendText("\t"+object);
                else {
                    messagesTextArea.appendText("\n\n\t" + object);
                }
                break;
            }
            case "newLocalMessage": {
                if(messagesTextArea.getText().isEmpty())
                    messagesTextArea.appendText((String)object);
                else {
                    messagesTextArea.appendText("\n\n" + object);
                }
                break;
            }
        }
    }
}
