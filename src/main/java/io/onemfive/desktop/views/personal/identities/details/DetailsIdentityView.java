package io.onemfive.desktop.views.personal.identities.details;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;

public class DetailsIdentityView extends ActivatableView implements TopicListener {

    public static final String ACTIVE_IDENTITY = "ACTIVE_IDENTITY";

    private GridPane pane;
    private int gridRow = 0;

    private Button editIdentity;

    @Override
    protected void initialize() {
        super.initialize();
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
