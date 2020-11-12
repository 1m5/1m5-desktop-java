package io.onemfive.desktop.views.settings.network.i2p;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import net.i2p.router.Router;
import ra.common.network.NetworkPeer;
import ra.common.network.NetworkState;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class I2PSensorSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    public I2PSensorSettingsView() {
        super();
    }

    private ToggleButton hiddenMode;
    private ToggleButton routerEmbedded;
    private TextField sharePercentage;

    private String maxConnections = Resources.get("ops.network.notKnownYet");
    private TextField maxConnectionsTextField;

    private ObservableList<String> seeds = FXCollections.observableArrayList();
    private ListView seedsListView;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg configGroup = addTitledGroupBg(pane, gridRow, 5, Resources.get("settings.network.config"));
        GridPane.setColumnSpan(configGroup, 1);
        routerEmbedded = addSlideToggleButton(pane, ++gridRow, Resources.get("settings.network.i2p.routerEmbedded"), Layout.FIRST_ROW_DISTANCE);
        hiddenMode = addSlideToggleButton(pane, ++gridRow, Resources.get("settings.network.i2p.hiddenMode"));
        sharePercentage = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("settings.network.i2p.sharePercentage"), String.valueOf(Router.DEFAULT_SHARE_PERCENTAGE)).second;
        maxConnectionsTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("settings.network.i2p.maxConnectionsLabel"), maxConnections).second;
        seedsListView = addTopLabelListView(pane, ++gridRow, Resources.get("settings.network.i2p.seedsLabel")).second;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
        hiddenMode.setSelected(false);
        hiddenMode.setOnAction(e -> {
            LOG.info("hiddenMode="+hiddenMode.isSelected());
//            NetworkState networkState = new NetworkState();
//            networkState.network = Network.I2P;
//            networkState.params.put(Router.PROP_HIDDEN, String.valueOf(hiddenMode.isSelected()));
//            hiddenMode.disableProperty().setValue(true);
//            MVC.updateNetwork(networkState);
        });
        hiddenMode.disableProperty().setValue(true);

        routerEmbedded.setSelected(true);
        routerEmbedded.setOnAction(e -> {
            LOG.info("routerEmbedded="+routerEmbedded.isSelected());
        });
        routerEmbedded.disableProperty().setValue(true);

        seedsListView.setItems(seeds);
    }

    @Override
    protected void deactivate() {
        hiddenMode.setOnAction(null);
        routerEmbedded.setOnAction(null);
    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(object instanceof NetworkState) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
            if(hiddenMode!=null) {
                hiddenMode.setSelected("true".equals(networkState.params.get(Router.PROP_HIDDEN)));
            }
            // TODO: Support local router
            if(routerEmbedded!=null)
                routerEmbedded.setSelected(true);
            if(sharePercentage!=null)
                sharePercentage.setText((String)networkState.params.get(Router.PROP_BANDWIDTH_SHARE_PERCENTAGE));
            if(networkState.params.get("i2np.udp.maxConnections")!=null) {
                maxConnections = (String)networkState.params.get("i2np.udp.maxConnections");
                if(maxConnectionsTextField!=null)
                    maxConnectionsTextField.setText(maxConnections);
            }
//            if(networkState.seeds.size() > 0) {
//                seeds.clear();
//                for(NetworkPeer seed : networkState.seeds) {
//                    seeds.add(seed.getDid().getPublicKey().getFingerprint());
//                }
//            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
