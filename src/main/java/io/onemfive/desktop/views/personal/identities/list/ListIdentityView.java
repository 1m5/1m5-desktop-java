package io.onemfive.desktop.views.personal.identities.list;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;
import ra.common.network.ControlCommand;
import ra.did.DIDService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;
import static io.onemfive.desktop.DesktopClient.VIEW_OP;
import static io.onemfive.desktop.util.FormBuilder.addPrimaryActionButton;
import static io.onemfive.desktop.util.FormBuilder.addTopLabelListView;

public class ListIdentityView extends ActivatableView implements TopicListener {

    public static final String IDENTITIES_LIST = "IDENTITIES_LIST";
    public static final String IDENTITY_DELETED = "IDENTITY_DELETED";

    private GridPane pane;
    private int gridRow = 0;

    protected static ObservableList<Object> usernames = FXCollections.observableArrayList();
    protected static ListView<Object> usernameListView;

    private Button deleteButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        // List Identities
        usernameListView = addTopLabelListView(pane, gridRow, Resources.get("personalIdentitiesView.identities"), Layout.FIRST_ROW_DISTANCE).second;
        usernameListView.setItems(usernames);
        usernameListView.setEditable(false);
        usernameListView.getStyleClass().add("listView");
        deleteButton = addPrimaryActionButton(pane, ++gridRow, Resources.get("shared.create"), Layout.TWICE_FIRST_ROW_DISTANCE);
        deleteButton.getStyleClass().add("action-button");
        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Object username = usernameListView.getSelectionModel().getSelectedItem();
                if(username!=null)
                    deleteIdentity((String)username);
            }
        });
        if(DesktopClient.getCache().getLocalIdentities()==null
                || DesktopClient.getCache().getLocalIdentities().isEmpty()) {
            updateIdentitiesList();
        }
    }

    @Override
    protected void deactivate() {
        deleteButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case IDENTITIES_LIST: {
                List<DID> dids = (List<DID>)e.getValue("identities");
                Collections.sort(dids, new Comparator<DID>() {
                    @Override
                    public int compare(DID o1, DID o2) {
                        if(o1==null || o2==null)
                            return 0;
                        return o1.getUsername().compareTo(o2.getUsername());
                    }
                });
                usernames.clear();
                for(DID did : dids) {
                    usernames.add(did.getUsername());
                }
                break;
            }
            case IDENTITY_DELETED: {

                break;
            }
        }
        LOG.info("Model updated.");
    }

    private void deleteIdentity(String username) {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(VIEW_NAME, IdentitiesView.class.getName());
        e.addNVP(VIEW_OP, IDENTITY_DELETED);
        e.addRoute(DIDService.class, DIDService.OPERATION_DELETE_IDENTITY);
        DesktopClient.deliver(e);
    }

    private void updateIdentitiesList() {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addNVP(VIEW_NAME, IdentitiesView.class.getName());
        e.addNVP(VIEW_OP, IDENTITIES_LIST);
        e.addRoute(DIDService.class, DIDService.OPERATION_GET_IDENTITIES);
        DesktopClient.deliver(e);
    }
}
