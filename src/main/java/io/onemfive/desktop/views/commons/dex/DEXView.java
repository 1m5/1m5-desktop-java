package io.onemfive.desktop.views.commons.dex;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.commons.dex.history.ExchangeHistoryView;
import io.onemfive.desktop.views.commons.dex.rates.ExchangeRatesView;
import io.onemfive.desktop.views.commons.dex.request.ExchangeRequestView;
import io.onemfive.desktop.views.commons.dex.status.ExchangeStatusView;
import io.onemfive.desktop.views.home.HomeView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.common.Resources;

public class DEXView extends ActivatableView {

    private Scene scene;
    private TabPane pane;
    @FXML
    private Tab ratesTab, requestTab, statusTab, historyTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
//    private EventHandler<KeyEvent> keyEventEventHandler;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");

        pane = (TabPane) root;
        ratesTab.setText(Resources.get("commonsView.dex.tabs.rates").toUpperCase());
        requestTab.setText(Resources.get("commonsView.dex.tabs.request").toUpperCase());
        statusTab.setText(Resources.get("commonsView.dex.tabs.status").toUpperCase());
        historyTab.setText(Resources.get("commonsView.dex.tabs.history").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(DEXView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if(newValue == ratesTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeRatesView.class);
            else if(newValue == requestTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeRequestView.class);
            else if (newValue == statusTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeStatusView.class);
            else if (newValue == historyTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeHistoryView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        if(pane.getSelectionModel().getSelectedItem() == ratesTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeRatesView.class);
        else if (pane.getSelectionModel().getSelectedItem() == requestTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeRequestView.class);
        else if (pane.getSelectionModel().getSelectedItem() == statusTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeStatusView.class);
        else if (pane.getSelectionModel().getSelectedItem() == historyTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeHistoryView.class);
        else
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class, ExchangeRatesView.class);

//        if (root.getScene() != null) {
//            scene = root.getScene();
//            scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
//        }
        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);

//        if (scene != null)
//            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
        LOG.info("Deactivated.");
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof ExchangeRatesView) tab = ratesTab;
        else if (view instanceof ExchangeRequestView) tab = requestTab;
        else if (view instanceof ExchangeStatusView) tab = statusTab;
        else if (view instanceof ExchangeHistoryView) tab = historyTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}
