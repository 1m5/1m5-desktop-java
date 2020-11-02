package io.onemfive.desktop.views.community.social;

import io.onemfive.data.DID;
import io.onemfive.desktop.views.InitializableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;

import java.util.List;

public class SocialView extends InitializableView {

    private ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    public void updateContacts(List<DID> contacts) {
        contactAddresses.clear();
        for(DID c : contacts) {
            contactAddresses.add(c.getUsername() + ": "+c.getPublicKey().getAddress());
        }
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        StackPane pane = (StackPane)root;


        LOG.info("Initialized.");
    }

}
