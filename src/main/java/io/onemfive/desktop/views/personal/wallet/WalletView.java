package io.onemfive.desktop.views.personal.wallet;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.personal.PersonalView;
import io.onemfive.desktop.views.personal.wallet.create.CreateWalletView;
import io.onemfive.desktop.views.personal.wallet.details.DetailsWalletView;
import io.onemfive.desktop.views.personal.wallet.info.InfoWalletView;
import io.onemfive.desktop.views.personal.wallet.receive.ReceiveWalletView;
import io.onemfive.desktop.views.personal.wallet.send.SendWalletView;
import io.onemfive.desktop.views.personal.wallet.sweep.SweepWalletView;
import io.onemfive.desktop.views.personal.wallet.transaction.TransactionWalletView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import ra.util.Resources;

public class WalletView extends ActivatableView {

    private Scene scene;
    private TabPane pane;
    @FXML
    private Tab infoTab, createTab, detailsTab, sendTab, receiveTab, transactionTab, sweepTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;
//    private EventHandler<KeyEvent> keyEventEventHandler;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");

        pane = (TabPane)root;
        infoTab.setText(Resources.get("personalView.wallet.tabs.info").toUpperCase());
        createTab.setText(Resources.get("personalView.wallet.tabs.create").toUpperCase());
        detailsTab.setText(Resources.get("personalView.wallet.tabs.details").toUpperCase());
        sendTab.setText(Resources.get("personalView.wallet.tabs.send").toUpperCase());
        receiveTab.setText(Resources.get("personalView.wallet.tabs.receive").toUpperCase());
        transactionTab.setText(Resources.get("personalView.wallet.tabs.tx").toUpperCase());
        sweepTab.setText(Resources.get("personalView.wallet.tabs.sweep").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(WalletView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if(newValue == infoTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, InfoWalletView.class);
            else if(newValue == createTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, CreateWalletView.class);
            else if (newValue == detailsTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, DetailsWalletView.class);
            else if (newValue == sendTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, SendWalletView.class);
            else if (newValue == receiveTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, ReceiveWalletView.class);
            else if (newValue == transactionTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, TransactionWalletView.class);
            else if (newValue == sweepTab)
                MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, SweepWalletView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        if(pane.getSelectionModel().getSelectedItem() == createTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, CreateWalletView.class);
        else if (pane.getSelectionModel().getSelectedItem() == detailsTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, DetailsWalletView.class);
        else if (pane.getSelectionModel().getSelectedItem() == sendTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, SendWalletView.class);
        else if (pane.getSelectionModel().getSelectedItem() == receiveTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, ReceiveWalletView.class);
        else if (pane.getSelectionModel().getSelectedItem() == transactionTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, TransactionWalletView.class);
        else if (pane.getSelectionModel().getSelectedItem() == sweepTab)
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, SweepWalletView.class);
        else
            MVC.navigation.navigateTo(HomeView.class, PersonalView.class, WalletView.class, InfoWalletView.class);

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

        if (view instanceof InfoWalletView) tab = infoTab;
        else if (view instanceof CreateWalletView) tab = createTab;
        else if (view instanceof DetailsWalletView) tab = detailsTab;
        else if (view instanceof SendWalletView) tab = sendTab;
        else if (view instanceof ReceiveWalletView) tab = receiveTab;
        else if (view instanceof TransactionWalletView) tab = transactionTab;
        else if (view instanceof SweepWalletView) tab = sweepTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }
}

