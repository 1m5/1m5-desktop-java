package io.onemfive.desktop.views.personal.identities.list;

import io.onemfive.desktop.DesktopClient;
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
import ra.common.network.ControlCommand;
import ra.did.DIDService;

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;
import static io.onemfive.desktop.DesktopClient.VIEW_OP;

public class ListIdentityView extends ActivatableView implements TopicListener {

    public static final String IDENTITIES_LIST = "IDENTITIES_LIST";

    private GridPane pane;
    private int gridRow = 0;

    protected static ObservableList<Object> identityDIDs = FXCollections.observableArrayList();
    protected static ListView<Object> identitiesList;

    private Button deleteIdentity;

    @Override
    protected void initialize() {
        super.initialize();
        // List Identities
        identitiesList = new ListView<>();
        identitiesList.setPrefSize(800, 250);
        identitiesList.setItems(identityDIDs);
        identitiesList.setEditable(false);
        identitiesList.getStyleClass().add("listView");
        VBox mainBox = new VBox(Layout.GRID_GAP, identitiesList);
        pane.add(mainBox, 0, gridRow);

        LOG.info("Initialized.");
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

        }
        LOG.info("Model updated.");
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
