package io.onemfive.desktop.views.personal.contacts;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;
import ra.common.network.ControlCommand;
import ra.did.DIDService;

import java.util.List;

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;
import static io.onemfive.desktop.DesktopClient.VIEW_OP;
import static java.util.Objects.nonNull;

public class ContactsView extends ActivatableView implements TopicListener {

    public static final String CONTACTS_LIST = "CONTACTS_LIST";
    public static final String CONTACT_ADDED = "CONTACT_ADDED";

    private GridPane pane;
    private int gridRow = 0;

    private ObservableList<Object> contactDIDs = FXCollections.observableArrayList();
    private ObservableList<String> contactAddresses = FXCollections.observableArrayList();

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

        HBox mainHBox = new HBox(Layout.GRID_GAP, contactVBox);
        pane.add(mainHBox, 0, gridRow);

        LOG.info("Initialized.");
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

        updateContactsList();
    }

    @Override
    protected void deactivate() {
        addContact.setOnAction(null);
        editContact.setOnAction(null);
        deleteContact.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating Identities View model...");
        Envelope e = (Envelope) object;
        switch (topic) {
            case CONTACTS_LIST: {
                List<DID> contacts = (List<DID>)e.getValue("contacts");
                contactsList.getItems().clear();
                contactsList.getItems().addAll(contacts);
                break;
            }
            case CONTACT_ADDED: {
                contactAddressText.setText(null);
                contactAliasText.setText(null);
                contactFingerprintText.setText(null);
                contactDescription.setText(null);
                updateContactsList();
            }
        }
    }

    private void updateContactsList() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP("contactsStart",1);
        e.addNVP("contactsNumber", 10);
        e.addNVP(VIEW_NAME, ContactsView.class.getName());
        e.addNVP(VIEW_OP, CONTACTS_LIST);
        e.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
        DesktopClient.deliver(e);
    }
}
