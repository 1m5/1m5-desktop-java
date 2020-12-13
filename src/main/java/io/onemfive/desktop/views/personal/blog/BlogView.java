package io.onemfive.desktop.views.personal.blog;

import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ra.util.Resources;

import static io.onemfive.desktop.util.FormBuilder.addMultilineLabel;
import static io.onemfive.desktop.util.FormBuilder.addTitledGroupBg;

public class BlogView extends ActivatableView implements TopicListener {

    private GridPane pane;
    private int gridRow = 0;

    private Label notes;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;

        TitledGroupBg statusGroup = addTitledGroupBg(pane, gridRow, 1, Resources.get("shared.notes"));
        GridPane.setColumnSpan(statusGroup, 1);

        String note = "Personal Blog is on the roadmap. Multiple blogs will be supported backed by a local DID or none for anonymous blogs.";
        notes = addMultilineLabel(pane, ++gridRow, note, Layout.FIRST_ROW_DISTANCE);

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        LOG.info("Activating...");

        LOG.info("Activated.");
    }

    @Override
    protected void deactivate() {
        LOG.info("Deactivating...");

        LOG.info("Deactivated.");
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model...");

        LOG.info("Model updated.");
    }

}
