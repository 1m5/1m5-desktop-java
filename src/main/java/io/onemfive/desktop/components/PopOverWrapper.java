package io.onemfive.desktop.components;

import io.onemfive.desktop.components.controlsfx.control.PopOver;
import javafx.application.Platform;

import java.util.function.Supplier;

public class PopOverWrapper {

    private PopOver popover;
    private Supplier<PopOver> popoverSupplier;
    private boolean hidePopover;
    private PopOverState state = PopOverState.HIDDEN;

    enum PopOverState {
        HIDDEN, SHOWING, SHOWN, HIDING
    }

    public void showPopOver(Supplier<PopOver> popoverSupplier) {
        this.popoverSupplier = popoverSupplier;
        hidePopover = false;

        if (state == PopOverState.HIDDEN) {
            state = PopOverState.SHOWING;
            popover = popoverSupplier.get();

            Platform.runLater(() -> {
                state = PopOverState.SHOWN;
                if (hidePopover) {
                    hidePopOver();
                }
            });
        }
    }

    public void hidePopOver() {
        hidePopover = true;

        if (state == PopOverState.SHOWN) {
            state = PopOverState.HIDING;
            popover.hide();

            Platform.runLater(() -> {
                state = PopOverState.HIDDEN;
                if (!hidePopover) {
                    showPopOver(popoverSupplier);
                }
            });
        }
    }
}
