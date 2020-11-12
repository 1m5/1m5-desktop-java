package io.onemfive.desktop.views.ops.network;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.ops.OpsView;
import io.onemfive.desktop.views.ops.network.bluetooth.BluetoothOpsView;
import io.onemfive.desktop.views.ops.network.fullspectrum.FullSpectrumRadioOpsView;
import io.onemfive.desktop.views.ops.network.i2p.I2POpsView;
import io.onemfive.desktop.views.ops.network.idn.IDNView;
import io.onemfive.desktop.views.ops.network.lifi.LiFiOpsView;
import io.onemfive.desktop.views.ops.network.satellite.SatelliteOpsView;
import io.onemfive.desktop.views.ops.network.tor.TOROpsView;
import io.onemfive.desktop.views.ops.network.wifidirect.WifiDirectOpsView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.util.Resources;

public class NetworkOpsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab imsTab, torTab, i2pTab, wifiDirectTab, bluetoothTab, satelliteTab, fsRadioTab, lifiTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        imsTab.setText(Resources.get("ops.network.tab.ims").toUpperCase());
        torTab.setText(Resources.get("ops.network.tab.tor").toUpperCase());
        i2pTab.setText(Resources.get("ops.network.tab.i2p").toUpperCase());
        wifiDirectTab.setText(Resources.get("ops.network.tab.wifiDirect").toUpperCase());
        bluetoothTab.setText(Resources.get("ops.network.tab.bluetooth").toUpperCase());
        satelliteTab.setText(Resources.get("ops.network.tab.satellite").toUpperCase());
        fsRadioTab.setText(Resources.get("ops.network.tab.fsRadio").toUpperCase());
        lifiTab.setText(Resources.get("ops.network.tab.lifi").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(NetworkOpsView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == imsTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, IDNView.class);
            else if (newValue == torTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, TOROpsView.class);
            else if (newValue == i2pTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, I2POpsView.class);
            else if (newValue == wifiDirectTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, WifiDirectOpsView.class);
            else if (newValue == bluetoothTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, BluetoothOpsView.class);
            else if (newValue == satelliteTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, SatelliteOpsView.class);
            else if (newValue == fsRadioTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, FullSpectrumRadioOpsView.class);
            else if (newValue == lifiTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, LiFiOpsView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == imsTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, IDNView.class);
        else if (selectedItem == torTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, TOROpsView.class);
        else if (selectedItem == i2pTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, I2POpsView.class);
        else if (selectedItem == wifiDirectTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, WifiDirectOpsView.class);
        else if (selectedItem == bluetoothTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, BluetoothOpsView.class);
        else if (selectedItem == satelliteTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, SatelliteOpsView.class);
        else if (selectedItem == fsRadioTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, FullSpectrumRadioOpsView.class);
        else if (selectedItem == lifiTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, NetworkOpsView.class, LiFiOpsView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof IDNView) tab = imsTab;
        else if (view instanceof TOROpsView) tab = torTab;
        else if (view instanceof I2POpsView) tab = i2pTab;
        else if (view instanceof WifiDirectOpsView) tab = wifiDirectTab;
        else if (view instanceof BluetoothOpsView) tab = bluetoothTab;
        else if (view instanceof SatelliteOpsView) tab = satelliteTab;
        else if (view instanceof FullSpectrumRadioOpsView) tab = fsRadioTab;
        else if (view instanceof LiFiOpsView) tab = lifiTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}

