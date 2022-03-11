package io.onemfive.desktop.views.personal.identities.dashboard;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;

import static io.onemfive.desktop.views.personal.identities.details.DetailsIdentityView.ACTIVE_IDENTITY;
import static java.util.Objects.nonNull;

public class DashboardIdentityView extends ActivatableView implements TopicListener {

    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");

    private GridPane pane;
    private int gridRow = 0;

    private ComboBox<String> authNAliasComboBox;
    private PasswordTextField authNPwdText;
    private Button authN;

    private ObservableList<String> identityAddresses = FXCollections.observableArrayList();

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
    protected void activate() {
        if(nonNull(DesktopClient.getCache().getActivePersonalDID())) {
            authNAliasComboBox.setValue(DesktopClient.getCache().getActivePersonalDID().getUsername());
        }
        authNAliasComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean isAnonymous = "Anonymous".equals(authNAliasComboBox.getValue());
                authNPwdText.setVisible(!isAnonymous);
                authN.setVisible(!isAnonymous);
            }
        });
        authN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                if(!authNAliasTxt.getText().isEmpty() && !authNPwdText.getText().isEmpty()) {
//                    Envelope e = Envelope.documentFactory();
//                    // 3. Update UI
//                    e.addRoute(DesktopBusClient.class, DesktopBusClient.OPERATION_NOTIFY_UI);
//                    // 2. Load ordered Contacts
//                    e.addRoute(DIDService.class, DIDService.OPERATION_GET_CONTACTS);
//                    // 1. Authenticate
//                    DID did = new DID();
//                    did.setUsername(authNAliasTxt.getText());
//                    did.setPassphrase(authNPwdText.getText());
//                    did.setPassphrase2(authNPwdText.getText());
//                    AuthNRequest ar = new AuthNRequest();
//                    ar.keyRingUsername = did.getUsername();
//                    ar.keyRingPassphrase = did.getPassphrase();
//                    ar.alias = did.getUsername(); // use username as default alias
//                    ar.aliasPassphrase = did.getPassphrase(); // just use same passphrase
//                    ar.autoGenerate = true;
//                    e.setDID(did);
//                    e.addData(AuthNRequest.class, ar);
//                    e.addRoute(KeyRingService.class, KeyRingService.OPERATION_AUTHN);
//                    // Send
//                    DesktopBusClient.deliver(e);
//                }
            }
        });
    }

    @Override
    protected void deactivate() {
        authNAliasComboBox.setOnAction(null);
        authN.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case ACTIVE_IDENTITY: {
                DID did = (DID)e.getValue("activeIdentity");
                if(nonNull(did)) {
                    DesktopClient.getCache().setActivePersonalDID(did);
                }
                break;
            }        }
        LOG.info("Model updated.");
    }
}
