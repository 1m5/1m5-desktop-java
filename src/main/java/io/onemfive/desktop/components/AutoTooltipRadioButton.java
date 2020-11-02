package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXRadioButton;
import javafx.scene.control.Skin;

import static io.onemfive.desktop.util.TooltipUtil.showTooltipIfTruncated;

public class AutoTooltipRadioButton extends JFXRadioButton {

    public AutoTooltipRadioButton() {
        super();
    }

    public AutoTooltipRadioButton(String text) {
        super(text);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipRadioButtonSkin(this);
    }

    private class AutoTooltipRadioButtonSkin extends JFXRadioButtonSkin {
        public AutoTooltipRadioButtonSkin(JFXRadioButton radioButton) {
            super(radioButton);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
