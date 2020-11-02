package io.onemfive.desktop.util;

import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

public class TooltipUtil {

    public static void showTooltipIfTruncated(SkinBase skinBase, Labeled labeled) {
        for (Object node : skinBase.getChildren()) {
            if (node instanceof Text) {
                String displayedText = ((Text) node).getText();
                String untruncatedText = labeled.getText();
                if (displayedText.equals(untruncatedText)) {
                    if (labeled.getTooltip() != null) {
                        labeled.setTooltip(null);
                    }
                } else if (untruncatedText != null && !untruncatedText.trim().isEmpty()) {
                    final Tooltip tooltip = new Tooltip(untruncatedText);
                    tooltip.setStyle("-fx-text-fill: -bs-rd-tooltip-truncated;");
                    labeled.setTooltip(tooltip);
                }
            }
        }
    }
}
