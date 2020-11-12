package io.onemfive.desktop.views.ops.services.monetary;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.ops.OpsView;
import io.onemfive.desktop.views.ops.services.ServicesOpsView;
import io.onemfive.desktop.views.ops.services.monetary.dex.DEXOpsView;
import io.onemfive.desktop.views.ops.services.monetary.bitcoin.BitcoinOpsView;
import io.onemfive.desktop.views.ops.services.monetary.komodo.KomodoOpsView;
import io.onemfive.desktop.views.ops.services.monetary.monero.MoneroOpsView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.util.Resources;

public class MonetaryOpsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab komodoTab, moneroTab, bitcoinTab, dexTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        komodoTab.setText(Resources.get("ops.services.monetary.tab.komodo").toUpperCase());
        moneroTab.setText(Resources.get("ops.services.monetary.tab.monero").toUpperCase());
        bitcoinTab.setText(Resources.get("ops.services.monetary.tab.bitcoin").toUpperCase());
        dexTab.setText(Resources.get("ops.services.monetary.tab.dex").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 5 && viewPath.indexOf(MonetaryOpsView.class) == 3)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == komodoTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, KomodoOpsView.class);
            else if (newValue == moneroTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, MoneroOpsView.class);
            else if (newValue == bitcoinTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, BitcoinOpsView.class);
            else if (newValue == dexTab)
                MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, DEXOpsView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == komodoTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, KomodoOpsView.class);
        else if (selectedItem == moneroTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, MoneroOpsView.class);
        else if (selectedItem == bitcoinTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, BitcoinOpsView.class);
        else if (selectedItem == dexTab)
            MVC.navigation.navigateTo(HomeView.class, OpsView.class, ServicesOpsView.class, MonetaryOpsView.class, DEXOpsView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof KomodoOpsView) tab = komodoTab;
        else if (view instanceof MoneroOpsView) tab = moneroTab;
        else if (view instanceof BitcoinOpsView) tab = bitcoinTab;
        else if (view instanceof DEXOpsView) tab = dexTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}
