package io.onemfive.desktop.views.personal.identities;

import io.onemfive.desktop.views.ActivatableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class BaseIdentityView extends ActivatableView {

    protected static ObservableList<Object> identityDIDs = FXCollections.observableArrayList();
    protected static ObservableList<String> identityAddresses = FXCollections.observableArrayList();
    protected static ListView<Object> identitiesList;



}
