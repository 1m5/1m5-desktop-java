package io.onemfive.desktop.views.personal.contacts.details;

import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.contacts.BaseContactView;
import javafx.scene.layout.GridPane;
import ra.common.Envelope;

public class DetailsContactView extends BaseContactView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {

        }
        LOG.info("Model updated.");
    }
}
