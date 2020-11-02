package io.onemfive.desktop.views.settings;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.settings.about.AboutView;
import io.onemfive.desktop.views.settings.network.NetworkSettingsView;
import io.onemfive.desktop.views.settings.network.ims.IMSSettingsView;
import io.onemfive.desktop.views.settings.platform.PlatformSettingsView;
import io.onemfive.desktop.views.settings.preferences.PreferencesView;
import io.onemfive.desktop.views.settings.services.ServicesSettingsView;
import io.onemfive.util.Res;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SettingsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab preferencesTab, servicesTab, networkTab, platformTab, aboutTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        preferencesTab.setText(Res.get("settings.tab.preferences").toUpperCase());
        servicesTab.setText(Res.get("settings.tab.services").toUpperCase());
        networkTab.setText(Res.get("settings.tab.network").toUpperCase());
        platformTab.setText(Res.get("settings.tab.platform").toUpperCase());
        aboutTab.setText(Res.get("settings.tab.about").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 3 && viewPath.indexOf(SettingsView.class) == 1)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == preferencesTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, PreferencesView.class);
            else if (newValue == servicesTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class);
            else if (newValue == networkTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class);
            else if (newValue == platformTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, PlatformSettingsView.class);
            else if (newValue == aboutTab)
                MVC.navigation.navigateTo(HomeView.class, SettingsView.class, AboutView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == preferencesTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, PreferencesView.class);
        else if (selectedItem == servicesTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, ServicesSettingsView.class);
        else if (selectedItem == networkTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, NetworkSettingsView.class);
        else if (selectedItem == platformTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, PlatformSettingsView.class);
        else if (selectedItem == aboutTab)
            MVC.navigation.navigateTo(HomeView.class, SettingsView.class, AboutView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof PreferencesView) tab = preferencesTab;
        else if (view instanceof ServicesSettingsView) tab = servicesTab;
        else if (view instanceof NetworkSettingsView) tab = networkTab;
        else if (view instanceof PlatformSettingsView) tab = platformTab;
        else if (view instanceof AboutView) tab = aboutTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }
}
