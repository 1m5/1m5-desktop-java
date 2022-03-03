package io.onemfive.desktop.views.personal.identities.details;

import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.identities.BaseIdentityView;
import ra.common.Envelope;

public class DetailsIdentityView extends BaseIdentityView implements TopicListener {

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {

        }
        LOG.info("Model updated.");
    }
}
