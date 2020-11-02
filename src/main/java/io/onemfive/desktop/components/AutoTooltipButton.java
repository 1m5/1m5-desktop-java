package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXButtonSkin;
import javafx.scene.Node;
import javafx.scene.control.Skin;

import static io.onemfive.desktop.util.TooltipUtil.showTooltipIfTruncated;

public class AutoTooltipButton extends JFXButton {

    public AutoTooltipButton() {
        super();
    }

    public AutoTooltipButton(String text) {
        super(text.toUpperCase());
    }

    public AutoTooltipButton(String text, Node graphic) {
        super(text.toUpperCase(), graphic);
    }

    public void updateText(String text) {
        setText(text.toUpperCase());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipButtonSkin(this);
    }

    private class AutoTooltipButtonSkin extends JFXButtonSkin {
        public AutoTooltipButtonSkin(JFXButton button) {
            super(button);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
