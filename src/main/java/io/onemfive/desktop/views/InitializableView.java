package io.onemfive.desktop.views;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class InitializableView extends BaseView implements Initializable {

    public InitializableView() {}

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        prepareInitialize();
        initialize();
    }

    protected void prepareInitialize() {
    }

    protected void initialize() {
    }
}
