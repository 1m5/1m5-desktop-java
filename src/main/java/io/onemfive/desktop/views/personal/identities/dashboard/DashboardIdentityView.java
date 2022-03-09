package io.onemfive.desktop.views.personal.identities.dashboard;

import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.personal.identities.BaseIdentityView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;

public class DashboardIdentityView extends BaseIdentityView implements TopicListener {

    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");

    private ComboBox<String> authNAliasComboBox;
    private PasswordTextField authNPwdText;
    private Button authN;

    @Override
    protected void initialize() {
        super.initialize();
        // Authenticate Identity
        authNAliasComboBox = new ComboBox<>();
        authNAliasComboBox.setItems(identityAddresses);
        authNAliasComboBox.getStyleClass().add("jfx-combo-box");
        authNAliasComboBox.setValue("Anonymous");
        authNPwdText = new PasswordTextField();
        authNPwdText.setVisible(false);
        authN = new AutoTooltipButton(authNText);
        authN.setDefaultButton(true);
        authN.getStyleClass().add("action-button");
        authN.setVisible(false);
        pane.add(new VBox(Layout.GRID_GAP, authNAliasComboBox, authNPwdText, authN), 0, gridRow);
        LOG.info("Initialized.");
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
