package io.onemfive.desktop.views.settings.services;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.settings.SettingsView;
import io.onemfive.desktop.views.settings.services.dcdn.DCDNSettingsView;
import io.onemfive.desktop.views.settings.services.identity.IdentitySettingsView;
import io.onemfive.desktop.views.settings.services.infovault.InfovaultSettingsView;
import io.onemfive.desktop.views.settings.services.keyring.KeyringSettingsView;
import io.onemfive.desktop.views.settings.services.bitcoin.BitcoinSettingsView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.util.Resources;

public class ServicesSettingsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab identityTab, infovaultTab, keyringTab, bitcoinTab, dcdnTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        identityTab.setText(Resources.get("settings.services.tab.identity").toUpperCase());
        infovaultTab.setText(Resources.get("settings.services.tab.infovault").toUpperCase());
        keyringTab.setText(Resources.get("settings.services.tab.keyring").toUpperCase());
        bitcoinTab.setText(Resources.get("settings.services.tab.bitcoin").toUpperCase());
        dcdnTab.setText(Resources.get("settings.services.tab.dcdn").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(ServicesSettingsView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == identityTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, IdentitySettingsView.class);
            else if (newValue == infovaultTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, InfovaultSettingsView.class);
            else if (newValue == keyringTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, KeyringSettingsView.class);
            else if (newValue == bitcoinTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, BitcoinSettingsView.class);
            else if (newValue == dcdnTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, DCDNSettingsView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == identityTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, IdentitySettingsView.class);
        else if (selectedItem == infovaultTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, InfovaultSettingsView.class);
        else if (selectedItem == keyringTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, KeyringSettingsView.class);
        else if (selectedItem == bitcoinTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, BitcoinSettingsView.class);
        else if (selectedItem == dcdnTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class, DCDNSettingsView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof IdentitySettingsView) tab = identityTab;
        else if (view instanceof InfovaultSettingsView) tab = infovaultTab;
        else if (view instanceof KeyringSettingsView) tab = keyringTab;
        else if (view instanceof BitcoinSettingsView) tab = bitcoinTab;
        else if (view instanceof DCDNSettingsView) tab = dcdnTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}
