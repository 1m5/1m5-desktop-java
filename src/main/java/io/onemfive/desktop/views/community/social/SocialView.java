package io.onemfive.desktop.views.community.social;

import io.onemfive.desktop.DesktopBusClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;
import ra.common.identity.DID;
import ra.util.Resources;

import java.util.List;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SocialView extends ActivatableView implements TopicListener {

    private ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private GridPane pane;
    private int gridRow = 0;

    private String contactAddress = Resources.get("communityView.social.contactAddress");
    private TextField addressTextField;

    private String messages = Resources.get("communityView.social.messages");
    private TextArea messagesTextArea;

    private String enterText = Resources.get("communityView.social.enterText");
    private TextField textTextField;

    private String sendText = Resources.get("shared.send");
    private Button sendTextButton = new Button(sendText);

    public void updateContacts(List<DID> contacts) {
        contactAddresses.clear();
        for(DID c : contacts) {
            contactAddresses.add(c.getUsername() + ": "+c.getPublicKey().getAddress());
        }
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 1, Resources.get("communityView.social.messaging"));
        GridPane.setColumnSpan(statusGroup, 1);

        addressTextField = addInputTextField(pane, ++gridRow, Resources.get("communityView.social.contactAddress"), Layout.TWICE_FIRST_ROW_DISTANCE);
        // TODO: Load past messages for last selected conversation
        messagesTextArea = addCompactTopLabelTextAreaWithText(pane, "", ++gridRow, Resources.get("communityView.social.messages"), true).second;
        textTextField = addInputTextField(pane, ++gridRow, Resources.get("communityView.social.enterText"));
        sendTextButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("shared.send"), Layout.FIRST_ROW_DISTANCE);

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
//                Envelope e = Envelope.documentFactory();
//
//                DesktopBusClient.deliver(e);
            };
        });
    }

    @Override
    protected void deactivate() {
        sendTextButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {

    }
}
