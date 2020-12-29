package io.onemfive.desktop.views.commons.exchange;

import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.commons.exchange.accounts.ExchangeAccountsView;
import io.onemfive.desktop.views.commons.exchange.buy.ExchangeBuyView;
import io.onemfive.desktop.views.commons.exchange.market.ExchangeMarketView;
import io.onemfive.desktop.views.commons.exchange.qa.ExchangeQAView;
import io.onemfive.desktop.views.commons.exchange.sell.ExchangeSellView;
import io.onemfive.desktop.views.commons.exchange.settings.ExchangeSettingsView;
import io.onemfive.desktop.views.home.HomeView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ra.util.Resources;

public class ExchangeView extends ActivatableView {

    private TabPane pane;
    @FXML
    private Tab accountsTab, buyTab, marketTab, qaTab, sellTab, settingsTab;

    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (TabPane)root;
        accountsTab.setText(Resources.get("commonsView.exchange.tab.accounts").toUpperCase());
        buyTab.setText(Resources.get("commonsView.exchange.tab.buy").toUpperCase());
        marketTab.setText(Resources.get("commonsView.exchange.tab.market").toUpperCase());
        qaTab.setText(Resources.get("commonsView.exchange.tab.qa").toUpperCase());
        sellTab.setText(Resources.get("commonsView.exchange.tab.sell").toUpperCase());
        settingsTab.setText(Resources.get("commonsView.exchange.tab.settings").toUpperCase());

        navigationListener = viewPath -> {
            if (viewPath.size() == 4 && viewPath.indexOf(ExchangeView.class) == 2)
                loadView(viewPath.tip());
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == accountsTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeAccountsView.class);
            else if (newValue == buyTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeBuyView.class);
            else if (newValue == marketTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeMarketView.class);
            else if (newValue == qaTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeQAView.class);
            else if (newValue == sellTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeSellView.class);
            else if (newValue == settingsTab)
                MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeSettingsView.class);
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        pane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        MVC.navigation.addListener(navigationListener);

        Tab selectedItem = pane.getSelectionModel().getSelectedItem();
        if (selectedItem == accountsTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeAccountsView.class);
        else if (selectedItem == buyTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeBuyView.class);
        else if (selectedItem == marketTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeMarketView.class);
        else if (selectedItem == qaTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeQAView.class);
        else if (selectedItem == sellTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeSellView.class);
        else if (selectedItem == settingsTab)
            MVC.navigation.navigateTo(HomeView.class, CommonsView.class, ExchangeView.class, ExchangeSettingsView.class);
    }

    @Override
    protected void deactivate() {
        pane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        MVC.navigation.removeListener(navigationListener);
    }

    private void loadView(Class<? extends View> viewClass) {
        final Tab tab;
        View view = MVC.loadView(viewClass);

        if (view instanceof ExchangeAccountsView) tab = accountsTab;
        else if (view instanceof ExchangeBuyView) tab = buyTab;
        else if (view instanceof ExchangeMarketView) tab = marketTab;
        else if (view instanceof ExchangeQAView) tab = qaTab;
        else if (view instanceof ExchangeSellView) tab = sellTab;
        else if (view instanceof ExchangeSettingsView) tab = settingsTab;
        else throw new IllegalArgumentException("Navigation to " + viewClass + " is not supported");

        if (tab.getContent() != null && tab.getContent() instanceof ScrollPane) {
            ((ScrollPane) tab.getContent()).setContent(view.getRoot());
        } else {
            tab.setContent(view.getRoot());
        }
        pane.getSelectionModel().select(tab);
    }

}
