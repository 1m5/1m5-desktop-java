package io.onemfive.desktop.views.ops.network.bluetooth;

import onemfive.Cmd;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.network.NetworkState;
import io.onemfive.network.sensors.SensorStatus;
import io.onemfive.util.Res;
import io.onemfive.util.StringUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

import static io.onemfive.desktop.util.FormBuilder.*;

public class BluetoothSensorOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private SensorStatus sensorStatus = SensorStatus.NOT_INITIALIZED;
    private String sensorStatusField = StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' '));
    private TextField sensorStatusTextField;

    private ToggleButton discoverButton;

    private String friendlyName = Res.get("ops.network.notKnownYet");
    private String address = Res.get("ops.network.notKnownYet");
    private TextField friendlynameTextField;
    private TextField addressTextField;

    public BluetoothSensorOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Res.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        sensorStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Res.get("ops.network.status.sensor"), sensorStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg sensorPower = addTitledGroupBg(pane, ++gridRow, 3, Res.get("ops.network.sensorControls"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(sensorPower, 1);
        discoverButton = addSlideToggleButton(pane, ++gridRow, Res.get("ops.network.bluetooth.discoverButton"), Layout.TWICE_FIRST_ROW_DISTANCE);

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Res.get("ops.network.localNode"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        friendlynameTextField = addCompactTopLabelTextField(pane, ++gridRow, Res.get("ops.network.bluetooth.friendlyNameLabel"), friendlyName, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        addressTextField = addCompactTopLabelTextField(pane, ++gridRow, Res.get("ops.network.bluetooth.addressLabel"), address).second;

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
                            Cmd.startBluetoothDiscovery();
                        }
                    });
                } else {
                    MVC.execute(new Runnable() {
                        @Override
                        public void run() {
                            Cmd.stopBluetoothDiscovery();
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
            if(this.sensorStatus != networkState.sensorStatus) {
                this.sensorStatus = networkState.sensorStatus;
                if(sensorStatusField != null) {
                    sensorStatusTextField.setText(StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(sensorStatus==SensorStatus.NETWORK_CONNECTING
                    || sensorStatus==SensorStatus.NETWORK_CONNECTED) {
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
            } else if(sensorStatus==SensorStatus.NETWORK_WARMUP
                    || sensorStatus==SensorStatus.STARTING) {
                discoverButton.disableProperty().setValue(false);
            } else {
                discoverButton.disableProperty().setValue(true);
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
