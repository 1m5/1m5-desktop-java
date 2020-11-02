package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXSpinner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class BusyAnimation extends JFXSpinner {

    private final BooleanProperty isRunningProperty = new SimpleBooleanProperty();

    public BusyAnimation() {
        this(true);
    }

    public BusyAnimation(boolean isRunning) {
        getStyleClass().add("busyanimation");
        isRunningProperty.set(isRunning);

        updateVisibility();
    }

    public void play() {
        isRunningProperty.set(true);

        setProgress(-1);
        updateVisibility();
    }

    public void stop() {
        isRunningProperty.set(false);
        setProgress(0);
        updateVisibility();
    }

    public boolean isRunning() {
        return isRunningProperty.get();
    }

    public BooleanProperty isRunningProperty() {
        return isRunningProperty;
    }

    private void updateVisibility() {
        setVisible(isRunning());
        setManaged(isRunning());
    }
}
