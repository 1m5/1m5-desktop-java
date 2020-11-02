package io.onemfive.desktop.views;

public abstract class ActivatableView extends InitializableView {

    public ActivatableView() {}

    @Override
    protected void prepareInitialize() {
        if (root != null) {
            root.sceneProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue == null && newValue != null)
                    activate();
                else if (oldValue != null && newValue == null)
                    deactivate();
            });
        }
    }

    protected void activate() {
    }

    protected void deactivate() {
    }
}

