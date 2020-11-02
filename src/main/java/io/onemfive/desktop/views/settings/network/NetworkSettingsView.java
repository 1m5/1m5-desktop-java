package io.onemfive.desktop.views.settings.network;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.settings.SettingsView;
import io.onemfive.desktop.views.settings.network.bluetooth.BluetoothSensorSettingsView;
import io.onemfive.desktop.views.settings.network.fullspectrum.FullSpectrumRadioSensorSettingsView;
import io.onemfive.desktop.views.settings.network.i2p.I2PSensorSettingsView;
import io.onemfive.desktop.views.settings.network.ims.IMSSettingsView;
import io.onemfive.desktop.views.settings.network.lifi.LiFiSensorSettingsView;
import io.onemfive.desktop.views.settings.network.satellite.SatelliteSensorSettingsView;
import io.onemfive.desktop.views.settings.network.tor.TORSensorSettingsView;
import io.onemfive.desktop.views.settings.network.wifidirect.WifiDirectSensorSettingsView;
import ra.util.Resources;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NetworkSettingsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab imsTab, torTab, i2pTab, wifiDirectTab, bluetoothTab, satelliteTab, fsRadioTab, lifiTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        imsTab.setText(Resources.get("settings.network.tab.ims").toUpperCase());
        torTab.setText(Resources.get("settings.network.tab.tor").toUpperCase());
        i2pTab.setText(Resources.get("settings.network.tab.i2p").toUpperCase());
        wifiDirectTab.setText(Resources.get("settings.network.tab.wifiDirect").toUpperCase());
        bluetoothTab.setText(Resources.get("settings.network.tab.bluetooth").toUpperCase());
        satelliteTab.setText(Resources.get("settings.network.tab.satellite").toUpperCase());
        fsRadioTab.setText(Resources.get("settings.network.tab.fsRadio").toUpperCase());
        lifiTab.setText(Resources.get("settings.network.tab.lifi").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(NetworkSettingsView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == imsTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, IMSSettingsView.class);
            else if (newValue == torTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, TORSensorSettingsView.class);
            else if (newValue == i2pTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, I2PSensorSettingsView.class);
            else if (newValue == wifiDirectTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, WifiDirectSensorSettingsView.class);
            else if (newValue == bluetoothTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, BluetoothSensorSettingsView.class);
            else if (newValue == satelliteTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, SatelliteSensorSettingsView.class);
            else if (newValue == fsRadioTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, FullSpectrumRadioSensorSettingsView.class);
            else if (newValue == lifiTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, LiFiSensorSettingsView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == imsTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, IMSSettingsView.class);
        else if (selectedItem == torTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, TORSensorSettingsView.class);
        else if (selectedItem == i2pTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, I2PSensorSettingsView.class);
        else if (selectedItem == wifiDirectTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, WifiDirectSensorSettingsView.class);
        else if (selectedItem == bluetoothTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, BluetoothSensorSettingsView.class);
        else if (selectedItem == satelliteTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, SatelliteSensorSettingsView.class);
        else if (selectedItem == fsRadioTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, FullSpectrumRadioSensorSettingsView.class);
        else if (selectedItem == lifiTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class, LiFiSensorSettingsView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof IMSSettingsView) tab = imsTab;
        else if (view instanceof TORSensorSettingsView) tab = torTab;
        else if (view instanceof I2PSensorSettingsView) tab = i2pTab;
        else if (view instanceof WifiDirectSensorSettingsView) tab = wifiDirectTab;
        else if (view instanceof BluetoothSensorSettingsView) tab = bluetoothTab;
        else if (view instanceof SatelliteSensorSettingsView) tab = satelliteTab;
        else if (view instanceof FullSpectrumRadioSensorSettingsView) tab = fsRadioTab;
        else if (view instanceof LiFiSensorSettingsView) tab = lifiTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}

