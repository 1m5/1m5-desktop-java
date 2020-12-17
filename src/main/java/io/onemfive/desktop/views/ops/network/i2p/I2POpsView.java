package io.onemfive.desktop.views.ops.network.i2p;

import io.onemfive.desktop.DesktopBusClient;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.service.ServiceReport;
import ra.common.service.ServiceStatus;
import ra.i2p.I2PService;
import ra.util.Resources;
import ra.util.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.*;

public class I2POpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;

    private String serviceStatusField = StringUtil.capitalize(serviceStatus.name().toLowerCase().replace('_', ' '));
    private TextField serviceStatusTextField;

    private String networkStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField networkStatusTextField;

    private ToggleButton powerButton;
    private CheckBox hardStop;

    private String i2PFingerprint = Resources.get("ops.network.notKnownYet");
    private TextField i2PFingerprintTextField;

    private String i2PAddress = Resources.get("ops.network.notKnownYet");
    private TextArea i2PAddressTextArea;

    private String i2PIPv6Address = Resources.get("ops.network.notKnownYet");
    private TextField i2PIPv6AddressTextField;

    private String port = Resources.get("ops.network.notKnownYet");
    private TextField portTextField;

    public I2POpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 3, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        serviceStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.service"), serviceStatusField, Layout.FIRST_ROW_DISTANCE).second;
        networkStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.network"), networkStatusField).second;

        TitledGroupBg sensorPower = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.networkControls"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(sensorPower, 1);
        powerButton = addSlideToggleButton(pane, ++gridRow, Resources.get("ops.network.networkPowerButton"), Layout.TWICE_FIRST_ROW_DISTANCE);
        hardStop = addCheckBox(pane, ++gridRow, Resources.get("ops.network.hardStop"));

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 6, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        i2PFingerprintTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.i2p.fingerprintLabel"), i2PFingerprint, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        i2PAddressTextArea = addCompactTopLabelTextAreaWithText(pane, i2PAddress, ++gridRow, Resources.get("ops.network.i2p.addressLabel"), true).second;
        i2PAddressTextArea.setMaxHeight(80d);
        i2PIPv6AddressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.i2p.ipv6Label"), i2PIPv6Address).second;
        portTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.i2p.portLabel"), port).second;

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
                    DesktopBusClient.startService(I2PService.class);
                } else {
                    reset();
                    DesktopBusClient.shutdownService(I2PService.class, hardStop.isSelected());
                }
                powerButton.disableProperty().setValue(true);
                hardStop.disableProperty().setValue(true);
            };
        });
    }

    @Override
    protected void deactivate() {
        powerButton.setOnAction(null);
    }


    @Override
    public void modelUpdated(String name, Object object) {
        if(ServiceReport.class.getSimpleName().equals(name)) {
            ServiceReport report = (ServiceReport) object;
            LOG.info("ServiceReport received to update model: status="+report.serviceStatus.name());
            serviceStatus = report.serviceStatus;
        }
        else if(NetworkState.class.getSimpleName().equals(name)) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
            if(this.networkStatus != networkState.networkStatus) {
                this.networkStatus = networkState.networkStatus;
                if(networkStatusField != null) {
                    networkStatusTextField.setText(StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(networkStatus == NetworkStatus.CONNECTED) {
                if (networkState.localPeer != null) {
                    i2PAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                    i2PFingerprint = networkState.localPeer.getDid().getPublicKey().getFingerprint();
                    if (i2PAddressTextArea != null)
                        i2PAddressTextArea.setText(i2PAddress);
                    if (i2PFingerprintTextField != null)
                        i2PFingerprintTextField.setText(i2PFingerprint);
                }
                if (networkState.virtualPort != null) {
                    port = String.valueOf(networkState.virtualPort);
                    if (portTextField != null) {
                        portTextField.setText(port);
                    }
                }
                if (networkState.params.get("i2np.lastIPv6") != null) {
                    i2PIPv6Address = (String) networkState.params.get("i2np.lastIPv6");
                    if (i2PIPv6AddressTextField != null)
                        i2PIPv6AddressTextField.setText(i2PIPv6Address);
                }
            }
            updateComponents();
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

    private void reset() {
        i2PAddress = Resources.get("ops.network.notKnownYet");
        i2PFingerprint = Resources.get("ops.network.notKnownYet");
        if(i2PAddressTextArea!=null)
            i2PAddressTextArea.setText(i2PAddress);
        if(i2PFingerprintTextField!=null)
            i2PFingerprintTextField.setText(i2PFingerprint);
        port = Resources.get("ops.network.notKnownYet");
        if(portTextField!=null) {
            portTextField.setText(port);
        }
        i2PIPv6Address = Resources.get("ops.network.notKnownYet");
        if(i2PIPv6AddressTextField!=null)
            i2PIPv6AddressTextField.setText(i2PIPv6Address);
    }

    private void updateComponents() {
        if(networkStatus ==NetworkStatus.CLOSED
                || networkStatus ==NetworkStatus.PORT_CONFLICT
                || serviceStatus==ServiceStatus.SHUTDOWN
                || serviceStatus==ServiceStatus.GRACEFULLY_SHUTDOWN) {
            // Power is off and able to turn it on
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(false);
            hardStop.setVisible(false);
        } else if(networkStatus ==NetworkStatus.WARMUP
                || networkStatus ==NetworkStatus.WAITING) {
            // Power is on, but not yet able to turn it off - starting up
            powerButton.setSelected(true);
            powerButton.disableProperty().setValue(true);
            hardStop.setVisible(false);
        } else if(serviceStatus==ServiceStatus.SHUTTING_DOWN
                || serviceStatus==ServiceStatus.GRACEFULLY_SHUTTING_DOWN
                || networkStatus ==NetworkStatus.ERROR) {
            // Power is off and unable to turn it on as it is shutting down
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(true);
            hardStop.setVisible(true);
            hardStop.disableProperty().setValue(true);
        } else if(networkStatus ==NetworkStatus.CONNECTING
                || networkStatus ==NetworkStatus.CONNECTED
                || networkStatus ==NetworkStatus.VERIFIED
                || networkStatus ==NetworkStatus.HANGING
                || networkStatus ==NetworkStatus.DISCONNECTED) {
            // Power is on and shutting it down is available
            powerButton.setSelected(true);
            powerButton.disableProperty().setValue(false);
            hardStop.setVisible(true);
            hardStop.disableProperty().setValue(false);
        }
        serviceStatusTextField.setText(serviceStatus.name());
        networkStatusTextField.setText(networkStatus.name());
    }

}
