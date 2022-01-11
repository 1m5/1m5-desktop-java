package io.onemfive.desktop.views.personal.identities;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.InputTextField;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.overlays.popups.Popup;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.identity.DID;
import ra.common.network.ControlCommand;
import ra.did.DIDService;
import ra.common.Resources;

import java.util.List;

import static io.onemfive.desktop.DesktopClient.*;
import static java.util.Objects.nonNull;

public class IdentitiesView extends ActivatableView implements TopicListener {

    public static final String IDENTITIES_LIST = "IDENTITIES_LIST";
    public static final String ACTIVE_IDENTITY = "ACTIVE_IDENTITY";
    public static final String IDENTITY_ADDED = "IDENTITY_ADDED";

    private GridPane pane;
    private int gridRow = 0;

    private ObservableList<Object> identityDIDs = FXCollections.observableArrayList();
    private ObservableList<String> identityAddresses = FXCollections.observableArrayList();

    private ListView<Object> identitiesList;

    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");
    private final String pwd2Prompt = Resources.get("shared.passphraseAgain");

    private final String fingerprintPrompt = Resources.get("shared.fingerprint");
    private final String addressPrompt = Resources.get("shared.address");
    private final String descriptionPrompt = Resources.get("shared.description");
    private final String generateText = Resources.get("shared.generate");
    private final String deleteText = Resources.get("shared.delete");
    private final String editText = Resources.get("shared.edit");
    private final String addText = Resources.get("shared.add");
    private final String identitiesText = Resources.get("personalIdentitiesView.identities");

    private ComboBox<String> authNAliasComboBox;
    private PasswordTextField authNPwdText;
    private InputTextField identityAliasTxt;
    private PasswordTextField identityPwdText;
    private PasswordTextField identityPwd2Text;
    private InputTextField identityDescription;

    private Button authN;
    private Button addIdentity;
    private Button editIdentity;
    private Button deleteIdentity;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // Authenticate Identity
        authNAliasComboBox = new ComboBox<>();
        authNAliasComboBox.setItems(identityAddresses);
        authNAliasComboBox.getStyleClass().add("jfx-combo-box");
        authNAliasComboBox.setValue("Anonymous");
        authNPwdText = new PasswordTextField();
        authNPwdText.setVisible(false);
        authN = new AutoTooltipButton(authNText);
        authN.setDefaultButton(true);
        authN.getStyleClass().add("action-button");
        authN.setVisible(false);
        HBox authNHBox = new HBox(Layout.GRID_GAP, authNAliasComboBox, authNPwdText, authN);

        // Identities
        Label identitiesLabel = new Label(identitiesText);

        // Add Identity
        identityAliasTxt = new InputTextField();
        identityAliasTxt.setPromptText(aliasPrompt);
        identityPwdText = new PasswordTextField();
        identityPwdText.setPromptText(pwdPrompt);
        identityPwd2Text = new PasswordTextField();
        identityPwd2Text.setPromptText(pwd2Prompt);
        identityDescription = new InputTextField();
        identityDescription.setPromptText(descriptionPrompt);
        addIdentity = new AutoTooltipButton(addText);
        addIdentity.getStyleClass().add("action-button");
        editIdentity = new AutoTooltipButton(editText);
        editIdentity.getStyleClass().add("action-raised");
        deleteIdentity = new AutoTooltipButton(deleteText);
        deleteIdentity.getStyleClass().add("button-raised");

        // List Identities
        identitiesList = new ListView<>();
        identitiesList.setPrefSize(800, 250);
        identitiesList.setItems(identityDIDs);
        identitiesList.setEditable(false);
        identitiesList.getStyleClass().add("listView");

        VBox identityVBox = new VBox(Layout.GRID_GAP, identitiesLabel, identityAliasTxt, identityPwdText, identityPwd2Text, identityDescription, addIdentity, identitiesList);

        HBox mainHBox = new HBox(Layout.GRID_GAP, identityVBox);

        pane.add(authNHBox, 0, gridRow);
        pane.add(mainHBox, 0, ++gridRow);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        if(nonNull(DesktopClient.getActivePersonalDID())) {
            authNAliasComboBox.setValue(getActivePersonalDID().getUsername());
        }

        authNAliasComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean isAnonymous = "Anonymous".equals(authNAliasComboBox.getValue());
                authNPwdText.setVisible(!isAnonymous);
                authN.setVisible(!isAnonymous);
            }
        });

        authN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                if(!authNAliasTxt.getText().isEmpty() && !authNPwdText.getText().isEmpty()) {
//                    Envelope e = Envelope.documentFactory();
//                    // 3. Update UI
//                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
//                    // 2. Load ordered Contacts
//                    e.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
//                    // 1. Authenticate
//                    DID did = new DID();
//                    did.setUsername(authNAliasTxt.getText());
//                    did.setPassphrase(authNPwdText.getText());
//                    did.setPassphrase2(authNPwdText.getText());
//                    AuthNRequest ar = new AuthNRequest();
//                    ar.keyRingUsername = did.getUsername();
//                    ar.keyRingPassphrase = did.getPassphrase();
//                    ar.alias = did.getUsername(); // use username as default alias
//                    ar.aliasPassphrase = did.getPassphrase(); // just use same passphrase
//                    ar.autoGenerate = true;
//                    e.setDID(did);
//                    e.addData(AuthNRequest.class, ar);
//                    e.addRoute(KeyRingService.class, KeyRingService.OPERATION_AUTHN);
//                    // Send
//                    DesktopBusClient.deliver(e);
//                }
            }
        });

        addIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(identityAddresses.size()==10) {
                    new Popup().information(Resources.get("personalIdentifiesView.maxIdentities"))
                            .closeButtonText(Resources.get("shared.ok"))
                            .show();
                    return;
                }
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
                    e.addNVP(VIEW_OP, IDENTITY_ADDED);
                    e.addData(DID.class, did);
                    e.addRoute(DIDService.class, DIDService.OPERATION_SAVE_IDENTITY);
                    DesktopClient.deliver(e);
                }
            }
        });

        editIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info(actionEvent.toString());
            }
        });

        deleteIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = identitiesList.getSelectionModel().getSelectedIndex();
                if(index >= 0) {
                    String itemStr = identityAddresses.get(index);
                    LOG.info(itemStr);

                }
            }
        });

        updateIdentitiesList();
        updateContactsList();
    }

    @Override
    protected void deactivate() {
        authN.setOnAction(null);
        addIdentity.setOnAction(null);
        editIdentity.setOnAction(null);
        deleteIdentity.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating Identities View model...");
        Envelope e = (Envelope) object;
        switch (topic) {
            case IDENTITIES_LIST: {
                List<DID> identities = (List<DID>)e.getValue("identities");
                identitiesList.getItems().clear();
                identitiesList.getItems().addAll(identities);
                identityAddresses.add(identityAliasTxt.getText());
                break;
            }
            case ACTIVE_IDENTITY: {
                DID did = (DID)e.getValue("activeIdentity");
                if(nonNull(did)) {
                    DesktopClient.setActivePersonalDID(did);
                }
                break;
            }
            case IDENTITY_ADDED: {
                identityAliasTxt.setText(null);
                identityPwdText.setText(null);
                identityPwd2Text.setText(null);
                identityDescription.setText(null);
                updateIdentitiesList();
            }
        }
    }

    private void updateIdentitiesList() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(VIEW_NAME, IdentitiesView.class.getName());
        e.addNVP(VIEW_OP, IDENTITIES_LIST);
        e.addRoute(DIDService.class, DIDService.OPERATION_GET_IDENTITIES);
        DesktopClient.deliver(e);
    }

    private void updateContactsList() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP("contactsStart",1);
        e.addNVP("contactsNumber", 10);
        e.addNVP(VIEW_NAME, IdentitiesView.class.getName());
        e.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
        DesktopClient.deliver(e);
    }
}
