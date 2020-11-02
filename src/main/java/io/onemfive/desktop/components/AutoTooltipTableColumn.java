package io.onemfive.desktop.components;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import io.onemfive.desktop.components.controlsfx.control.PopOver;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class AutoTooltipTableColumn<S, T> extends TableColumn<S, T> {

    private Label helpIcon;
    private PopOverWrapper popoverWrapper = new PopOverWrapper();

    public AutoTooltipTableColumn(String text) {
        super();

        setTitle(text);
    }

    public AutoTooltipTableColumn(String text, String help) {

        setTitleWithHelpText(text, help);
    }

    public void setTitle(String title) {
        setGraphic(new AutoTooltipLabel(title));
    }

    public void setTitleWithHelpText(String title, String help) {
        helpIcon = new Label();
        AwesomeDude.setIcon(helpIcon, AwesomeIcon.QUESTION_SIGN, "1em");
        helpIcon.setOpacity(0.4);
        helpIcon.setOnMouseEntered(e -> popoverWrapper.showPopOver(() -> createInfoPopOver(help)));
        helpIcon.setOnMouseExited(e -> popoverWrapper.hidePopOver());

        final AutoTooltipLabel label = new AutoTooltipLabel(title);
        final HBox hBox = new HBox(label, helpIcon);
        hBox.setStyle("-fx-alignment: center-left");
        hBox.setSpacing(4);
        setGraphic(hBox);
    }

    private PopOver createInfoPopOver(String help) {
        Label helpLabel = new Label(help);
        helpLabel.setMaxWidth(300);
        helpLabel.setWrapText(true);
        return createInfoPopOver(helpLabel);
    }

    private PopOver createInfoPopOver(Node node) {
        node.getStyleClass().add("default-text");

        PopOver infoPopover = new PopOver(node);
        if (helpIcon.getScene() != null) {
            infoPopover.setDetachable(false);
            infoPopover.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

            infoPopover.show(helpIcon, -10);
        }
        return infoPopover;
    }
}
