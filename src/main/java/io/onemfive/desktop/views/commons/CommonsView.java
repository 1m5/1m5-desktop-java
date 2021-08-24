package io.onemfive.desktop.views.commons;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.commons.agora.AgoraView;
import io.onemfive.desktop.views.commons.browser.BrowserView;
import io.onemfive.desktop.views.commons.dashboard.DashboardView;
import io.onemfive.desktop.views.commons.dex.DEXView;
import io.onemfive.desktop.views.commons.topics.TopicsView;
import io.onemfive.desktop.views.home.HomeView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.common.Resources;

public class CommonsView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab agoraTab, browserTab, dashboardTab, topicsTab, dexTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
//    private EventHandler<KeyEvent> keyEventEventHandler;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");

        pane = (TabPane)root;
        agoraTab.setText(Resources.get("commonsView.tabs.agora").toUpperCase());
        browserTab.setText(Resources.get("commonsView.tabs.browser").toUpperCase());
        dashboardTab.setText(Resources.get("commonsView.tabs.dashboard").toUpperCase());
        topicsTab.setText(Resources.get("commonsView.tabs.topics").toUpperCase());
        dexTab.setText(Resources.get("commonsView.tabs.dex").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 3 && viewPath.indexOf(CommonsView.class) == 1)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == agoraTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, AgoraView.class);
            else if (newValue == browserTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, BrowserView.class);
            else if (newValue == dashboardTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DashboardView.class);
            else if (newValue == topicsTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, TopicsView.class);
            else if (newValue == dexTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        if (pane.getSelectionModel().getSelectedItem() == agoraTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, AgoraView.class);
        else if (pane.getSelectionModel().getSelectedItem() == browserTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, BrowserView.class);
        else if (pane.getSelectionModel().getSelectedItem() == topicsTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, TopicsView.class);
        else if(pane.getSelectionModel().getSelectedItem() == dexTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DEXView.class);
        else
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, DashboardView.class);

//        if (root.getScene() != null) {
//            scene = root.getScene();
//            scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
//        }
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);

//        if (scene != null)
//            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
    }


    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof AgoraView) tab = agoraTab;
        else if (view instanceof BrowserView) tab = browserTab;
        else if (view instanceof DashboardView) tab = dashboardTab;
        else if (view instanceof TopicsView) tab = topicsTab;
        else if (view instanceof DEXView) tab = dexTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }
}
