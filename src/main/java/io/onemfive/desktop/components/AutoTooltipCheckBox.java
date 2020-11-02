package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.skins.JFXCheckBoxSkin;
import javafx.scene.control.Skin;

import static io.onemfive.desktop.util.TooltipUtil.showTooltipIfTruncated;

public class AutoTooltipCheckBox extends JFXCheckBox {

    public AutoTooltipCheckBox() {
        super();
    }

    public AutoTooltipCheckBox(String text) {
        super(text);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipCheckBoxSkin(this);
    }

    private class AutoTooltipCheckBoxSkin extends JFXCheckBoxSkin {
        public AutoTooltipCheckBoxSkin(JFXCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
