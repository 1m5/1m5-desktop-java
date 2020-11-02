package io.onemfive.desktop.components;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

public class ExternalHyperlink extends HyperlinkWithIcon {

    public ExternalHyperlink(String text) {
        super(text, MaterialDesignIcon.LINK);
    }

    public ExternalHyperlink(String text, String style) {
        super(text, MaterialDesignIcon.LINK, style);
    }
}
