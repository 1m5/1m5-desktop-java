package io.onemfive.desktop.views.personal.identities;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.personal.PersonalView;
import io.onemfive.desktop.views.personal.identities.add.AddIdentityView;
import io.onemfive.desktop.views.personal.identities.create.CreateIdentityView;
import io.onemfive.desktop.views.personal.identities.dashboard.DashboardIdentityView;
import io.onemfive.desktop.views.personal.identities.details.DetailsIdentityView;
import io.onemfive.desktop.views.personal.identities.list.ListIdentityView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import ra.common.Resources;

public class IdentitiesView extends ActivatableView {

    private Scene scene;
    private TabPane pane;
    @FXML
    private Tab addTab, createTab, dashboardTab, detailsTab, listTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
//    private EventHandler<KeyEvent> keyEventEventHandler;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");

        pane = (TabPane)root;
        addTab.setText(Resources.get("personalIdentitiesView.tabs.add").toUpperCase());
        createTab.setText(Resources.get("personalIdentitiesView.tabs.create").toUpperCase());
        dashboardTab.setText(Resources.get("personalIdentitiesView.tabs.dashboard").toUpperCase());
        detailsTab.setText(Resources.get("personalIdentitiesView.tabs.details").toUpperCase());
        listTab.setText(Resources.get("personalIdentitiesView.tabs.list").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(IdentitiesView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if(newValue == addTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, AddIdentityView.class);
            else if(newValue == createTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, CreateIdentityView.class);
            else if (newValue == dashboardTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, DashboardIdentityView.class);
            else if (newValue == detailsTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, DetailsIdentityView.class);
            else if (newValue == listTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, ListIdentityView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        if(pane.getSelectionModel().getSelectedItem() == addTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, AddIdentityView.class);
        else if (pane.getSelectionModel().getSelectedItem() == createTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, CreateIdentityView.class);
        else if (pane.getSelectionModel().getSelectedItem() == detailsTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, DetailsIdentityView.class);
        else if (pane.getSelectionModel().getSelectedItem() == listTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, ListIdentityView.class);
        else
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, IdentitiesView.class, DashboardIdentityView.class);

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

        if (view instanceof AddIdentityView) tab = addTab;
        else if (view instanceof CreateIdentityView) tab = createTab;
        else if (view instanceof DashboardIdentityView) tab = dashboardTab;
        else if (view instanceof DetailsIdentityView) tab = detailsTab;
        else if (view instanceof ListIdentityView) tab = listTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }
}
