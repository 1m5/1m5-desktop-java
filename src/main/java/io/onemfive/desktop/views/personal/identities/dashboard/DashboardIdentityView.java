package io.onemfive.desktop.views.personal.identities.dashboard;

import io.onemfive.desktop.DesktopClient;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.PasswordTextField;
import io.onemfive.desktop.components.TitledGroupBg;
import io.onemfive.desktop.util.Layout;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.TopicListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ra.common.Envelope;
import ra.common.Resources;
import ra.common.identity.DID;
import ra.common.network.ControlCommand;
import ra.did.AuthenticateDIDRequest;
import ra.did.DIDService;

import static io.onemfive.desktop.util.FormBuilder.*;
import static java.util.Objects.nonNull;

public class DashboardIdentityView extends ActivatableView implements TopicListener {

    private static final String AUTHN_RESULT = "AUTHN_RESULT";

    private final String authNText = Resources.get("personalIdentitiesView.authn");
    private final String aliasPrompt = Resources.get("shared.alias");
    private final String pwdPrompt = Resources.get("shared.passphrase");

    private GridPane pane;
    private int gridRow = 0;

    private ComboBox<String> authNAliasComboBox;
    private PasswordTextField authNPwdText;
    private Button authNButton;
    private Label errorLabel;
    private String currentUsername = null;

    private ObservableList<String> identityAddresses = FXCollections.observableArrayList();

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        pane = (GridPane)root;
        TitledGroupBg createWalletGroup = addTitledGroupBg(pane, ++gridRow, 4, Resources.get("personalIdentitiesView.authn"), Layout.FIRST_ROW_DISTANCE);
        GridPane.setColumnSpan(createWalletGroup, 1);
        // Authenticate Identity
        authNAliasComboBox = addComboBox(pane, gridRow, Resources.get("personalIdentitiesView.username"));
        authNAliasComboBox.setItems(identityAddresses);
        authNAliasComboBox.getStyleClass().add("jfx-combo-box");
        authNAliasComboBox.setPromptText("Select username");
        authNPwdText = new PasswordTextField();
        authNPwdText.setVisible(false);
        authNButton = new AutoTooltipButton(authNText);
        authNButton.setDefaultButton(true);
        authNButton.getStyleClass().add("action-button");
        authNButton.setVisible(false);
        errorLabel = addLabel(pane, ++gridRow, "");
        errorLabel.setVisible(false);
        pane.add(new VBox(Layout.GRID_GAP, authNAliasComboBox, authNPwdText, authNButton), 0, gridRow);
        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        if(nonNull(DesktopClient.getCache().getActivePersonalDID())) {
            currentUsername = DesktopClient.getCache().getActivePersonalDID().getUsername();
            authNAliasComboBox.setValue(currentUsername);
        }
        authNAliasComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(authNAliasComboBox.getValue()==null) {

                }

            }
        });
        authNButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                errorLabel.setText(null);
                errorLabel.setVisible(false);

//                authenticate(username, passphrase);
            }
        });
    }

    @Override
    protected void deactivate() {
        authNAliasComboBox.setOnAction(null);
        authNButton.setOnAction(null);
    }

    @Override
    public void modelUpdated(String topic, Object object) {
        LOG.info("Updating model with topic: "+topic);
        Envelope e = (Envelope)object;
        switch (topic) {
            case AUTHN_RESULT: {
                if(e.getValue("authN")!=null) {
                    // AuthN failed
                    errorLabel.setText("Authentication failed.");
                    errorLabel.setVisible(true);
                } else {
                    DID did = (DID)e.getData(DID.class);
                    DesktopClient.getCache().setActivePersonalDID(did);
                    currentUsername = did.getUsername();
                    authNPwdText.setText(null);
                    authNPwdText.setVisible(false);
                    authNButton.setVisible(false);
                    errorLabel.setText(null);
                    errorLabel.setVisible(false);
                }
                break;
            }
        }
        LOG.info("Model updated.");
    }

    private void authenticate(String username, String passphrase) {
        AuthenticateDIDRequest request = new AuthenticateDIDRequest();
        request.username = username;
        request.passphrase = passphrase;
        request.didType = DID.DIDType.IDENTITY;
        request.external = false;
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.Send.name());
        e.addData(AuthenticateDIDRequest.class, request);
        e.addRoute(DIDService.class, DIDService.OPERATION_AUTHENTICATE);
    }
}
