package io.onemfive.desktop.views.personal.identities.add;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;

public class AddIdentityView extends ActivatableView implements TopicListener {

    public static final String IDENTITY_ADDED = "IDENTITY_ADDED";

    private GridPane pane;
    private int gridRow = 0;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void activate() {

    }

    @Override
    protected void deactivate() {
        super.deactivate();
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {

        }
        LOG.info("Model updated.");
    }
}
