package io.onemfive.desktop.views.settings.network.lifi;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.Resources;

import static io.onemfive.desktop.util.FormBuilder.addMultilineLabel;
import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class LiFiNetworkSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private Label notes;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 1, Resources.get("shared.notes"));
        GridPane.setColumnSpan(statusGroup, 1);

        String note = "LiFi Network Service Settings is on the roadmap. Settings will enable changes for configuring integration with LiFi.";
        notes = addMultilineLabel(pane, ++gridRow, note, Layout.FIRST_ROW_DISTANCE);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {

    }

    @Override
    protected void deactivate() {

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
