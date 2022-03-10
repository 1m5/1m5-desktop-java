package io.onemfive.desktop.views.personal.contacts.list;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.contacts.BaseContactView;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.did.DIDService;

import static io.onemfive.desktop.DesktopClient.VIEW_NAME;

public class ListContactView extends BaseContactView implements TopicListener {

    public static final String CONTACTS_LIST = "CONTACTS_LIST";

    private GridPane pane;
    private int gridRow = 0;

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {

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
