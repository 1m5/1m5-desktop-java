package io.onemfive.desktop.views.ops.network.lifi;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ra.common.network.NetworkState;
import ra.common.network.NetworkStatus;
import ra.util.Resources;
import ra.util.StringUtil;

import static io.onemfive.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class LiFiOpsView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private NetworkStatus networkStatus = NetworkStatus.NOT_INSTALLED;
    private String networkStatusField = StringUtil.capitalize(networkStatus.name().toLowerCase().replace('_', ' '));
    private TextField networkStatusTextField;

    private String lifiFriendlyName = Resources.get("ops.network.notKnownYet");
    private TextField lifiFriendlyNameTextField;

    private String lifiAddress = Resources.get("ops.network.notKnownYet");
    private TextField lifiAddressTextField;

    public LiFiOpsView() {
        super();
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 2, Resources.get("ops.network.status"));
        GridPane.setColumnSpan(statusGroup, 1);
        networkStatusTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.status.sensor"), networkStatusField, Layout.FIRST_ROW_DISTANCE).second;

        TitledGroupBg localNodeGroup = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("ops.network.localNode"),Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(localNodeGroup, 1);
        lifiFriendlyNameTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.lifi.friendlyNameLabel"), lifiFriendlyName, Layout.TWICE_FIRST_ROW_DISTANCE).second;
        lifiAddressTextField = addCompactTopLabelTextField(pane, ++gridRow, Resources.get("ops.network.lifi.addressLabel"), lifiAddress).second;

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
                lifiAddress = networkState.localPeer.getDid().getPublicKey().getAddress();
                if(lifiAddressTextField!=null)
                    lifiAddressTextField.setText(lifiAddress);
                lifiFriendlyName = networkState.localPeer.getDid().getUsername();
                if(lifiFriendlyNameTextField!=null)
                    lifiFriendlyNameTextField.setText(lifiFriendlyName);
            }
        } else {
            LOG.warning("Received unknown model update with name: "+name);
        }
    }

}
