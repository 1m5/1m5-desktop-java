package io.onemfive.desktop.views.community;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.community.agora.AgoraView;
import io.onemfive.desktop.views.community.calendar.CalendarView;
import io.onemfive.desktop.views.community.dashboard.DashboardView;
import io.onemfive.desktop.views.community.social.SocialView;
import io.onemfive.desktop.views.community.wallet.WalletView;
import io.onemfive.desktop.views.home.HomeView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.common.social.Group;
import ra.util.Resources;

import java.util.Map;

/**
 * Communities hard-capped to 150 people based on Dunbar's Number.
 * https://en.wikipedia.org/wiki/Dunbar%27s_number
 */
public class CommunityView extends ActivatableView {

    private Scene scene;
    private TabPane pane;
    @FXML
    private Tab agoraTab, calendarTab, dashboardTab, searchTab, socialTab, walletTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
//    private EventHandler<KeyEvent> keyEventEventHandler;

    private Map<String, Group> communities;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");

        pane = (TabPane)root;
        agoraTab.setText(Resources.get("communityView.tabs.agora").toUpperCase());
        calendarTab.setText(Resources.get("communityView.tabs.calendar").toUpperCase());
        dashboardTab.setText(Resources.get("communityView.tabs.dashboard").toUpperCase());
        socialTab.setText(Resources.get("communityView.tabs.social").toUpperCase());
        searchTab.setText(Resources.get("communityView.tabs.search").toUpperCase());
        walletTab.setText(Resources.get("communityView.tabs.wallet").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 3 && viewPath.indexOf(CommunityView.class) == 1)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == agoraTab)
                MVC.navigation.navigateTo(HomeView.class, CommunityView.class, AgoraView.class);
            else if (newValue == calendarTab)
                MVC.navigation.navigateTo(HomeView.class, CommunityView.class, CalendarView.class);
            else if (newValue == dashboardTab)
                MVC.navigation.navigateTo(HomeView.class, CommunityView.class, DashboardView.class);
            else if (newValue == socialTab)
                MVC.navigation.navigateTo(HomeView.class, CommunityView.class, SocialView.class);
            else if (newValue == walletTab)
                MVC.navigation.navigateTo(HomeView.class, CommunityView.class, WalletView.class);
        };

        // Load Communities
        // Default: Immediate Family and Extended Family

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        if (pane.getSelectionModel().getSelectedItem() == agoraTab)
            MVC.navigation.navigateTo(HomeView.class, CommunityView.class, AgoraView.class);
        else if (pane.getSelectionModel().getSelectedItem() == calendarTab)
            MVC.navigation.navigateTo(HomeView.class, CommunityView.class, CalendarView.class);
        else if (pane.getSelectionModel().getSelectedItem() == socialTab)
            MVC.navigation.navigateTo(HomeView.class, CommunityView.class, SocialView.class);
        else if (pane.getSelectionModel().getSelectedItem() == walletTab)
            MVC.navigation.navigateTo(HomeView.class, CommunityView.class, WalletView.class);
        else
            MVC.navigation.navigateTo(HomeView.class, CommunityView.class, DashboardView.class);

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
        else if (view instanceof CalendarView) tab = calendarTab;
        else if (view instanceof DashboardView) tab = dashboardTab;
        else if (view instanceof SocialView) tab = socialTab;
        else if (view instanceof WalletView) tab = walletTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }
}
