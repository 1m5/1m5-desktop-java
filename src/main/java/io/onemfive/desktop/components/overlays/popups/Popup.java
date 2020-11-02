package io.onemfive.desktop.components.overlays.popups;

import io.onemfive.desktop.components.overlays.Overlay;

import java.util.logging.Logger;

public class Popup extends Overlay<Popup> {

    protected final Logger LOG = Logger.getLogger(Popup.class.getName());

    public Popup() {
    }

    public void onReadyForDisplay() {
        super.display();
    }

    @Override
    protected void onShow() {
        PopupManager.queueForDisplay(this);
    }

    @Override
    protected void onHidden() {
        PopupManager.onHidden(this);
    }
}
