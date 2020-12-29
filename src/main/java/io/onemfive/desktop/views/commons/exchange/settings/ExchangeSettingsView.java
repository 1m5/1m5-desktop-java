package io.onemfive.desktop.views.commons.exchange.settings;

import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.addSlideToggleButton;

public class ExchangeSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private String title = Resources.get("commonsView.exchange.settings.title");
    private ToggleButton ldnButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane) root;

        ldnButton = addSlideToggleButton(pane, ++gridRow, Resources.get("commonsView.exchange.settings.1dn.title"), Layout.FIRST_ROW_DISTANCE);
        ldnButton.disableProperty().setValue(true);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        ldnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }
        });
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        ldnButton.setOnAction(null);
        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");

        LOG.info("Model updated.");
    }

}
