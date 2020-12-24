package io.onemfive.desktop.views.personal.identities;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;
import ra.common.identity.DID;
import ra.did.DIDService;
import ra.keyring.AuthNRequest;
import ra.keyring.KeyRingService;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;


public class IdentitiesView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private DID activeDID;

    private ObservableList<Object> identityAddresses = FXCollections.observableArrayList();
    private ObservableList<Object> contactAddresses = FXCollections.observableArrayList();

    private ListView<Object> identitiesList;
    private ListView<Object> contactsList;

    private final String currentIdentityText = Resources.get("personalIdentitiesView.current");
    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");
    private final String pwd2Prompt = Resources.get("shared.passphraseAgain");
    private final String fingerprintPrompt = Resources.get("shared.fingerprint");
    private final String addressPrompt = Resources.get("shared.address");
    private final String descriptionPrompt = Resources.get("shared.description");
    private final String generateText = Resources.get("shared.generate");
    private final String deleteText = Resources.get("shared.delete");
    private final String addText = Resources.get("shared.add");
    private final String identitiesText = Resources.get("personalIdentitiesView.identities");
    private final String contactsText = Resources.get("personalIdentitiesView.contacts");

    private TextField currentIdentity;
    private TextField authNAliasTxt;
    private TextField authNPwdText;
    private TextField identityAliasTxt;
    private TextField identityPwdText;
    private TextField identityPwd2Text;
    private TextField identityDescription;
    private TextField contactAliasText;
    private TextField contactFingerprintText;
    private TextField contactAddressText;
    private TextField contactDescription;

    private Button authN;
    private Button addIdentity;
    private Button deleteIdentity;
    private Button addContact;
    private Button deleteContact;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // Current Identity
        TitledGroupBg currentIdentityGroup = addTitledGroupBg(pane, gridRow, 2, currentIdentityText);
        GridPane.setColumnSpan(currentIdentityGroup, 1);
        currentIdentity = addCompactTopLabelTextField(pane, ++gridRow, currentIdentityText, aliasPrompt, Layout.FIRST_ROW_DISTANCE).second;

        // Authenticate
        TitledGroupBg authNGroup = addTitledGroupBg(pane, ++gridRow, 2, authNText);
        GridPane.setColumnSpan(authNGroup, 3);
        authNAliasTxt = addInputTextField(pane, ++gridRow, aliasPrompt, Layout.TWICE_FIRST_ROW_DISTANCE);
        authNPwdText = addInputTextField(pane, gridRow, pwdPrompt, Layout.TWICE_FIRST_ROW_DISTANCE);
        authN = addButton(pane, gridRow, authNText, Layout.TWICE_FIRST_ROW_DISTANCE);

        // Identities
        TitledGroupBg identitiesGroup = addTitledGroupBg(pane, ++gridRow, 8, identitiesText, Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(identitiesGroup, 1);

        // Contacts
        TitledGroupBg contactsGroup = addTitledGroupBg(pane, gridRow, 8, contactsText ,Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(contactsGroup, 1);

        // Add Identity / Contact
        identityAliasTxt = addInputTextField(pane, ++gridRow, aliasPrompt);
        contactAliasText = addInputTextField(pane, gridRow, aliasPrompt);

        identityPwdText = addInputTextField(pane, ++gridRow, pwdPrompt);
        contactFingerprintText = addInputTextField(pane, gridRow, fingerprintPrompt);

        identityPwd2Text = addInputTextField(pane, ++gridRow, pwd2Prompt);
        contactAddressText = addInputTextField(pane, gridRow, addressPrompt);

        identityDescription = addInputTextField(pane, ++gridRow, descriptionPrompt);
        contactDescription = addInputTextField(pane, gridRow, descriptionPrompt);

        addIdentity = addButton(pane, ++gridRow, addText);
        addIdentity.getStyleClass().add("button-raised");
        addContact = addButton(pane, gridRow, addText);
        addContact.getStyleClass().add("button-raised");

        deleteIdentity = addButton(pane, ++gridRow, deleteText);
        deleteIdentity.getStyleClass().add("button-raised");
        deleteContact = addButton(pane, gridRow, deleteText);
        deleteContact.getStyleClass().add("button-raised");

        // List Identities
        identitiesList = addTopLabelListView(pane, ++gridRow, identitiesText).second;
        identitiesList.setPrefSize(800, 250);
        identitiesList.setItems(identityAddresses);
        identitiesList.setEditable(false);
        identitiesList.getStyleClass().add("listView");


        // List Contacts
        contactsList = addTopLabelListView(pane, gridRow, contactsText).second;
        contactsList.setPrefSize(800, 250);
        contactsList.setItems(contactAddresses);
        contactsList.setEditable(false);
        contactsList.getStyleClass().add("listView");

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        if(activeDID!=null)
            currentIdentity.setText(activeDID.getUsername()+" : "+activeDID.getPublicKey().getFingerprint());

        authN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!authNAliasTxt.getText().isEmpty() && !authNPwdText.getText().isEmpty()) {
                    Envelope e = Envelope.documentFactory();
                    // 3. Update UI
                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_UPDATE_IDENTITY_VIEW);
                    // 2. Load ordered Contacts
                    e.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
                    // 1. Authenticate
                    DID did = new DID();
                    did.setUsername(authNAliasTxt.getText());
                    did.setPassphrase(authNPwdText.getText());
                    did.setPassphrase2(authNPwdText.getText());
                    AuthNRequest ar = new AuthNRequest();
                    ar.keyRingUsername = did.getUsername();
                    ar.keyRingPassphrase = did.getPassphrase();
                    ar.alias = did.getUsername(); // use username as default alias
                    ar.aliasPassphrase = did.getPassphrase(); // just use same passphrase
                    ar.autoGenerate = true;
                    e.setDID(did);
                    e.addData(AuthNRequest.class, ar);
                    e.addRoute(KeyRingService.class, KeyRingService.OPERATION_AUTHN);
                    // Send
                    DesktopBusClient.deliver(e);
                }
            }
        });

        addIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!identityAliasTxt.getText().isEmpty()
                        && !identityPwdText.getText().isEmpty()
                        && !identityPwd2Text.getText().isEmpty()) {

                } else {
                    // TODO: show in pop up
                    LOG.warning("Alias, pwd, pwd again required.");
                }
            }
        });

        deleteIdentity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = identitiesList.getSelectionModel().getSelectedIndex();
                if(index >= 0) {
                    String itemStr = (String)identityAddresses.get(index);
                    LOG.info(itemStr);
//                    String[] item = itemStr.split(":");

                }
            }
        });

        addContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!contactAliasText.getText().isEmpty()
                        && !contactFingerprintText.getText().isEmpty()
                        && !contactAddressText.getText().isEmpty()) {

                } else {
                    // TODO: show in pop up
                    LOG.warning("Alias, fingerprint, and address are required.");
                }
            }
        });

        deleteContact.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = contactsList.getSelectionModel().getSelectedIndex();
                if(index >= 0) {
                    String itemStr = (String)contactAddresses.get(index);
                    LOG.info(itemStr);
//                    String[] item = itemStr.split(":");

                }
            }
        });

        // Get Identities
//        Envelope e1 = Envelope.documentFactory();
//        e1.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_UPDATE_IDENTITY_VIEW);
//        e1.addRoute(DIDService.class, DIDService.OPERATION_GET_IDENTITIES);
//        DesktopBusClient.deliver(e1);

        // Get Active Identity
//        Envelope e2 = Envelope.documentFactory();
//        e2.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_UPDATE_IDENTITY_VIEW);
//        e2.addRoute(DIDService.class, DIDService.OPERATION_GET_ACTIVE_IDENTITY);
//        DesktopBusClient.deliver(e2);

        // Get Contacts
//        Envelope e3 = Envelope.documentFactory();
//        e3.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_UPDATE_IDENTITY_VIEW);
//        e3.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
//        DesktopBusClient.deliver(e3);

    }

    @Override
    protected void deactivate() {
        currentIdentity.setText("");
        authN.setOnAction(null);
        addIdentity.setOnAction(null);
        deleteIdentity.setOnAction(null);
        addContact.setOnAction(null);
        deleteContact.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        Envelope e = (Envelope) object;

    }
}
