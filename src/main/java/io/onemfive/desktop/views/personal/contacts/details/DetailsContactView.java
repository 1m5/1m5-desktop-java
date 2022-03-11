package io.onemfive.desktop.views.personal.contacts.details;

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

public class DetailsContactView extends ActivatableView implements TopicListener {

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
    private Button saveContact;
    private Button deleteContact;

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
        saveContact = new AutoTooltipButton(Resources.get("shared.save"));
        saveContact.getStyleClass().add("action-button");
        deleteContact = new AutoTooltipButton(Resources.get("shared.delete"));
        deleteContact.getStyleClass().add("button-raised");
        HBox actionHBox = new HBox(Layout.GRID_GAP, saveContact, deleteContact);
        VBox mainBox = new VBox(Layout.GRID_GAP, actionHBox);
        pane.add(mainBox, 0, gridRow);
    }

    @Override
    protected void activate() {
        saveContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info(actionEvent.toString());
            }
        });

        deleteContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info(actionEvent.toString());
            }
        });
    }

    @Override
    protected void deactivate() {
        saveContact.setOnAction(null);
        deleteContact.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {

        }
        LOG.info("Model updated.");
    }
}
