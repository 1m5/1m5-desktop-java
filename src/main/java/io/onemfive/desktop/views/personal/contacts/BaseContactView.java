package io.onemfive.desktop.views.personal.contacts;

import io.onemfive.desktop.views.ActivatableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class BaseContactView extends ActivatableView {

    private static ObservableList<Object> contactDIDs = FXCollections.observableArrayList();
    private static ObservableList<String> contactAddresses = FXCollections.observableArrayList();

    private static ListView<Object> contactsList;

    protected GridPane pane;
    protected int gridRow = 0;


}
