package io.onemfive.desktop.views.personal.contacts.add;

import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;

public class AddContactView extends ActivatableView implements TopicListener {

    public static final String CONTACT_ADDED = "CONTACT_ADDED";

    private GridPane pane;
    private int gridRow = 0;

    private final String aliasPrompt = Resources.get("shared.alias");
    private final String fingerprintPrompt = Resources.get("shared.fingerprint");
    private final String addressPrompt = Resources.get("shared.address");
    private final String descriptionPrompt = Resources.get("shared.description");

    private InputTextField contactAliasText;
    private InputTextField contactFingerprintText;
    private InputTextField contactAddressText;
    private InputTextField contactDescription;

    private Button addContact;

    @Override
    protected void initialize() {
        contactAliasText = new InputTextField();
        contactAliasText.setPromptText(aliasPrompt);
        contactFingerprintText = new InputTextField();
        contactFingerprintText.setPromptText(fingerprintPrompt);
        contactAddressText = new InputTextField();
        contactAddressText.setPromptText(addressPrompt);
        contactDescription = new InputTextField();
        contactDescription.setPromptText(descriptionPrompt);
        addContact = new AutoTooltipButton(Resources.get("shared.add"));
        addContact.getStyleClass().add("action-button");
        VBox mainBox = new VBox(Layout.GRID_GAP, contactAliasText, contactFingerprintText, contactAddressText, contactDescription, addContact);
        pane.add(mainBox, 0, gridRow);
    }

    @Override
    protected void activate() {
        addContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!contactAliasText.getText().isEmpty()
                        && !contactFingerprintText.getText().isEmpty()
                        && !contactAddressText.getText().isEmpty()) {
                    DID did = new DID();
                    did.setUsername(contactAliasText.getText());
                    did.getPublicKey().setFingerprint(contactFingerprintText.getText());
                    did.getPublicKey().setAddress(contactAddressText.getText());
                } else {
                    // TODO: show in pop up
                    LOG.warning("Alias, fingerprint, and address are required.");
                }
            }
        });
    }

    @Override
    protected void deactivate() {
        addContact.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case CONTACT_ADDED: {
                contactAddressText.setText(null);
                contactAliasText.setText(null);
                contactFingerprintText.setText(null);
                contactDescription.setText(null);
            }
        }
        LOG.info("Model updated.");
    }
}
