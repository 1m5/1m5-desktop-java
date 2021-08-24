package io.onemfive.desktop.views.ops.network.wifidirect;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.service.ServiceStatus;
import ra.common.Resources;
import ra.common.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class WifiDirectOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus sensorStatus = NetworkStatus.CLOSED;
    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;

    private String sensorStatusField = StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' '));
    private TextField sensorStatusTextField;

    private String wifidirectFriendlyName = Resources.get("ops.network.notKnownYet");
    private TextField wifidirectFriendlynameTextField;

    private String wifidirectAddress = Resources.get("ops.network.notKnownYet");
    private TextField wifidirectAddressTextField;

    public WifiDirectOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        sensorStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.network"), sensorStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        wifidirectFriendlynameTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.wifidirect.friendlyNameLabel"), wifidirectFriendlyName, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        wifidirectAddressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.wifidirect.addressLabel"), wifidirectAddress).second;

        LOG.info("Initialized");
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
            if(this.sensorStatus != networkState.networkStatus) {
                this.sensorStatus = networkState.networkStatus;
                if(sensorStatusField != null) {
                    sensorStatusTextField.setText(StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(networkState.localPeer!=null) {
                wifidirectAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                if(wifidirectAddressTextField!=null)
                    wifidirectAddressTextField.setText(wifidirectAddress);
                wifidirectFriendlyName = networkState.localPeer.getDid().getUsername();
                if(wifidirectFriendlynameTextField!=null)
                    wifidirectFriendlynameTextField.setText(wifidirectFriendlyName);
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
