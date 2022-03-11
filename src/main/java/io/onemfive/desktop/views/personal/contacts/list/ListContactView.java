package io.onemfive.desktop.views.personal.contacts.list;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;
import ra.common.network.ControlCommand;
import ra.did.DIDService;

import java.util.List;

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;

public class ListContactView extends ActivatableView implements TopicListener {

    public static final String CONTACTS_LIST = "CONTACTS_LIST";

    private static ObservableList<Object> contactDIDs = FXCollections.observableArrayList();
    private static ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private static ListView<Object> contactsList;
    private Button deleteContact;

    private GridPane pane;
    private int gridRow = 0;

    @Override
    protected void initialize() {
        contactsList = new ListView<>();
        contactsList.setPrefSize(800, 250);
        contactsList.setItems(contactDIDs);
        contactsList.setEditable(false);
        contactsList.getStyleClass().add("listView");
        VBox mainBox = new VBox(Layout.GRID_GAP, contactsList);
        pane.add(mainBox, 0, gridRow);
    }

    @Override
    protected void activate() {
        super.activate();
    }

    @Override
    protected void deactivate() {
        super.deactivate();
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case CONTACTS_LIST: {
                List<DID> contacts = (List<DID>)e.getValue("contacts");
                contactsList.getItems().clear();
                contactsList.getItems().addAll(contacts);
                break;
            }
        }
        LOG.info("Model updated.");
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
