package io.onemfive.desktop.views.ops.network.satellite;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.network.NetworkState;
import io.onemfive.network.sensors.SensorStatus;
import io.onemfive.util.Res;
import io.onemfive.util.StringUtil;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static io.onemfive.desktop.util.FormBuilder.*;

public class SatelliteSensorOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private SensorStatus sensorStatus = SensorStatus.NOT_INITIALIZED;
    private String sensorStatusField = StringUtil.capitalize(sensorStatus.name().toLowerCase().replace('_', ' '));
    private TextField sensorStatusTextField;

    private String satelliteFingerprint = Res.get("ops.network.notKnownYet");
    private TextField satelliteFingerprintTextField;

    private String satelliteAddress = Res.get("ops.network.notKnownYet");
    private TextArea satelliteAddressTextArea;

    public SatelliteSensorOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Res.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        sensorStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Res.get("ops.network.status.sensor"), sensorStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Res.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        satelliteFingerprintTextField = addCompactTopLabelTextField(pane, ++gridRow, Res.get("ops.network.satellite.fingerprintLabel"), satelliteFingerprint, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        satelliteAddressTextArea = addCompactTopLabelTextAreaWithText(pane, satelliteAddress, ++gridRow, Res.get("ops.network.satellite.addressLabel"), true).second;

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
            if(this.sensorStatus != networkState.sensorStatus) {
                this.sensorStatus = networkState.sensorStatus;
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
