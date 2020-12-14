package io.onemfive.desktop.views.settings.network.tor;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkPeer;
import ra.common.network.NetworkState;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.addSlideToggleButton;
import static io.onemfive.desktop.util.FormBuilder.addTopLabelListView;

public class TORNetworkSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private ToggleButton routerEmbedded;

    private ObservableList<String> seeds = FXCollections.observableArrayList();
    private ListView seedsListView;

    public TORNetworkSettingsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        routerEmbedded = addSlideToggleButton(pane, gridRow, Resources.get("settings.network.tor.routerEmbedded"));
        seedsListView = addTopLabelListView(pane, ++gridRow, Resources.get("settings.network.tor.seedsLabel")).second;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
        routerEmbedded.setSelected(false);
        routerEmbedded.setOnAction(e -> {
            LOG.info("routerEmbedded="+routerEmbedded.isSelected());
        });
        routerEmbedded.disableProperty().setValue(true);

        seedsListView.setItems(seeds);
    }

    @Override
    protected void deactivate() {
        routerEmbedded.setOnAction(null);
    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(object instanceof NetworkState) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;

        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }
}
