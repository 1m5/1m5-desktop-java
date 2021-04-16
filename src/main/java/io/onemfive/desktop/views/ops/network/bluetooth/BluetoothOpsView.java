package io.onemfive.desktop.views.ops.network.bluetooth;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import ra.bluetooth.BluetoothService;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.service.ServiceStatus;
import ra.util.Resources;
import ra.util.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.*;

public class BluetoothOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;
    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private String networkStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField networkStatusTextField;

    private ToggleButton powerButton;
//    private ToggleButton discoverButton;

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
        networkStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.network"), networkStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg networkControls = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.networkControls"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(networkControls, 1);
        powerButton = addSlideToggleButton(pane, ++gridRow, Resources.get("ops.network.networkPowerButton"), Layout.TWICE_FIRST_ROW_DISTANCE);
//        discoverButton = addSlideToggleButton(pane, ++gridRow, Resources.get("ops.network.networkDiscoverButton"));

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        friendlynameTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.bluetooth.friendlyNameLabel"), friendlyName, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        addressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.bluetooth.addressLabel"), address).second;

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
        updateComponents();
        powerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("powerButton=" + powerButton.isSelected());
                if (powerButton.isSelected()) {
                    DesktopClient.startService(BluetoothService.class);
                } else {
                    DesktopClient.shutdownService(BluetoothService.class, true);
                }
                powerButton.disableProperty().setValue(true);
            }
        });
    }

    @Override
    protected void deactivate() {
        powerButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(object instanceof NetworkState) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
            if(this.networkStatus != networkState.networkStatus) {
                this.networkStatus = networkState.networkStatus;
                if(networkStatusField != null) {
                    networkStatusTextField.setText(StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' ')));
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
                powerButton.disableProperty().setValue(false);
            } else if(networkStatus == NetworkStatus.WARMUP
                    || networkStatus == NetworkStatus.WAITING) {
                powerButton.disableProperty().setValue(false);
            } else {
                powerButton.disableProperty().setValue(true);
            }
            updateComponents();
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

    private void updateComponents() {
        if(networkStatus ==NetworkStatus.CLOSED
                || serviceStatus== ServiceStatus.SHUTDOWN
                || serviceStatus==ServiceStatus.GRACEFULLY_SHUTDOWN) {
            // Power is off and able to turn it on
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(false);
        } else if(networkStatus ==NetworkStatus.WARMUP
                || networkStatus ==NetworkStatus.WAITING) {
            // Power is on, but not yet able to turn it off - starting up
            powerButton.setSelected(true);
            powerButton.disableProperty().setValue(true);
        } else if(serviceStatus==ServiceStatus.SHUTTING_DOWN
                || serviceStatus==ServiceStatus.GRACEFULLY_SHUTTING_DOWN
                || networkStatus ==NetworkStatus.ERROR) {
            // Power is off and unable to turn it on as it is shutting down
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(true);
        } else if(networkStatus ==NetworkStatus.CONNECTING
                || networkStatus ==NetworkStatus.CONNECTED
                || networkStatus ==NetworkStatus.VERIFIED
                || networkStatus ==NetworkStatus.HANGING
                || networkStatus ==NetworkStatus.DISCONNECTED) {
            // Power is on and shutting it down is available
            powerButton.setSelected(true);
            powerButton.disableProperty().setValue(false);
        }
    }

}
