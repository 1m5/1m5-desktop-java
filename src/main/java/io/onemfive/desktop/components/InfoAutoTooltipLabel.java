package io.onemfive.desktop.components;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import io.onemfive.desktop.components.controlsfx.control.PopOver;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

import static io.onemfive.desktop.util.FormBuilder.getIcon;

public class InfoAutoTooltipLabel extends AutoTooltipLabel {

    public static final int DEFAULT_WIDTH = 300;
    private Node textIcon;
    private PopOverWrapper popoverWrapper = new PopOverWrapper();
    private ContentDisplay contentDisplay;

    public InfoAutoTooltipLabel(String text, GlyphIcons icon, ContentDisplay contentDisplay, String info) {
        this(text, contentDisplay);

        setIcon(icon);
        positionAndActivateIcon(contentDisplay, info, DEFAULT_WIDTH);
    }

    public InfoAutoTooltipLabel(String text, AwesomeIcon icon, ContentDisplay contentDisplay, String info, double width) {
        super(text);

        setIcon(icon);
        positionAndActivateIcon(contentDisplay, info, width);
    }

    public InfoAutoTooltipLabel(String text, ContentDisplay contentDisplay) {
        super(text);
        this.contentDisplay = contentDisplay;
    }

    public void setIcon(GlyphIcons icon) {
        textIcon = getIcon(icon);
    }

    public void setIcon(GlyphIcons icon, String info) {
        setIcon(icon);
        positionAndActivateIcon(contentDisplay, info, DEFAULT_WIDTH);
    }

    public void setIcon(AwesomeIcon icon) {
        textIcon = getIcon(icon);
    }

    public void hideIcon() {
        textIcon = null;
        setGraphic(textIcon);
    }

    private void positionAndActivateIcon(ContentDisplay contentDisplay, String info, double width) {
        textIcon.setOpacity(0.4);
        textIcon.getStyleClass().add("tooltip-icon");
        textIcon.setOnMouseEntered(e -> popoverWrapper.showPopOver(() -> createInfoPopOver(info, width)));
        textIcon.setOnMouseExited(e -> popoverWrapper.hidePopOver());

        setGraphic(textIcon);
        setContentDisplay(contentDisplay);
    }

    private PopOver createInfoPopOver(String info, double width) {
        Label helpLabel = new Label(info);
        helpLabel.setMaxWidth(width);
        helpLabel.setWrapText(true);
        helpLabel.setPadding(new Insets(10));
        return createInfoPopOver(helpLabel);
    }

    private PopOver createInfoPopOver(Node node) {
        node.getStyleClass().add("default-text");

        PopOver infoPopover = new PopOver(node);
        if (textIcon.getScene() != null) {
            infoPopover.setDetachable(false);
            infoPopover.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

            infoPopover.show(textIcon, -10);
        }
        return infoPopover;
    }
}
