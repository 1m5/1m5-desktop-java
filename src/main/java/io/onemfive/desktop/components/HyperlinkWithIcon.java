package io.onemfive.desktop.components;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import io.onemfive.desktop.util.FormBuilder;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class HyperlinkWithIcon extends Hyperlink {

    public HyperlinkWithIcon(String text) {
        this(text, AwesomeIcon.INFO_SIGN);
    }

    public HyperlinkWithIcon(String text, AwesomeIcon awesomeIcon) {
        super(text);

        Label icon = new Label();
        AwesomeDude.setIcon(icon, awesomeIcon);
        icon.setMinWidth(20);
        icon.setOpacity(0.7);
        icon.getStyleClass().addAll("hyperlink", "no-underline");
        setPadding(new Insets(0));
        icon.setPadding(new Insets(0));

        setIcon(icon);
    }

    public HyperlinkWithIcon(String text, GlyphIcons icon) {
        this(text, icon, null);
    }

    public HyperlinkWithIcon(String text, GlyphIcons icon, String style) {
        super(text);

        Text textIcon = FormBuilder.getIcon(icon);
        textIcon.setOpacity(0.7);
        textIcon.getStyleClass().addAll("hyperlink", "no-underline");

        if (style != null) {
            textIcon.getStyleClass().add(style);
            getStyleClass().add(style);
        }

        setPadding(new Insets(0));

        setIcon(textIcon);
    }

    public void hideIcon() {
        setGraphic(null);
    }

    private void setIcon(Node icon) {
        setGraphic(icon);

        setContentDisplay(ContentDisplay.RIGHT);
        setGraphicTextGap(7.0);
    }

    public void clear() {
        setText("");
        setGraphic(null);
    }
}
