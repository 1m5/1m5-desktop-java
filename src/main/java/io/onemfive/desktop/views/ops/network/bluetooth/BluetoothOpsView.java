package io.onemfive.desktop.views.ops.network.bluetooth;

import io.onemfive.desktop.BusClient;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.util.Resources;
import ra.util.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.*;

public class BluetoothOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus networkStatus = NetworkStatus.NOT_INSTALLED;
    private String sensorStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField sensorStatusTextField;

    private ToggleButton discoverButton;

    private String friendlyName = Resources.get("ops.network.notKnownYet");
    private String address = Resources.get("ops.network.notKnownYet");
    private TextField friendlynameTextField;
    private TextField addressTextField;

    public BluetoothOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        sensorStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.sensor"), sensorStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg sensorPower = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.sensorControls"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(sensorPower, 1);
        discoverButton = addSlideToggleButton(pane, ++gridRow, Resources.get("ops.network.bluetooth.discoverButton"), Layout.TWICE_FIRST_ROW_DISTANCE);

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        friendlynameTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.bluetooth.friendlyNameLabel"), friendlyName, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        addressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.bluetooth.addressLabel"), address).second;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
        discoverButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("powerButton=" + discoverButton.isSelected());
                if (discoverButton.isSelected()) {
                    MVC.execute(new Runnable() {
                        @Override
                        public void run() {
//                            BusClient.startService(BluetoothService.class);
                        }
                    });
                } else {
                    MVC.execute(new Runnable() {
                        @Override
                        public void run() {
//                            BusClient.shutdownService(BluetoothService.class, true);
                        }
                    });
                }
            }
        });
        discoverButton.disableProperty().setValue(true);
    }

    @Override
    protected void deactivate() {
        discoverButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(object instanceof NetworkState) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
            if(this.networkStatus != networkState.networkStatus) {
                this.networkStatus = networkState.networkStatus;
                if(sensorStatusField != null) {
                    sensorStatusTextField.setText(StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(networkStatus == NetworkStatus.CONNECTING
                    || networkStatus == NetworkStatus.CONNECTED) {
                if (networkState.localPeer != null) {
                    friendlyName = networkState.localPeer.getDid().getUsername();
                    if (friendlynameTextField != null) {
                        friendlynameTextField.setText(friendlyName);
                    }
                    address = networkState.localPeer.getDid().getPublicKey().getAddress();
                    if (addressTextField != null) {
                        addressTextField.setText(address);
                    }
                }
                discoverButton.disableProperty().setValue(false);
            } else if(networkStatus == NetworkStatus.WARMUP
                    || networkStatus == NetworkStatus.WAITING) {
                discoverButton.disableProperty().setValue(false);
            } else {
                discoverButton.disableProperty().setValue(true);
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
