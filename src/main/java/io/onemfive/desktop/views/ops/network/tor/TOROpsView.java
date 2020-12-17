package io.onemfive.desktop.views.ops.network.tor;

import io.onemfive.desktop.DesktopBusClient;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.components.HyperlinkWithIcon;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.ViewPath;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.commons.browser.BrowserView;
import io.onemfive.desktop.views.home.HomeView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.service.ServiceReport;
import ra.common.service.ServiceStatus;
import ra.tor.TORClientService;
import ra.util.Resources;
import ra.util.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.*;

public class TOROpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    // Considering TOR Ops is watching both a client and server service,
    // both client and hidden service status' are taken into account
    // to determine overall status
    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;

    private String serviceStatusField = StringUtil.capitalize(serviceStatus.name().toLowerCase().replace('_', ' '));
    private TextField serviceStatusTextField;

    private String networkStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField networkStatusTextField;

    private ToggleButton powerButton;
    private CheckBox hardStop;

    private String address = Resources.get("ops.network.notKnownYet");
    private TextField addressTextField;

    private String virtualPort = Resources.get("ops.network.notKnownYet");
    private TextField virtualPortTextField;

    private String targetPort = Resources.get("ops.network.notKnownYet");
    private TextField targetPortTextField;

    private String hiddenServiceURL = "http://127.0.0.1";
    private HyperlinkWithIcon hiddenServiceHyperLink;

    public TOROpsView() {
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

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 5, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        addressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.tor.addressLabel"), address, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        virtualPortTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.tor.vPortLabel"), virtualPort).second;
        targetPortTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.tor.tPortLabel"), targetPort).second;
        hiddenServiceHyperLink = addHyperlinkWithIcon(pane, ++gridRow, Resources.get("ops.network.tor.hiddenServiceTestLabel"), hiddenServiceURL);
        GridPane.setColumnSpan(hiddenServiceHyperLink, 2);
        hiddenServiceHyperLink.disableProperty().setValue(true);

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
                    DesktopBusClient.startService(TORClientService.class);
                } else {
                    reset();
                    DesktopBusClient.shutdownService(TORClientService.class, hardStop.isSelected());
                }
                powerButton.disableProperty().setValue(true);
                hardStop.disableProperty().setValue(true);
            }
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
        } else if(NetworkState.class.getSimpleName().equals(name)) {
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
                    address = networkState.localPeer.getDid().getPublicKey().getAddress();
                    if (addressTextField != null)
                        addressTextField.setText(address);
                }
                if (networkState.virtualPort != null) {
                    virtualPort = String.valueOf(networkState.virtualPort);
                    if (virtualPortTextField != null) {
                        virtualPortTextField.setText(virtualPort);
                    }
                }
                if (networkState.targetPort != null) {
                    targetPort = String.valueOf(networkState.targetPort);
                    if (targetPortTextField != null) {
                        targetPortTextField.setText(targetPort);
                    }
                    hiddenServiceURL = "http://127.0.0.1:" + targetPort + "/test";
                    if (hiddenServiceHyperLink != null) {
                        hiddenServiceHyperLink.setOnAction(e -> MVC.navigation.navigateTo(ViewPath.to(HomeView.class, CommonsView.class, BrowserView.class), hiddenServiceURL));
                        hiddenServiceHyperLink.disableProperty().set(false);
                    }
                }
            }
            updateComponents();
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

    private void reset() {
        address = Resources.get("ops.network.notKnownYet");
        if(addressTextField!=null)
            addressTextField.setText(address);
        virtualPort = Resources.get("ops.network.notKnownYet");
        if(virtualPortTextField !=null) {
            virtualPortTextField.setText(virtualPort);
        }
        targetPort = Resources.get("ops.network.notKnownYet");
        if(targetPortTextField !=null) {
            targetPortTextField.setText(targetPort);
        }
        hiddenServiceURL = Resources.get("ops.network.notKnownYet");
        if(hiddenServiceHyperLink!=null) {
            hiddenServiceHyperLink.setOnAction(null);
            hiddenServiceHyperLink.disableProperty().set(true);
        }
    }

    private void updateComponents() {
        if(networkStatus == NetworkStatus.CLOSED
                || networkStatus == NetworkStatus.PORT_CONFLICT
                || serviceStatus == ServiceStatus.SHUTDOWN
                || serviceStatus == ServiceStatus.GRACEFULLY_SHUTDOWN) {
            // Power is off and able to turn it on
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(false);
            hardStop.setVisible(false);
        } else if(serviceStatus == ServiceStatus.INITIALIZING
                || networkStatus == NetworkStatus.WARMUP
                || networkStatus == NetworkStatus.WAITING) {
            // Power is on, but not yet able to turn it off - starting up
            powerButton.setSelected(true);
            powerButton.disableProperty().setValue(true);
            hardStop.setVisible(false);
        } else if(serviceStatus == ServiceStatus.SHUTTING_DOWN
                || serviceStatus == ServiceStatus.GRACEFULLY_SHUTTING_DOWN
                || networkStatus == NetworkStatus.ERROR) {
            // Power is off and unable to turn it on as it is shutting down
            powerButton.setSelected(false);
            powerButton.disableProperty().setValue(true);
            hardStop.setVisible(true);
            hardStop.disableProperty().setValue(true);
        } else if(serviceStatus == ServiceStatus.RUNNING
                || networkStatus == NetworkStatus.CONNECTING
                || networkStatus == NetworkStatus.CONNECTED
                || networkStatus == NetworkStatus.VERIFIED
                || networkStatus == NetworkStatus.HANGING
                || networkStatus == NetworkStatus.DISCONNECTED) {
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
