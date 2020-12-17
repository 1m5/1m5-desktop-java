package io.onemfive.desktop.views.settings.network.i2p;

import com.jfoenix.controls.JFXButton;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import net.i2p.router.Router;
import ra.common.network.NetworkPeer;
import ra.common.network.NetworkState;
import ra.common.service.ServiceReport;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.*;

public class I2PNetworkSettingsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    public I2PNetworkSettingsView() {
        super();
    }

    private ToggleButton hiddenMode;
    private ToggleButton routerEmbedded;
    private TextField sharePercentage;

    private String maxConnections = Resources.get("ops.network.notKnownYet");
    private TextField maxConnectionsTextField;

    private ObservableList<String> seeds = FXCollections.observableArrayList();
    private ListView seedsListView;
    private TextField fingerprintTextField;
    private TextField addressTextField;
    private Button addButton;
    private Button removeButton;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

//        TitledGroupBg seedsGroup = addTitledGroupBg(pane, gridRow, 3, Resources.get("settings.network.seedsManagement"));
//        GridPane.setColumnSpan(seedsGroup, 2);
        TitledGroupBg seedsGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("settings.network.seedsManagement"));
        GridPane.setColumnSpan(seedsGroup, 1);

        GridPane.setHalignment(seedsGroup, HPos.LEFT);
//        fingerprintTextField = addInputTextField(pane, ++gridRow, Resources.get("settings.network.i2p.fingerprint"), Layout.FIRST_ROW_DISTANCE);
//        addressTextField = addInputTextField(pane, ++gridRow, Resources.get("settings.network.i2p.address"));
//        addButton = addButton(pane, gridRow, 1, Resources.get("shared.add"));
        seedsListView = addTopLabelListView(pane, ++gridRow, Resources.get("settings.network.seeds")).second;
//        removeButton = addButton(pane, gridRow, 1, Resources.get("shared.remove"));

//        TitledGroupBg configGroup = addTitledGroupBg(pane, gridRow, 3, Resources.get("settings.network.config"), Layout.FIRST_ROW_AND_GROUP_DISTANCE);
//        GridPane.setColumnSpan(configGroup, 2);
//        GridPane.setHalignment(configGroup, HPos.LEFT);
//        routerEmbedded = addSlideToggleButton(pane, ++gridRow, Resources.get("settings.network.i2p.routerEmbedded"), Layout.FIRST_ROW_DISTANCE);
//        hiddenMode = addSlideToggleButton(pane, gridRow, 1, Resources.get("settings.network.i2p.hiddenMode"), Layout.FIRST_ROW_DISTANCE);
//        sharePercentage = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("settings.network.i2p.sharePercentage"), String.valueOf(Router.DEFAULT_SHARE_PERCENTAGE)).second;
//        maxConnectionsTextField = addCompactTopLabelTextField(pane, gridRow, 1, Resources.get("settings.network.i2p.maxConnectionsLabel"), maxConnections).second;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
//        addButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                if(fingerprintTextField.getText()==null
//                        || fingerprintTextField.getText().isEmpty()
//                        || addressTextField.getText()==null
//                        || addressTextField.getText().isEmpty())
//                    return;
//                seeds.add(fingerprintTextField.getText()+" : "+addressTextField.getText());
//                fingerprintTextField.setText(null);
//                addressTextField.setText(null);
//            }
//        });
//        removeButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                if(seedsListView.getSelectionModel().getSelectedIndex()>=0) {
//                    seeds.remove(seedsListView.getSelectionModel().getSelectedIndex());
//                }
//            }
//        });

//        hiddenMode.setSelected(false);
//        hiddenMode.setOnAction(e -> {
//            LOG.info("hiddenMode="+hiddenMode.isSelected());
//            NetworkState networkState = new NetworkState();
//            networkState.network = Network.I2P;
//            networkState.params.put(Router.PROP_HIDDEN, String.valueOf(hiddenMode.isSelected()));
//            hiddenMode.disableProperty().setValue(true);
//            MVC.updateNetwork(networkState);
//        });
//        hiddenMode.disableProperty().setValue(true);
//
//        routerEmbedded.setSelected(true);
//        routerEmbedded.setOnAction(e -> {
//            LOG.info("routerEmbedded="+routerEmbedded.isSelected());
//        });
//        routerEmbedded.disableProperty().setValue(true);

        seedsListView.setItems(seeds);
        seedsListView.setMaxHeight(320d);
    }

    @Override
    protected void deactivate() {
//        hiddenMode.setOnAction(null);
//        routerEmbedded.setOnAction(null);
    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(NetworkState.class.getSimpleName().equals(name)) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
//            if(hiddenMode!=null) {
//                hiddenMode.setSelected("true".equals(networkState.params.get(Router.PROP_HIDDEN)));
//            }
            // TODO: Support local router
//            if(routerEmbedded!=null)
//                routerEmbedded.setSelected(true);
//            if(sharePercentage!=null)
//                sharePercentage.setText((String)networkState.params.get(Router.PROP_BANDWIDTH_SHARE_PERCENTAGE));
//            if(networkState.params.get("i2np.udp.maxConnections")!=null) {
//                maxConnections = (String)networkState.params.get("i2np.udp.maxConnections");
//                if(maxConnectionsTextField!=null)
//                    maxConnectionsTextField.setText(maxConnections);
//            }
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
