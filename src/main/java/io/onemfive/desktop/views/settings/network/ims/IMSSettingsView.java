package io.onemfive.desktop.views.settings.network.ims;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;

public class IMSSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    public IMSSettingsView() {
        super();
    }

    public void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        LOG.info("Initialized");
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

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

