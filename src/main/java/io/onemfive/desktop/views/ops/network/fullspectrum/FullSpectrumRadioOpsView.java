package io.onemfive.desktop.views.ops.network.fullspectrum;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.common.Resources;
import ra.common.StringUtil;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static io.onemfive.desktop.util.FormBuilder.*;

public class FullSpectrumRadioOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private String networkStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField networkStatusTextField;

    private String fsRadioFingerprint = Resources.get("ops.network.notKnownYet");
    private String fsRadioAddress = Resources.get("ops.network.notKnownYet");
    private TextField fsRadioFingerprintTextField;
    private TextArea fsRadioAddressTextArea;

    public FullSpectrumRadioOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        networkStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.network"), networkStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        fsRadioFingerprintTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.fullspectrum.fingerprintLabel"), fsRadioFingerprint, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        fsRadioAddressTextArea = addCompactTopLabelTextAreaWithText(pane, fsRadioAddress, ++gridRow, Resources.get("ops.network.fullspectrum.addressLabel"), true).second;

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
            if(this.networkStatus != networkState.networkStatus) {
                this.networkStatus = networkState.networkStatus;
                if(networkStatusField != null) {
                    networkStatusTextField.setText(StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(networkState.localPeer!=null) {
                fsRadioFingerprint = networkState.localPeer.getDid().getPublicKey().getFingerprint();
                fsRadioAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                if(fsRadioFingerprintTextField !=null) {
                    fsRadioFingerprintTextField.setText(fsRadioFingerprint);
                }
                if(fsRadioAddressTextArea !=null) {
                    fsRadioAddressTextArea.setText(fsRadioAddress);
                }
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
