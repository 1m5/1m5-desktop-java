package io.onemfive.desktop.views.personal.identities.create;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.overlays.popups.Popup;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;
import ra.did.DIDService;

import static io.onemfive.desktop.DesktopClient.*;

public class CreateIdentityView extends ActivatableView implements TopicListener {

    public static final String IDENTITY_CREATED = "IDENTITY_CREATED";

    private GridPane pane;
    private int gridRow = 0;

    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");
    private final String pwd2Prompt = Resources.get("shared.passphraseAgain");

    private final String descriptionPrompt = Resources.get("shared.description");
    private final String addText = Resources.get("shared.add");
    private final String identitiesText = Resources.get("personalIdentitiesView.identities");

    private InputTextField identityAliasTxt;
    private PasswordTextField identityPwdText;
    private PasswordTextField identityPwd2Text;
    private InputTextField identityDescription;

    private Button createIdentity;

    @Override
    protected void initialize() {
        super.initialize();

        // Create Identity
        identityAliasTxt = new InputTextField();
        identityAliasTxt.setPromptText(aliasPrompt);
        identityPwdText = new PasswordTextField();
        identityPwdText.setPromptText(pwdPrompt);
        identityPwd2Text = new PasswordTextField();
        identityPwd2Text.setPromptText(pwd2Prompt);
        identityDescription = new InputTextField();
        identityDescription.setPromptText(descriptionPrompt);
        createIdentity = new AutoTooltipButton(addText);
        createIdentity.getStyleClass().add("action-button");

        VBox identityVBox = new VBox(Layout.GRID_GAP, identityAliasTxt, identityPwdText, identityPwd2Text, identityDescription, createIdentity);

        HBox mainHBox = new HBox(Layout.GRID_GAP, identityVBox);

        pane.add(mainHBox, 0, gridRow);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {

        createIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(identityAliasTxt.getText().isEmpty()
                        || identityPwdText.getText().isEmpty()
                        || identityPwd2Text.getText().isEmpty()) {
                    new Popup().information(Resources.get("personalIdentifiesView.identityRequired"))
                            .closeButtonText(Resources.get("shared.ok"))
                            .show();
                    return;
                }
                if(!identityPwdText.getText().equals(identityPwd2Text.getText())) {
                    new Popup().information(Resources.get("personalIdentitiesView.pwdMustBeSame"))
                            .closeButtonText(Resources.get("shared.ok"))
                            .show();
                    return;
                }
                if(!identityAliasTxt.getText().isEmpty()) {
                    DID did = new DID();
                    did.setUsername(identityAliasTxt.getText());
                    did.setPassphrase(identityPwdText.getText());
                    did.setPassphrase2(identityPwd2Text.getText());
                    did.setDescription(identityDescription.getText());
                    Envelope e = Envelope.documentFactory();
                    e.addNVP(VIEW_NAME, IdentitiesView.class.getName());
                    e.addNVP(VIEW_OP, IDENTITY_CREATED);
                    e.addData(DID.class, did);
                    e.addRoute(DIDService.class, DIDService.OPERATION_SAVE_IDENTITY);
                    DesktopClient.deliver(e);
                }
            }
        });
    }

    @Override
    protected void deactivate() {
        createIdentity.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case IDENTITY_CREATED: {
                identityAliasTxt.setText(null);
                identityPwdText.setText(null);
                identityPwd2Text.setText(null);
                identityDescription.setText(null);
                // TODO: Navigate to List

            }
        }
        LOG.info("Model updated.");
    }
}
