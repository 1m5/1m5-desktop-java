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

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;
import static io.onemfive.desktop.DesktopClient.VIEW_OP;

public class IdentitiesView extends ActivatableView implements TopicListener {

    public static final String IDENTITIES_LIST = "IDENTITIES_LIST";
    public static final String ACTIVE_IDENTITY = "ACTIVE_IDENTITY";
    public static final String CONTACTS_LIST = "CONTACTS_LIST";
    public static final String IDENTITY_ADDED = "IDENTITY_ADDED";

    private GridPane pane;
    private int gridRow = 0;

    private DID activeDID;

    private ObservableList<Object> identityDIDs = FXCollections.observableArrayList();
    private ObservableList<String> identityAddresses = FXCollections.observableArrayList();
    private ObservableList<Object> contactDIDs = FXCollections.observableArrayList();
    private ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private ListView<Object> identitiesList;
    private ListView<Object> contactsList;

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
    private final String contactsText = Resources.get("personalIdentitiesView.contacts");

    private ComboBox<String> authNAliasComboBox;
    private PasswordTextField authNPwdText;
    private InputTextField identityAliasTxt;
    private PasswordTextField identityPwdText;
    private PasswordTextField identityPwd2Text;
    private InputTextField identityDescription;

    private InputTextField contactAliasText;
    private InputTextField contactFingerprintText;
    private InputTextField contactAddressText;
    private InputTextField contactDescription;

    private Button authN;
    private Button addIdentity;
    private Button editIdentity;
    private Button deleteIdentity;

    private Button addContact;
    private Button editContact;
    private Button deleteContact;

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

        // Contacts
        Label contactsLabel = new Label(contactsText);

        // Add Contact
        contactAliasText = new InputTextField();
        contactAliasText.setPromptText(aliasPrompt);
        contactFingerprintText = new InputTextField();
        contactFingerprintText.setPromptText(fingerprintPrompt);
        contactAddressText = new InputTextField();
        contactAddressText.setPromptText(addressPrompt);
        contactDescription = new InputTextField();
        contactDescription.setPromptText(descriptionPrompt);
        addContact = new AutoTooltipButton(addText);
        addContact.getStyleClass().add("action-button");
        editContact = new AutoTooltipButton(editText);
        editContact.getStyleClass().add("action-button");
        deleteContact = new AutoTooltipButton(deleteText);
        deleteContact.getStyleClass().add("button-raised");

        // List Contacts
        contactsList = new ListView<>();
        contactsList.setPrefSize(800, 250);
        contactsList.setItems(contactDIDs);
        contactsList.setEditable(false);
        contactsList.getStyleClass().add("listView");

        VBox contactVBox = new VBox(Layout.GRID_GAP, contactsLabel, contactAliasText, contactFingerprintText, contactAddressText, contactDescription, addContact, contactsList);

        HBox mainHBox = new HBox(Layout.GRID_GAP, identityVBox, contactVBox);

        pane.add(authNHBox, 0, gridRow);
        pane.add(mainHBox, 0, ++gridRow);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        if(activeDID!=null) {
            authNAliasComboBox.setValue(activeDID.getUsername());
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
                    contactsList.getItems().add(did);
                } else {
                    // TODO: show in pop up
                    LOG.warning("Alias, fingerprint, and address are required.");
                }
            }
        });

        editContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info(actionEvent.toString());
            }
        });

        deleteContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = contactsList.getSelectionModel().getSelectedIndex();
                if(index >= 0) {
                    String itemStr = contactAddresses.get(index);
                    LOG.info(itemStr);

                }
            }
        });

        Envelope e1 = Envelope.documentFactory();
        e1.setCommandPath(ControlCommand.Send.name());
        e1.addNVP(VIEW_NAME, IdentitiesView.class.getName());
        e1.addNVP(VIEW_OP, IDENTITIES_LIST);
        e1.addRoute(DIDService.class, DIDService.OPERATION_GET_IDENTITIES);
        e1.ratchet();
        DesktopClient.deliver(e1);

//        Envelope e2 = Envelope.documentFactory();
//        e2.setCommandPath(ControlCommand.Send.name());
//        e2.addNVP(VIEW_NAME, IdentitiesView.class.getName());
//        e2.addNVP(VIEW_OP, ACTIVE_IDENTITY);
//        e2.addRoute(DIDService.class, DIDService.OPERATION_GET_ACTIVE_IDENTITY);
//        e2.ratchet();
//        DesktopBusClient.deliver(e2);
//
//        Envelope e3 = Envelope.documentFactory();
//        e3.setCommandPath(ControlCommand.Send.name());
//        e3.addNVP(VIEW_NAME, IdentitiesView.class.getName());
//        e3.addNVP(VIEW_OP, CONTACTS_LIST);
//        e3.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
//        e3.ratchet();
//        DesktopBusClient.deliver(e3);

    }

    @Override
    protected void deactivate() {
        authN.setOnAction(null);
        addIdentity.setOnAction(null);
        editIdentity.setOnAction(null);
        deleteIdentity.setOnAction(null);
        addContact.setOnAction(null);
        editContact.setOnAction(null);
        deleteContact.setOnAction(null);
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
                activeDID = did;
                break;
            }
            case CONTACTS_LIST: {

                break;
            }
            case IDENTITY_ADDED: {
                identityAliasTxt.setText(null);
                identityPwdText.setText(null);
                identityPwd2Text.setText(null);
                identityDescription.setText(null);
            }
        }
    }
}
