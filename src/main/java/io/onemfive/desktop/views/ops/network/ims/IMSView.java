package io.onemfive.desktop.views.ops.network.ims;

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

public class IMSView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus networkStatus = NetworkStatus.CLOSED;
    private ServiceStatus serviceStatus = ServiceStatus.NOT_INITIALIZED;

    private String serviceStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField serviceStatusTextField;

    private String imsFingerprint = Resources.get("ops.network.notKnownYet");
    private String imsAddress = Resources.get("ops.network.notKnownYet");

    private TextField imsFingerprintTextField;
    private TextArea imsAddressTextField;

    public IMSView() {
        super();
    }

    public void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        serviceStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.network"), serviceStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        imsFingerprintTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.1m5.fingerprintLabel"), imsFingerprint, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        imsAddressTextField = addCompactTopLabelTextAreaWithText(pane, imsAddress, ++gridRow, Resources.get("ops.network.1m5.addressLabel"), true).second;

        LOG.info("Initialized");
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void modelUpdated(String name, Object object) {
        if(object instanceof NetworkState) {
            LOG.info("NetworkState received to update model.");
            NetworkState networkState = (NetworkState)object;
            if(this.networkStatus != networkState.networkStatus) {
                this.networkStatus = networkState.networkStatus;
                if(serviceStatusField != null) {
                    serviceStatusTextField.setText(StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' ')));
                }
            }
            if(networkState.localPeer!=null) {
                imsFingerprint = networkState.localPeer.getDid().getPublicKey().getFingerprint();
                imsAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                if(imsFingerprintTextField !=null) {
                    imsFingerprintTextField.setText(imsFingerprint);
                }
                if(imsAddressTextField !=null) {
                    imsAddressTextField.setText(imsAddress);
                }
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}

