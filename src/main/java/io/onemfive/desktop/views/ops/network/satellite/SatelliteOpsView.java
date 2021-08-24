package io.onemfive.desktop.views.ops.network.satellite;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.service.ServiceStatus;
import ra.common.Resources;
import ra.common.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SatelliteOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus sensorStatus = NetworkStatus.CLOSED;
    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;

    private String sensorStatusField = StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' '));
    private TextField sensorStatusTextField;

    private String satelliteFingerprint = Resources.get("ops.network.notKnownYet");
    private TextField satelliteFingerprintTextField;

    private String satelliteAddress = Resources.get("ops.network.notKnownYet");
    private TextArea satelliteAddressTextArea;

    public SatelliteOpsView() {
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
        satelliteFingerprintTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.satellite.fingerprintLabel"), satelliteFingerprint, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        satelliteAddressTextArea = addCompactTopLabelTextAreaWithText(pane, satelliteAddress, ++gridRow, Resources.get("ops.network.satellite.addressLabel"), true).second;

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
                satelliteAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                if(satelliteAddressTextArea!=null)
                    satelliteAddressTextArea.setText(satelliteAddress);
                satelliteFingerprint = networkState.localPeer.getDid().getPublicKey().getFingerprint();
                if(satelliteFingerprintTextField!=null)
                    satelliteFingerprintTextField.setText(satelliteFingerprint);
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
