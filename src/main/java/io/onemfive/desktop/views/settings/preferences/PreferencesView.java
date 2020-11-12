package io.onemfive.desktop.views.settings.preferences;

import io.onemfive.desktop.CssTheme;
import io.onemfive.desktop.DesktopApp;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.components.overlays.popups.Popup;
import io.onemfive.desktop.user.Preferences;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import ra.util.LanguageUtil;
import ra.util.Resources;

import java.util.Locale;

import static io.onemfive.desktop.util.FormBuilder.*;

public class PreferencesView extends ActivatableView {

    private GridPane pane;
    private ComboBox<String> userLanguageComboBox;

    private ToggleButton useAnimations, useDarkMode;
    private int gridRow = 0;

    private ObservableList<String> languageCodes;

    public PreferencesView() {

    }

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;
        languageCodes = FXCollections.observableArrayList(LanguageUtil.getUserLanguageCodes());

        TitledGroupBg titledGroupBg = addTitledGroupBg(pane, gridRow, 2, Resources.get("setting.preferences.general"));
        GridPane.setColumnSpan(titledGroupBg, 1);
        userLanguageComboBox = addComboBox(pane, ++gridRow, Resources.get("shared.language"), Layout.FIRST_ROW_DISTANCE);

        TitledGroupBg titledGroupBg1 = addTitledGroupBg(pane, ++gridRow, 3, Resources.get("setting.preferences.displayOptions"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(titledGroupBg1, 1);
        useAnimations = addSlideToggleButton(pane, ++gridRow, Resources.get("setting.preferences.useAnimations"), Layout.TWICE_FIRST_ROW_DISTANCE);
        useDarkMode = addSlideToggleButton(pane, ++gridRow, Resources.get("setting.preferences.useDarkMode"));

        LOG.info("Initialized");
    }

    @Override
    protected void activate() {
        activateGeneralOptions();
        activateDisplayPreferences();
    }

    @Override
    protected void deactivate() {
        deactivateGeneralOptions();
        deactivateDisplayPreferences();
    }

    private void activateGeneralOptions() {

        userLanguageComboBox.setItems(languageCodes);
        userLanguageComboBox.getSelectionModel().select(Preferences.locale.getLanguage());
        userLanguageComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String code) {
                return LanguageUtil.getDisplayName(code);
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });

        userLanguageComboBox.setOnAction(e -> {
            String selectedItem = userLanguageComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Preferences.locale = Locale.forLanguageTag(selectedItem);
                new Popup().information(Resources.get("settings.preferences.languageChange"))
                        .closeButtonText(Resources.get("shared.ok"))
                        .show();
            }
        });
    }

    private void activateDisplayPreferences() {

        useAnimations.setSelected(Preferences.useAnimations);
        useAnimations.setOnAction(e -> Preferences.useAnimations = useAnimations.isSelected());

        useDarkMode.setSelected(Preferences.cssTheme == 1);
        useDarkMode.setOnAction(e -> {
            Preferences.cssTheme = useDarkMode.isSelected() ? 1 : 0;
            CssTheme.loadSceneStyles(DesktopApp.scene, Preferences.cssTheme);
        });

    }

    private void deactivateGeneralOptions() {
        userLanguageComboBox.setOnAction(null);
    }

    private void deactivateDisplayPreferences() {
        useAnimations.setOnAction(null);
        useDarkMode.setOnAction(null);
    }

}
