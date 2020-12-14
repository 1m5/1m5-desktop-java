package io.onemfive.desktop.views.settings.about;

import io.onemfive.desktop.components.HyperlinkWithIcon;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static io.onemfive.desktop.util.FormBuilder.addHyperlinkWithIcon;
import static io.onemfive.desktop.util.FormBuilder.addLabel;
import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class AboutView extends ActivatableView {

    private int gridRow = 0;

    public AboutView() {
        super();
    }

    @Override
    public void initialize() {
        LOG.info("Initializing...");
        GridPane pane = (GridPane)root;

        addTitledGroupBg(pane, gridRow, 4, Resources.get("setting.about.about1M5"));
        Label label = addLabel(pane, ++gridRow, Resources.get("setting.about.about"), Layout.FIRST_ROW_DISTANCE);
        label.setWrapText(true);
        GridPane.setColumnSpan(label, 2);
        GridPane.setHalignment(label, HPos.LEFT);
        HyperlinkWithIcon hyperlinkWithIcon = addHyperlinkWithIcon(pane, ++gridRow, Resources.get("setting.about.web"), "https://1m5.io");
        GridPane.setColumnSpan(hyperlinkWithIcon, 2);
        hyperlinkWithIcon = addHyperlinkWithIcon(pane, ++gridRow, Resources.get("setting.about.code"), "https://github.com/1m5");
        GridPane.setColumnSpan(hyperlinkWithIcon, 2);

        addTitledGroupBg(pane, gridRow, 3, Resources.get("setting.about.support"), Layout.GROUP_DISTANCE);
        label = addLabel(pane, ++gridRow, Resources.get("setting.about.def"), Layout.TWICE_FIRST_ROW_DISTANCE);
        label.setWrapText(true);
        GridPane.setColumnSpan(label, 2);
        GridPane.setHalignment(label, HPos.LEFT);
        hyperlinkWithIcon = addHyperlinkWithIcon(pane, ++gridRow, Resources.get("setting.about.contribute"), "https://1m5.io/collaborate.html");
        GridPane.setColumnSpan(hyperlinkWithIcon, 2);

        addTitledGroupBg(pane, gridRow, 8, Resources.get("setting.about.versions"), Layout.GROUP_DISTANCE);
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.version1M5"), "0.6.5", Layout.TWICE_FIRST_ROW_DISTANCE);
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionTOR"), "0.3.2.10");
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionI2P"), "0.9.47");
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionBT"), "Bluecove 2.1.0");
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionWiFiDirect"), "Not Registered");
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionSDR"), "GNU Radio - Not Registered");
        addCompactTopLabelTextField(pane, ++gridRow, Resources.get("setting.about.versionLiFi"), "PureLiFi - Not Registered");
        LOG.info("Initialized");
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

}

