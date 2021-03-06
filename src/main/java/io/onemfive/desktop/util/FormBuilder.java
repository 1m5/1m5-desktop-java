package io.onemfive.desktop.util;

import com.jfoenix.controls.*;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import ra.common.Tuple2;
import ra.common.Tuple3;
import ra.common.Tuple4;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.components.*;
import io.onemfive.desktop.views.ViewPath;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.commons.browser.BrowserView;
import io.onemfive.desktop.views.home.HomeView;
import ra.util.Resources;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.onemfive.desktop.util.GUIUtil.getComboBoxButtonCell;

public class FormBuilder {

    private static final String MATERIAL_DESIGN_ICONS = "'Material Design Icons'";

    public static TitledGroupBg addTitledGroupBg(GridPane gridPane, int rowIndex, int rowSpan, String title) {
        return addTitledGroupBg(gridPane, rowIndex, rowSpan, title, 0);
    }

    public static TitledGroupBg addTitledGroupBg(GridPane gridPane, int rowIndex, int columnIndex, int rowSpan, String title) {
        TitledGroupBg titledGroupBg = addTitledGroupBg(gridPane, rowIndex, rowSpan, title, 0);
        GridPane.setColumnIndex(titledGroupBg, columnIndex);
        return titledGroupBg;
    }

    public static TitledGroupBg addTitledGroupBg(GridPane gridPane, int rowIndex, int columnIndex, int rowSpan, String title, double top) {
        TitledGroupBg titledGroupBg = addTitledGroupBg(gridPane, rowIndex, rowSpan, title, top);
        GridPane.setColumnIndex(titledGroupBg, columnIndex);
        return titledGroupBg;
    }

    public static TitledGroupBg addTitledGroupBg(GridPane gridPane, int rowIndex, int rowSpan, String title, double top) {
        TitledGroupBg titledGroupBg = new TitledGroupBg();
        titledGroupBg.setText(title);
        titledGroupBg.prefWidthProperty().bind(gridPane.widthProperty());
        GridPane.setRowIndex(titledGroupBg, rowIndex);
        GridPane.setRowSpan(titledGroupBg, rowSpan);
        GridPane.setMargin(titledGroupBg, new Insets(top + 8, -10, -12, -10));
        gridPane.getChildren().add(titledGroupBg);
        return titledGroupBg;
    }

    public static Label addLabel(GridPane gridPane, int rowIndex, String title) {
        return addLabel(gridPane, rowIndex, title, 0);
    }

    public static Label addLabel(GridPane gridPane, int colIndex, int rowIndex, String title) {
        Label label = new AutoTooltipLabel(title);
        GridPane.setColumnIndex(label, colIndex);
        GridPane.setRowIndex(label, rowIndex);
        GridPane.setMargin(label, new Insets(0, 0, 0, 0));
        gridPane.getChildren().add(label);
        return label;
    }

    public static Label addLabel(GridPane gridPane, int rowIndex, String title, double top) {
        Label label = new AutoTooltipLabel(title);
        GridPane.setRowIndex(label, rowIndex);
        GridPane.setMargin(label, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(label);
        return label;
    }

    public static Label addLabel(GridPane gridPane, int colIndex, int rowIndex, String title, double top) {
        Label label = new AutoTooltipLabel(title);
        GridPane.setColumnIndex(label, colIndex);
        GridPane.setRowIndex(label, rowIndex);
        GridPane.setMargin(label, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(label);
        return label;
    }

    public static Tuple3<Label, Label, VBox> addLabelWithSubText(GridPane gridPane, int rowIndex, String title, String description) {
        return addLabelWithSubText(gridPane, rowIndex, title, description, 0);
    }

    public static Tuple3<Label, Label, VBox> addLabelWithSubText(GridPane gridPane, int rowIndex, String title, String description, double top) {
        Label label = new AutoTooltipLabel(title);
        Label subText = new AutoTooltipLabel(description);

        VBox vBox = new VBox();
        vBox.getChildren().setAll(label, subText);

        GridPane.setRowIndex(vBox, rowIndex);
        GridPane.setMargin(vBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(vBox);

        return new Tuple3<>(label, subText, vBox);
    }

    public static Label addMultilineLabel(GridPane gridPane, int rowIndex) {
        return addMultilineLabel(gridPane, rowIndex, 0);
    }

    public static Label addMultilineLabel(GridPane gridPane, int rowIndex, String text) {
        return addMultilineLabel(gridPane, rowIndex, text, 0);
    }

    public static Label addMultilineLabel(GridPane gridPane, int rowIndex, double top) {
        return addMultilineLabel(gridPane, rowIndex, "", top);
    }

    public static Label addMultilineLabel(GridPane gridPane, int rowIndex, String text, double top) {
        return addMultilineLabel(gridPane, rowIndex, text, top, 600);
    }

    public static Label addMultilineLabel(GridPane gridPane, int rowIndex, String text, double top, double maxWidth) {
        Label label = new AutoTooltipLabel(text);
        label.setWrapText(true);
        label.setMaxWidth(maxWidth);
        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setRowIndex(label, rowIndex);
        GridPane.setMargin(label, new Insets(top + Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(label);
        return label;
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, String title) {
        return addTopLabelTextField(gridPane, rowIndex, title, "", -15);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, int columnIndex, String title) {
        Tuple3<Label, TextField, VBox> tuple = addTopLabelTextField(gridPane, rowIndex, title, "", -15);
        GridPane.setColumnIndex(tuple.third, columnIndex);
        return tuple;
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, String title, double top) {
        return addTopLabelTextField(gridPane, rowIndex, title, "", top - 15);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, String title, String value) {
        return addTopLabelReadOnlyTextField(gridPane, rowIndex, title, value, 0);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, int columnIndex, String title, String value, double top) {
        Tuple3<Label, TextField, VBox> tuple = addTopLabelTextField(gridPane, rowIndex, title, value, top - 15);
        GridPane.setColumnIndex(tuple.third, columnIndex);
        return tuple;
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, int columnIndex, String title, double top) {
        Tuple3<Label, TextField, VBox> tuple = addTopLabelTextField(gridPane, rowIndex, title, "", top - 15);
        GridPane.setColumnIndex(tuple.third, columnIndex);
        return tuple;
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelReadOnlyTextField(GridPane gridPane, int rowIndex, String title, String value, double top) {
        return addTopLabelTextField(gridPane, rowIndex, title, value, top - 15);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelTextField(GridPane gridPane, int rowIndex, String title) {
        return addTopLabelTextField(gridPane, rowIndex, title, "", 0);
    }

    public static Tuple3<Label, TextField, VBox> addCompactTopLabelTextField(GridPane gridPane, int rowIndex, String title, String value) {
        return addTopLabelTextField(gridPane, rowIndex, title, value, -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple3<Label, TextField, VBox> addCompactTopLabelTextField(GridPane gridPane, int colIndex, int rowIndex, String title, String value, double top) {
        return addTopLabelTextField(gridPane, colIndex, rowIndex, title, value, top);
    }

    public static Tuple3<Label, TextField, VBox> addCompactTopLabelTextField(GridPane gridPane, int rowIndex, int colIndex, String title, String value) {
        final Tuple3<Label, TextField, VBox> labelTextFieldVBoxTuple3 = addTopLabelTextField(gridPane, rowIndex, title, value, -Layout.FLOATING_LABEL_DISTANCE);
        GridPane.setColumnIndex(labelTextFieldVBoxTuple3.third, colIndex);
        return labelTextFieldVBoxTuple3;
    }

    public static Tuple3<Label, TextField, VBox> addCompactTopLabelTextField(GridPane gridPane, int rowIndex, String title, String value, double top) {
        return addTopLabelTextField(gridPane, rowIndex, title, value, top - Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelTextField(GridPane gridPane, int rowIndex, String title, String value) {
        return addTopLabelTextField(gridPane, rowIndex, title, value, 0);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelTextField(GridPane gridPane, int rowIndex, String title, double top) {
        return addTopLabelTextField(gridPane, rowIndex, title, "", top);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelTextField(GridPane gridPane, int rowIndex, int columnIndex, String title, String value, double top) {
        TextField textField = new JFXTextField(value);
        textField.setEditable(false);
        textField.setFocusTraversable(false);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, columnIndex, rowIndex, title, textField, top);

        // TOD not 100% sure if that is a good idea....
        //topLabelWithVBox.first.getStyleClass().add("jfx-text-field-top-label");

        return new Tuple3<>(topLabelWithVBox.first, textField, topLabelWithVBox.second);
    }

    public static Tuple3<Label, TextField, VBox> addTopLabelTextField(GridPane gridPane, int rowIndex, String title, String value, double top) {
        TextField textField = new JFXTextField(value);
        textField.setEditable(false);
        textField.setFocusTraversable(false);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, textField, top);

        // TOD not 100% sure if that is a good idea....
        //topLabelWithVBox.first.getStyleClass().add("jfx-text-field-top-label");

        return new Tuple3<>(topLabelWithVBox.first, textField, topLabelWithVBox.second);
    }

    public static Tuple2<Label, Label> addConfirmationLabelLabel(GridPane gridPane, int rowIndex, String title1, String title2) {
        return addConfirmationLabelLabel(gridPane, rowIndex, title1, title2, 0);
    }

    public static Tuple2<Label, Label> addConfirmationLabelLabel(GridPane gridPane, int rowIndex, String title1, String title2, double top) {
        Label label1 = addLabel(gridPane, rowIndex, title1);
        label1.getStyleClass().add("confirmation-label");
        Label label2 = addLabel(gridPane, rowIndex, title2);
        label2.getStyleClass().add("confirmation-value");
        GridPane.setColumnIndex(label2, 1);
        GridPane.setMargin(label1, new Insets(top, 0, 0, 0));
        GridPane.setHalignment(label1, HPos.LEFT);
        GridPane.setMargin(label2, new Insets(top, 0, 0, 0));

        return new Tuple2<>(label1, label2);
    }

    public static Tuple2<Label, TextArea> addConfirmationLabelTextArea(GridPane gridPane, int rowIndex, String title1, String title2, double top) {
        Label label = addLabel(gridPane, rowIndex, title1);
        label.getStyleClass().add("confirmation-label");

        TextArea textArea = addTextArea(gridPane, rowIndex, title2);
        ((JFXTextArea) textArea).setLabelFloat(false);

        GridPane.setColumnIndex(textArea, 1);
        GridPane.setMargin(label, new Insets(top, 0, 0, 0));
        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setMargin(textArea, new Insets(top, 0, 0, 0));

        return new Tuple2<>(label, textArea);
    }

    public static Tuple2<Label, TextFieldWithIcon> addTopLabelTextFieldWithIcon(GridPane gridPane, int rowIndex, String title, double top) {
        return addTopLabelTextFieldWithIcon(gridPane, rowIndex, 0, title, top);
    }

    public static Tuple2<Label, TextFieldWithIcon> addTopLabelTextFieldWithIcon(GridPane gridPane, int rowIndex, int columnIndex, String title, double top) {

        TextFieldWithIcon textFieldWithIcon = new TextFieldWithIcon();
        textFieldWithIcon.setMouseTransparent(true);
        textFieldWithIcon.setFocusTraversable(false);

        return new Tuple2<>(addTopLabelWithVBox(gridPane, rowIndex, columnIndex, title, textFieldWithIcon, top).first, textFieldWithIcon);
    }

    public static HyperlinkWithIcon addHyperlinkWithIcon(GridPane gridPane, int rowIndex, String title, String url) {
        return addHyperlinkWithIcon(gridPane, rowIndex, title, url, 0);
    }

    public static HyperlinkWithIcon addHyperlinkWithIcon(GridPane gridPane, int rowIndex, String title, String url, double top) {
        HyperlinkWithIcon hyperlinkWithIcon = new ExternalHyperlink(title);
        hyperlinkWithIcon.setOnAction(e -> MVC.navigation.navigateTo(ViewPath.to(HomeView.class, CommonsView.class, BrowserView.class), url));
        GridPane.setRowIndex(hyperlinkWithIcon, rowIndex);
        GridPane.setColumnIndex(hyperlinkWithIcon, 0);
        GridPane.setMargin(hyperlinkWithIcon, new Insets(top, 0, 0, 0));
        GridPane.setHalignment(hyperlinkWithIcon, HPos.LEFT);
        gridPane.getChildren().add(hyperlinkWithIcon);
        return hyperlinkWithIcon;
    }

    public static Tuple2<Label, HyperlinkWithIcon> addLabelHyperlinkWithIcon(GridPane gridPane, int rowIndex, String labelTitle, String title, String url) {
        return addLabelHyperlinkWithIcon(gridPane, rowIndex, labelTitle, title, url, 0);
    }

    public static Tuple2<Label, HyperlinkWithIcon> addLabelHyperlinkWithIcon(GridPane gridPane, int rowIndex, String labelTitle, String title, String url, double top) {
        Label label = addLabel(gridPane, rowIndex, labelTitle, top);

        HyperlinkWithIcon hyperlinkWithIcon = new ExternalHyperlink(title);
        hyperlinkWithIcon.setOnAction(e -> MVC.navigation.navigateTo(ViewPath.to(HomeView.class, CommonsView.class, BrowserView.class), url));
        GridPane.setRowIndex(hyperlinkWithIcon, rowIndex);
        GridPane.setMargin(hyperlinkWithIcon, new Insets(top, 0, 0, -4));
        gridPane.getChildren().add(hyperlinkWithIcon);
        return new Tuple2<>(label, hyperlinkWithIcon);
    }

    public static Tuple3<Label, HyperlinkWithIcon, VBox> addTopLabelHyperlinkWithIcon(GridPane gridPane, int rowIndex, int columnIndex, String title, String value, String url, double top) {
        Tuple3<Label, HyperlinkWithIcon, VBox> tuple = addTopLabelHyperlinkWithIcon(gridPane,
                rowIndex,
                title,
                value,
                url,
                top);
        GridPane.setColumnIndex(tuple.third, columnIndex);
        return tuple;
    }

    public static Tuple3<Label, HyperlinkWithIcon, VBox> addTopLabelHyperlinkWithIcon(GridPane gridPane, int rowIndex, String title, String value, String url, double top) {
        HyperlinkWithIcon hyperlinkWithIcon = new ExternalHyperlink(value);
        hyperlinkWithIcon.setOnAction(e -> MVC.navigation.navigateTo(ViewPath.to(HomeView.class, CommonsView.class, BrowserView.class), url));
        hyperlinkWithIcon.getStyleClass().add("hyperlink-with-icon");
        GridPane.setRowIndex(hyperlinkWithIcon, rowIndex);
        Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, hyperlinkWithIcon, top - 15);
        return new Tuple3<>(topLabelWithVBox.first, hyperlinkWithIcon, topLabelWithVBox.second);
    }

    public static TextArea addTextArea(GridPane gridPane, int rowIndex, String prompt) {
        return addTextArea(gridPane, rowIndex, prompt, 0);
    }

    public static TextArea addTextArea(GridPane gridPane, int rowIndex, String prompt, double top) {

        JFXTextArea textArea = new JFXTextArea();
        textArea.setPromptText(prompt);
        textArea.setLabelFloat(true);
        textArea.setWrapText(true);

        GridPane.setRowIndex(textArea, rowIndex);
        GridPane.setColumnIndex(textArea, 0);
        GridPane.setMargin(textArea, new Insets(top + Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(textArea);

        return textArea;
    }

    public static Tuple2<Label, TextArea> addCompactTopLabelTextArea(GridPane gridPane, int rowIndex, String title, String prompt) {
        return addTopLabelTextArea(gridPane, rowIndex, title, prompt, -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextArea> addCompactTopLabelTextArea(GridPane gridPane, int rowIndex, int colIndex, String title, String prompt) {
        return addTopLabelTextArea(gridPane, rowIndex, colIndex, title, prompt, -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextArea> addCompactTopLabelTextAreaWithText(GridPane gridPane, String text, int rowIndex, String title, boolean readOnly) {
        return addTopLabelTextAreaWithText(gridPane, rowIndex, title, -Layout.FLOATING_LABEL_DISTANCE, text, readOnly);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextArea(GridPane gridPane, int rowIndex, String title, String prompt) {
        return addTopLabelTextArea(gridPane, rowIndex, title, prompt, 0);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextArea(GridPane gridPane, int rowIndex, int colIndex, String title, String prompt) {
        return addTopLabelTextArea(gridPane, rowIndex, colIndex, title, prompt, 0);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextAreaWithText(GridPane gridPane, String text, int rowIndex, int colIndex, String title, boolean readOnly) {
        return addTopLabelTextAreaWithText(gridPane, rowIndex, colIndex, title, 0, text, readOnly);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextArea(GridPane gridPane, int rowIndex, String title, String prompt, double top) {
        return addTopLabelTextArea(gridPane, rowIndex, 0, title, prompt, top);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextAreaWithText(GridPane gridPane,
                                                              int rowIndex,
                                                              String title,
                                                              double top,
                                                              String text,
                                                              boolean readOnly) {

        return addTopLabelTextAreaWithText(gridPane, rowIndex, 0, title, top, text, readOnly);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextArea(GridPane gridPane, int rowIndex, int colIndex,
                                                              String title, String prompt, double top) {

        JFXTextArea textArea = new JFXTextArea();
        textArea.setPromptText(prompt);
        textArea.setWrapText(true);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, textArea, top);
        GridPane.setColumnIndex(topLabelWithVBox.second, colIndex);

        return new Tuple2<>(topLabelWithVBox.first, textArea);
    }

    public static Tuple2<Label, TextArea> addTopLabelTextAreaWithText(GridPane gridPane, int rowIndex, int colIndex,
                                                              String title, double top, String text, boolean readOnly) {

        JFXTextArea textArea = new JFXTextArea();
        textArea.setText(text);
        textArea.setWrapText(true);
        textArea.setEditable(!readOnly);
        if(readOnly) {
            textArea.getStyleClass().add("jfx-text-area:readonly");
        }

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, textArea, top);
        GridPane.setColumnIndex(topLabelWithVBox.second, colIndex);

        return new Tuple2<>(topLabelWithVBox.first, textArea);
    }

    public static Tuple2<Label, JFXDatePicker> addTopLabelDatePicker(GridPane gridPane,
                                                                  int rowIndex,
                                                                  String title,
                                                                  double top) {
        JFXDatePicker datePicker = new JFXDatePicker();

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, datePicker, top);

        return new Tuple2<>(topLabelWithVBox.first, datePicker);
    }

    public static InputTextField addInputTextField(GridPane gridPane, int rowIndex, String title) {
        return addInputTextField(gridPane, rowIndex, title, 0);
    }

    public static InputTextField addInputTextField(GridPane gridPane, int rowIndex, String title, double top) {
        return addInputTextField(gridPane, rowIndex, 0, title, top);
    }

    public static InputTextField addInputTextField(GridPane gridPane, int rowIndex, int columnIndex, String title, double top) {
        InputTextField inputTextField = new InputTextField();
        inputTextField.setLabelFloat(true);
        inputTextField.setPromptText(title);
        GridPane.setRowIndex(inputTextField, rowIndex);
        GridPane.setColumnIndex(inputTextField, columnIndex);
        GridPane.setMargin(inputTextField, new Insets(top + Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(inputTextField);

        return inputTextField;
    }

    public static Tuple2<Label, InputTextField> addTopLabelInputTextField(GridPane gridPane, int rowIndex, String title) {
        return addTopLabelInputTextField(gridPane, rowIndex, title, 0);
    }

    public static Tuple2<Label, InputTextField> addTopLabelInputTextField(GridPane gridPane, int rowIndex, String title, double top) {

        final Tuple3<Label, InputTextField, VBox> topLabelWithVBox = addTopLabelInputTextFieldWithVBox(gridPane, rowIndex, title, top);

        return new Tuple2<>(topLabelWithVBox.first, topLabelWithVBox.second);
    }

    public static Tuple3<Label, InputTextField, VBox> addTopLabelInputTextFieldWithVBox(GridPane gridPane, int rowIndex, String title, double top) {

        InputTextField inputTextField = new InputTextField();

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, inputTextField, top);

        return new Tuple3<>(topLabelWithVBox.first, inputTextField, topLabelWithVBox.second);
    }

    public static Tuple2<Label, InfoInputTextField> addTopLabelInfoInputTextField(GridPane gridPane, int rowIndex, String title) {
        return addTopLabelInfoInputTextField(gridPane, rowIndex, title, 0);
    }

    public static Tuple2<Label, InfoInputTextField> addTopLabelInfoInputTextField(GridPane gridPane, int rowIndex, String title, double top) {

        InfoInputTextField inputTextField = new InfoInputTextField();

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, inputTextField, top);

        return new Tuple2<>(topLabelWithVBox.first, inputTextField);
    }


    public static PasswordTextField addPasswordTextField(GridPane gridPane, int rowIndex, String title) {
        return addPasswordTextField(gridPane, rowIndex, title, 0);
    }

    public static PasswordTextField addPasswordTextField(GridPane gridPane, int rowIndex, String title, double top) {
        PasswordTextField passwordField = new PasswordTextField();
        passwordField.setPromptText(title);
        GridPane.setRowIndex(passwordField, rowIndex);
        GridPane.setColumnIndex(passwordField, 0);
        GridPane.setColumnSpan(passwordField, 2);
        GridPane.setMargin(passwordField, new Insets(top + 10, 0, 20, 0));
        gridPane.getChildren().add(passwordField);

        return passwordField;
    }

    public static PasswordTextField addPasswordTextField(GridPane gridPane, int rowIndex, int colIndex, String title, double top) {
        PasswordTextField passwordField = new PasswordTextField();
        passwordField.setPromptText(title);
        GridPane.setRowIndex(passwordField, rowIndex);
        GridPane.setColumnIndex(passwordField, colIndex);
        GridPane.setColumnSpan(passwordField, 2);
        GridPane.setMargin(passwordField, new Insets(top + 10, 0, 20, 0));
        gridPane.getChildren().add(passwordField);

        return passwordField;
    }

    public static Tuple3<Label, InputTextField, ToggleButton> addTopLabelInputTextFieldSlideToggleButton(GridPane gridPane,
                                                                                                         int rowIndex,
                                                                                                         String title,
                                                                                                         String toggleButtonTitle) {

        InputTextField inputTextField = new InputTextField();
        ToggleButton toggleButton = new JFXToggleButton();
        toggleButton.setText(toggleButtonTitle);
        VBox.setMargin(toggleButton, new Insets(4, 0, 0, 0));

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, inputTextField, 0);

        topLabelWithVBox.second.getChildren().add(toggleButton);

        return new Tuple3<>(topLabelWithVBox.first, inputTextField, toggleButton);
    }

    public static Tuple3<Label, InputTextField, Button> addTopLabelInputTextFieldButton(GridPane gridPane,
                                                                                        int rowIndex,
                                                                                        String title,
                                                                                        String buttonTitle) {
        InputTextField inputTextField = new InputTextField();
        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(inputTextField, button);
        HBox.setHgrow(inputTextField, Priority.ALWAYS);

        final Tuple2<Label, VBox> labelVBoxTuple2 = addTopLabelWithVBox(gridPane, rowIndex, title, hBox, 0);

        return new Tuple3<>(labelVBoxTuple2.first, inputTextField, button);
    }

    public static Tuple3<Label, TextField, Button> addTopLabelTextFieldButton(GridPane gridPane,
                                                                              int rowIndex,
                                                                              String title,
                                                                              String buttonTitle) {
        return addTopLabelTextFieldButton(gridPane, rowIndex, title, buttonTitle, 0);
    }

    public static Tuple3<Label, TextField, Button> addTopLabelTextFieldButton(GridPane gridPane,
                                                                              int rowIndex,
                                                                              String title,
                                                                              String buttonTitle,
                                                                              double top) {

        JFXTextField textField = new JFXTextField();
        textField.setEditable(false);
        textField.setMouseTransparent(true);
        textField.setFocusTraversable(false);
        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(textField, button);
        HBox.setHgrow(textField, Priority.ALWAYS);

        final Tuple2<Label, VBox> labelVBoxTuple2 = addTopLabelWithVBox(gridPane, rowIndex, title, hBox, top);

        return new Tuple3<>(labelVBoxTuple2.first, textField, button);
    }

    public static Tuple2<InputTextField, InputTextField> addInputTextFieldInputTextField(GridPane gridPane,
                                                                                         int rowIndex,
                                                                                         String title1,
                                                                                         String title2) {

        InputTextField inputTextField1 = new InputTextField();
        inputTextField1.setPromptText(title1);
        inputTextField1.setLabelFloat(true);
        InputTextField inputTextField2 = new InputTextField();
        inputTextField2.setLabelFloat(true);
        inputTextField2.setPromptText(title2);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(inputTextField1, inputTextField2);
        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 0);
        GridPane.setMargin(hBox, new Insets(Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple2<>(inputTextField1, inputTextField2);
    }

    public static Tuple4<Label, TextField, Label, TextField> addCompactTopLabelTextFieldTopLabelTextField(GridPane gridPane,
                                                                                                          int rowIndex,
                                                                                                          String title1,
                                                                                                          String title2) {
        JFXTextField textField1 = new JFXTextField();
        textField1.setEditable(false);
        textField1.setMouseTransparent(true);
        textField1.setFocusTraversable(false);

        final Tuple2<Label, VBox> topLabelWithVBox1 = getTopLabelWithVBox(title1, textField1);

        JFXTextField textField2 = new JFXTextField();
        textField2.setEditable(false);
        textField2.setMouseTransparent(true);
        textField2.setFocusTraversable(false);

        final Tuple2<Label, VBox> topLabelWithVBox2 = getTopLabelWithVBox(title2, textField2);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(topLabelWithVBox1.second, topLabelWithVBox2.second);
        GridPane.setRowIndex(hBox, rowIndex);
        gridPane.getChildren().add(hBox);

        return new Tuple4<>(topLabelWithVBox1.first, textField1, topLabelWithVBox2.first, textField2);
    }

    public static Tuple2<Button, CheckBox> addButtonCheckBox(GridPane gridPane,
                                                             int rowIndex,
                                                             String buttonTitle,
                                                             String checkBoxTitle) {
        return addButtonCheckBox(gridPane, rowIndex, buttonTitle, checkBoxTitle, 0);
    }

    public static Tuple2<Button, CheckBox> addButtonCheckBox(GridPane gridPane,
                                                             int rowIndex,
                                                             String buttonTitle,
                                                             String checkBoxTitle,
                                                             double top) {
        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);
        CheckBox checkBox = new AutoTooltipCheckBox(checkBoxTitle);
        HBox.setMargin(checkBox, new Insets(6, 0, 0, 0));

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(button, checkBox);
        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 1);
        hBox.setPadding(new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple2<>(button, checkBox);
    }

    public static CheckBox addCheckBox(GridPane gridPane, int rowIndex, String checkBoxTitle) {
        return addCheckBox(gridPane, rowIndex, checkBoxTitle, 0);
    }

    public static CheckBox addCheckBox(GridPane gridPane, int rowIndex, String checkBoxTitle, double top) {
        return addCheckBox(gridPane, rowIndex, 0, checkBoxTitle, top);
    }

    public static CheckBox addCheckBox(GridPane gridPane,
                                       int rowIndex,
                                       int colIndex,
                                       String checkBoxTitle,
                                       double top) {
        CheckBox checkBox = new AutoTooltipCheckBox(checkBoxTitle);
        GridPane.setMargin(checkBox, new Insets(top, 0, 0, 0));
        GridPane.setRowIndex(checkBox, rowIndex);
        GridPane.setColumnIndex(checkBox, colIndex);
        gridPane.getChildren().add(checkBox);
        return checkBox;
    }

    public static RadioButton addRadioButton(GridPane gridPane, int rowIndex, ToggleGroup toggleGroup, String title) {
        RadioButton radioButton = new AutoTooltipRadioButton(title);
        radioButton.setToggleGroup(toggleGroup);
        GridPane.setRowIndex(radioButton, rowIndex);
        gridPane.getChildren().add(radioButton);
        return radioButton;
    }

    public static Tuple3<Label, RadioButton, RadioButton> addTopLabelRadioButtonRadioButton(GridPane gridPane,
                                                                                            int rowIndex,
                                                                                            ToggleGroup toggleGroup,
                                                                                            String title,
                                                                                            String radioButtonTitle1,
                                                                                            String radioButtonTitle2,
                                                                                            double top) {
        RadioButton radioButton1 = new AutoTooltipRadioButton(radioButtonTitle1);
        radioButton1.setToggleGroup(toggleGroup);
        radioButton1.setPadding(new Insets(6, 0, 0, 0));

        RadioButton radioButton2 = new AutoTooltipRadioButton(radioButtonTitle2);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton2.setPadding(new Insets(6, 0, 0, 0));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(radioButton1, radioButton2);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, hBox, top);

        return new Tuple3<>(topLabelWithVBox.first, radioButton1, radioButton2);
    }

    public static Tuple4<Label, TextField, RadioButton, RadioButton> addTopLabelTextFieldRadioButtonRadioButton(GridPane gridPane,
                                                                                                                int rowIndex,
                                                                                                                ToggleGroup toggleGroup,
                                                                                                                String title,
                                                                                                                String textFieldTitle,
                                                                                                                String radioButtonTitle1,
                                                                                                                String radioButtonTitle2,
                                                                                                                double top) {
        JFXTextField textField = new JFXTextField();
        textField.setPromptText(textFieldTitle);

        RadioButton radioButton1 = new AutoTooltipRadioButton(radioButtonTitle1);
        radioButton1.setToggleGroup(toggleGroup);
        radioButton1.setPadding(new Insets(6, 0, 0, 0));

        RadioButton radioButton2 = new AutoTooltipRadioButton(radioButtonTitle2);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton2.setPadding(new Insets(6, 0, 0, 0));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(textField, radioButton1, radioButton2);

        final Tuple2<Label, VBox> labelVBoxTuple2 = addTopLabelWithVBox(gridPane, rowIndex, title, hBox, top);

        return new Tuple4<>(labelVBoxTuple2.first, textField, radioButton1, radioButton2);
    }

    public static CheckBox addLabelCheckBox(GridPane gridPane, int rowIndex, String title) {
        return addLabelCheckBox(gridPane, rowIndex, title, 0);
    }

    public static CheckBox addLabelCheckBox(GridPane gridPane, int rowIndex, String title, double top) {
        CheckBox checkBox = new AutoTooltipCheckBox(title);
        GridPane.setRowIndex(checkBox, rowIndex);
        GridPane.setColumnIndex(checkBox, 0);
        GridPane.setMargin(checkBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(checkBox);

        return checkBox;
    }

    public static ToggleButton addSlideToggleButton(GridPane gridPane, int rowIndex, String title) {
        return addSlideToggleButton(gridPane, rowIndex, title, 0);
    }

    public static ToggleButton addSlideToggleButton(GridPane gridPane, int rowIndex, int columnIndex, String title) {
        return addSlideToggleButton(gridPane, rowIndex, columnIndex, title, 0);
    }

    public static ToggleButton addSlideToggleButton(GridPane gridPane, int rowIndex, String title, double top) {
        ToggleButton toggleButton = new AutoTooltipSlideToggleButton();
        toggleButton.setText(title);
        GridPane.setRowIndex(toggleButton, rowIndex);
        GridPane.setColumnIndex(toggleButton, 0);
        GridPane.setMargin(toggleButton, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(toggleButton);

        return toggleButton;
    }

    public static ToggleButton addSlideToggleButton(GridPane gridPane, int rowIndex, int columnIndex, String title, double top) {
        ToggleButton toggleButton = new AutoTooltipSlideToggleButton();
        toggleButton.setText(title);
        GridPane.setRowIndex(toggleButton, rowIndex);
        GridPane.setColumnIndex(toggleButton, columnIndex);
        GridPane.setMargin(toggleButton, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(toggleButton);

        return toggleButton;
    }

    public static <T> ComboBox<T> addComboBox(GridPane gridPane, int rowIndex, int top) {
        final JFXComboBox<T> comboBox = new JFXComboBox<>();

        GridPane.setRowIndex(comboBox, rowIndex);
        GridPane.setMargin(comboBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(comboBox);
        return comboBox;

    }

    public static <T> Tuple2<Label, ComboBox<T>> addTopLabelComboBox(GridPane gridPane,
                                                                     int rowIndex,
                                                                     String title,
                                                                     String prompt,
                                                                     int top) {
        final Tuple3<VBox, Label, ComboBox<T>> tuple3 = addTopLabelComboBox(title, prompt, 0);
        final VBox vBox = tuple3.first;

        GridPane.setRowIndex(vBox, rowIndex);
        GridPane.setMargin(vBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(vBox);

        return new Tuple2<>(tuple3.second, tuple3.third);
    }

    public static <T> Tuple3<VBox, Label, ComboBox<T>> addTopLabelComboBox(String title, String prompt) {
        return addTopLabelComboBox(title, prompt, 0);
    }

    public static <T> Tuple3<VBox, Label, ComboBox<T>> addTopLabelComboBox(String title, String prompt, int top) {
        Label label = getTopLabel(title);
        VBox vBox = getTopLabelVBox(top);

        final JFXComboBox<T> comboBox = new JFXComboBox<>();
        comboBox.setPromptText(prompt);

        vBox.getChildren().addAll(label, comboBox);

        return new Tuple3<>(vBox, label, comboBox);
    }

    public static <T> Tuple3<VBox, Label, AutoCompleteComboBox<T>> addTopLabelAutocompleteComboBox(String title) {
        return addTopLabelAutocompleteComboBox(title, 0);
    }

    public static <T> Tuple3<VBox, Label, AutoCompleteComboBox<T>> addTopLabelAutocompleteComboBox(String title,
                                                                                                   int top) {
        Label label = getTopLabel(title);
        VBox vBox = getTopLabelVBox(top);

        final AutoCompleteComboBox<T> comboBox = new AutoCompleteComboBox<>();

        vBox.getChildren().addAll(label, comboBox);

        return new Tuple3<>(vBox, label, comboBox);
    }

    private static VBox getTopLabelVBox(int top) {
        VBox vBox = new VBox();
        vBox.setSpacing(0);
        vBox.setPadding(new Insets(top, 0, 0, 0));
        vBox.setAlignment(Pos.CENTER_LEFT);
        return vBox;
    }

    private static Label getTopLabel(String title) {
        Label label = new AutoTooltipLabel(title);
        label.getStyleClass().add("small-text");
        return label;
    }

    public static Tuple2<Label, VBox> addTopLabelWithVBox(GridPane gridPane,
                                                          int rowIndex,
                                                          String title,
                                                          Node node,
                                                          double top) {
        return addTopLabelWithVBox(gridPane, rowIndex, 0, title, node, top);
    }

    public static Tuple2<Label, VBox> addTopLabelWithVBox(GridPane gridPane,
                                                          int rowIndex,
                                                          int columnIndex,
                                                          String title,
                                                          Node node,
                                                          double top) {
        final Tuple2<Label, VBox> topLabelWithVBox = getTopLabelWithVBox(title, node);
        VBox vBox = topLabelWithVBox.second;

        GridPane.setRowIndex(vBox, rowIndex);
        GridPane.setColumnIndex(vBox, columnIndex);
        GridPane.setMargin(vBox, new Insets(top + Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(vBox);

        return new Tuple2<>(topLabelWithVBox.first, vBox);
    }

    public static Tuple2<Label, VBox> getTopLabelWithVBox(String title, Node node) {
        Label label = getTopLabel(title);
        VBox vBox = getTopLabelVBox(0);
        vBox.getChildren().addAll(label, node);

        return new Tuple2<>(label, vBox);
    }

    public static <T> ComboBox<T> addComboBox(GridPane gridPane, int rowIndex) {
        return addComboBox(gridPane, rowIndex, null, 0);
    }

    public static <T> ComboBox<T> addComboBox(GridPane gridPane, int rowIndex, String title) {
        return addComboBox(gridPane, rowIndex, title, 0);
    }

    public static <T> ComboBox<T> addComboBox(GridPane gridPane, int rowIndex, String title, double top) {
        JFXComboBox<T> comboBox = new JFXComboBox<>();
        comboBox.setLabelFloat(true);
        comboBox.setPromptText(title);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        // Default ComboBox does not show promptText after clear selection.
        // https://stackoverflow.com/questions/50569330/how-to-reset-combobox-and-display-prompttext?noredirect=1&lq=1
        comboBox.setButtonCell(getComboBoxButtonCell(title, comboBox));

        GridPane.setRowIndex(comboBox, rowIndex);
        GridPane.setColumnIndex(comboBox, 0);
        GridPane.setMargin(comboBox, new Insets(top + Layout.FLOATING_LABEL_DISTANCE, 0, 0, 0));
        gridPane.getChildren().add(comboBox);

        return comboBox;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label  + AutocompleteComboBox
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T> Tuple2<Label, ComboBox<T>> addLabelAutocompleteComboBox(GridPane gridPane,
                                                                              int rowIndex,
                                                                              String title,
                                                                              double top) {
        AutoCompleteComboBox<T> comboBox = new AutoCompleteComboBox<>();
        final Tuple2<Label, VBox> labelVBoxTuple2 = addTopLabelWithVBox(gridPane, rowIndex, title, comboBox, top);
        return new Tuple2<>(labelVBoxTuple2.first, comboBox);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label + TextField + AutocompleteComboBox
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T> Tuple4<Label, TextField, Label, ComboBox<T>> addTopLabelTextFieldAutocompleteComboBox(
            GridPane gridPane,
            int rowIndex,
            String titleTextfield,
            String titleCombobox
            )
    {
        return addTopLabelTextFieldAutocompleteComboBox(gridPane, rowIndex, titleTextfield, titleCombobox, 0);
    }

    public static <T> Tuple4<Label, TextField, Label, ComboBox<T>> addTopLabelTextFieldAutocompleteComboBox(
            GridPane gridPane,
            int rowIndex,
            String titleTextfield,
            String titleCombobox,
            double top
            )
    {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        final VBox topLabelVBox1 = getTopLabelVBox(5);
        final Label topLabel1 = getTopLabel(titleTextfield);
        final JFXTextField textField = new JFXTextField();
        topLabelVBox1.getChildren().addAll(topLabel1, textField);

        final VBox topLabelVBox2 = getTopLabelVBox(5);
        final Label topLabel2 = getTopLabel(titleCombobox);
        AutoCompleteComboBox<T> comboBox = new AutoCompleteComboBox<>();
        comboBox.setPromptText(titleCombobox);
        comboBox.setLabelFloat(true);
        topLabelVBox2.getChildren().addAll(topLabel2, comboBox);

        hBox.getChildren().addAll(topLabelVBox1, topLabelVBox2);

        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setMargin(hBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple4<>(topLabel1, textField, topLabel2, comboBox);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label + ComboBox + ComboBox
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T, R> Tuple3<Label, ComboBox<R>, ComboBox<T>> addTopLabelComboBoxComboBox(GridPane gridPane,
                                                                                             int rowIndex,
                                                                                             String title) {
        return addTopLabelComboBoxComboBox(gridPane, rowIndex, title, 0);
    }

    public static <T, R> Tuple3<Label, ComboBox<T>, ComboBox<R>> addTopLabelComboBoxComboBox(GridPane gridPane,
                                                                                             int rowIndex,
                                                                                             String title,
                                                                                             double top) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        ComboBox<T> comboBox1 = new JFXComboBox<>();
        ComboBox<R> comboBox2 = new JFXComboBox<>();
        hBox.getChildren().addAll(comboBox1, comboBox2);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, hBox, top);

        return new Tuple3<>(topLabelWithVBox.first, comboBox1, comboBox2);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label + ComboBox + TextField
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T> Tuple4<ComboBox<T>, Label, TextField, HBox> addComboBoxTopLabelTextField(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               String titleCombobox,
                                                                                               String titleTextfield) {
        return addComboBoxTopLabelTextField(gridPane, rowIndex, titleCombobox, titleTextfield, 0);
    }

    public static <T> Tuple4<ComboBox<T>, Label, TextField, HBox> addComboBoxTopLabelTextField(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               String titleCombobox,
                                                                                               String titleTextfield,
                                                                                               double top) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        JFXComboBox<T> comboBox = new JFXComboBox<>();
        comboBox.setPromptText(titleCombobox);
        comboBox.setLabelFloat(true);

        JFXTextField textField = new JFXTextField();

        final VBox topLabelVBox = getTopLabelVBox(5);
        final Label topLabel = getTopLabel(titleTextfield);
        topLabelVBox.getChildren().addAll(topLabel, textField);

        hBox.getChildren().addAll(comboBox, topLabelVBox);

        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setMargin(hBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple4<>(comboBox, topLabel, textField, hBox);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label  + ComboBox + Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T> Tuple3<Label, ComboBox<T>, Button> addLabelComboBoxButton(GridPane gridPane,
                                                                                int rowIndex,
                                                                                String title,
                                                                                String buttonTitle) {
        return addLabelComboBoxButton(gridPane, rowIndex, title, buttonTitle, 0);
    }

    public static <T> Tuple3<Label, ComboBox<T>, Button> addLabelComboBoxButton(GridPane gridPane,
                                                                                int rowIndex,
                                                                                String title,
                                                                                String buttonTitle,
                                                                                double top) {
        Label label = addLabel(gridPane, rowIndex, title, top);

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);

        ComboBox<T> comboBox = new JFXComboBox<>();

        hBox.getChildren().addAll(comboBox, button);

        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 1);
        GridPane.setMargin(hBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple3<>(label, comboBox, button);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label  + ComboBox + Label
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static <T> Tuple3<Label, ComboBox<T>, TextField> addLabelComboBoxLabel(GridPane gridPane,
                                                                                  int rowIndex,
                                                                                  String title,
                                                                                  String textFieldText) {
        return addLabelComboBoxLabel(gridPane, rowIndex, title, textFieldText, 0);
    }

    public static <T> Tuple3<Label, ComboBox<T>, TextField> addLabelComboBoxLabel(GridPane gridPane,
                                                                                  int rowIndex,
                                                                                  String title,
                                                                                  String textFieldText,
                                                                                  double top) {
        Label label = addLabel(gridPane, rowIndex, title, top);

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        ComboBox<T> comboBox = new JFXComboBox<>();
        TextField textField = new TextField(textFieldText);
        textField.setEditable(false);
        textField.setMouseTransparent(true);
        textField.setFocusTraversable(false);

        hBox.getChildren().addAll(comboBox, textField);
        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 1);
        GridPane.setMargin(hBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple3<>(label, comboBox, textField);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addCompactTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               String title,
                                                                                               String value) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, title, value, -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addCompactTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               int colIndex,
                                                                                               String title,
                                                                                               String value,
                                                                                               double top) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, colIndex, title, value, top - Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addCompactTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               int colIndex,
                                                                                               String title) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, colIndex, title, "", -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addCompactTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                               int rowIndex,
                                                                                               int colIndex,
                                                                                               String title,
                                                                                               String value) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, colIndex, title, value, -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                        int rowIndex,
                                                                                        String title,
                                                                                        String value) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, title, value, 0);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                        int rowIndex,
                                                                                        String title,
                                                                                        String value,
                                                                                        double top) {
        return addTopLabelTextFieldWithCopyIcon(gridPane, rowIndex, title, value, top, null);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                        int rowIndex,
                                                                                        String title,
                                                                                        String value,
                                                                                        double top,
                                                                                        String styleClass) {
        TextFieldWithCopyIcon textFieldWithCopyIcon = new TextFieldWithCopyIcon(styleClass);
        textFieldWithCopyIcon.setText(value);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, textFieldWithCopyIcon, top);

        return new Tuple2<>(topLabelWithVBox.first, textFieldWithCopyIcon);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addTopLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                        int rowIndex,
                                                                                        int colIndex,
                                                                                        String title,
                                                                                        String value,
                                                                                        double top) {

        TextFieldWithCopyIcon textFieldWithCopyIcon = new TextFieldWithCopyIcon();
        textFieldWithCopyIcon.setText(value);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, textFieldWithCopyIcon, top);
        topLabelWithVBox.second.setAlignment(Pos.TOP_LEFT);
        GridPane.setColumnIndex(topLabelWithVBox.second, colIndex);

        return new Tuple2<>(topLabelWithVBox.first, textFieldWithCopyIcon);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addConfirmationLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                                 int rowIndex,
                                                                                                 String title,
                                                                                                 String value) {
        return addConfirmationLabelTextFieldWithCopyIcon(gridPane, rowIndex, title, value, 0);
    }

    public static Tuple2<Label, TextFieldWithCopyIcon> addConfirmationLabelTextFieldWithCopyIcon(GridPane gridPane,
                                                                                                 int rowIndex,
                                                                                                 String title,
                                                                                                 String value,
                                                                                                 double top) {
        Label label = addLabel(gridPane, rowIndex, title, top);
        label.getStyleClass().add("confirmation-label");
        GridPane.setHalignment(label, HPos.LEFT);

        TextFieldWithCopyIcon textFieldWithCopyIcon = new TextFieldWithCopyIcon("confirmation-text-field-as-label");
        textFieldWithCopyIcon.setText(value);
        GridPane.setRowIndex(textFieldWithCopyIcon, rowIndex);
        GridPane.setColumnIndex(textFieldWithCopyIcon, 1);
        GridPane.setMargin(textFieldWithCopyIcon, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(textFieldWithCopyIcon);

        return new Tuple2<>(label, textFieldWithCopyIcon);
    }

    public static Tuple3<Label, InfoTextField, VBox> addCompactTopLabelInfoTextField(GridPane gridPane,
                                                                                     int rowIndex,
                                                                                     String labelText,
                                                                                     String fieldText) {
        return addTopLabelInfoTextField(gridPane, rowIndex, labelText, fieldText,
                -Layout.FLOATING_LABEL_DISTANCE);
    }

    public static Tuple3<Label, InfoTextField, VBox> addTopLabelInfoTextField(GridPane gridPane,
                                                                              int rowIndex,
                                                                              String labelText,
                                                                              String fieldText,
                                                                              double top) {
        InfoTextField infoTextField = new InfoTextField();
        infoTextField.setText(fieldText);

        final Tuple2<Label, VBox> labelVBoxTuple2 = addTopLabelWithVBox(gridPane, rowIndex, labelText, infoTextField, top);

        return new Tuple3<>(labelVBoxTuple2.first, infoTextField, labelVBoxTuple2.second);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label + Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Tuple2<Label, Button> addTopLabelButton(GridPane gridPane,
                                                          int rowIndex,
                                                          String labelText,
                                                          String buttonTitle) {
        return addTopLabelButton(gridPane, rowIndex, labelText, buttonTitle, 0);
    }

    public static Tuple2<Label, Button> addTopLabelButton(GridPane gridPane,
                                                          int rowIndex,
                                                          String labelText,
                                                          String buttonTitle,
                                                          double top) {
        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, labelText, button, top);

        return new Tuple2<>(topLabelWithVBox.first, button);
    }

    public static Tuple2<Label, Button> addConfirmationLabelButton(GridPane gridPane,
                                                                   int rowIndex,
                                                                   String labelText,
                                                                   String buttonTitle,
                                                                   double top) {
        Label label = addLabel(gridPane, rowIndex, labelText);
        label.getStyleClass().add("confirmation-label");

        Button button = new AutoTooltipButton(buttonTitle);
        button.getStyleClass().add("confirmation-value");
        button.setDefaultButton(true);

        GridPane.setColumnIndex(button, 1);
        GridPane.setRowIndex(button, rowIndex);
        GridPane.setMargin(label, new Insets(top, 0, 0, 0));
        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setMargin(button, new Insets(top, 0, 0, 0));

        gridPane.getChildren().add(button);

        return new Tuple2<>(label, button);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Label + Button + Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Tuple3<Label, Button, Button> addTopLabel2Buttons(GridPane gridPane,
                                                                    int rowIndex,
                                                                    String labelText,
                                                                    String title1,
                                                                    String title2,
                                                                    double top) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        Button button1 = new AutoTooltipButton(title1);
        button1.setDefaultButton(true);
        button1.getStyleClass().add("action-button");
        button1.setDefaultButton(true);
        button1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button1, Priority.ALWAYS);

        Button button2 = new AutoTooltipButton(title2);
        button2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button2, Priority.ALWAYS);

        hBox.getChildren().addAll(button1, button2);

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, labelText, hBox, top);

        return new Tuple3<>(topLabelWithVBox.first, button1, button2);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Button addButton(GridPane gridPane, int rowIndex, String title) {
        return addButton(gridPane, rowIndex, title, 0);
    }

    public static Button addButtonAfterGroup(GridPane gridPane, int rowIndex, String title) {
        return addButton(gridPane, rowIndex, title, 15);
    }

    public static Button addPrimaryActionButton(GridPane gridPane, int rowIndex, String title, double top) {
        return addButton(gridPane, rowIndex, title, top, true);
    }

    public static Button addPrimaryActionButtonAFterGroup(GridPane gridPane, int rowIndex, String title) {
        return addPrimaryActionButton(gridPane, rowIndex, title, 15);
    }

    public static Button addPrimaryActionButton(GridPane gridPane, int rowIndex, int colIndex, String title, double top) {
        return addButton(gridPane, rowIndex, colIndex, title, top, true);
    }

    public static Button addButton(GridPane gridPane, int rowIndex, int columnIndex, String title) {
        return addButton(gridPane, rowIndex, columnIndex, title, 0, false);
    }

    public static Button addButton(GridPane gridPane, int rowIndex, String title, double top) {
        return addButton(gridPane, rowIndex, title, top, false);
    }

    public static Button addButton(GridPane gridPane, int rowIndex, int columnIndex, String title, double top) {
        return addButton(gridPane, rowIndex, columnIndex, title, top, false);
    }

    public static Button addButton(GridPane gridPane, int rowIndex, String title, double top, boolean isPrimaryAction) {
        return addButton(gridPane, rowIndex, 0, title, top, isPrimaryAction);
    }

    public static Button addButton(GridPane gridPane, int rowIndex, int columnIndex, String title, double top, boolean isPrimaryAction) {
        Button button = new AutoTooltipButton(title);
        if (isPrimaryAction) {
            button.setDefaultButton(true);
            button.getStyleClass().add("action-button");
        }

        GridPane.setRowIndex(button, rowIndex);
        GridPane.setColumnIndex(button, columnIndex);
        gridPane.getChildren().add(button);
        GridPane.setMargin(button, new Insets(top, 0, 0, 0));
        return button;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Button + Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Tuple2<Button, Button> add2Buttons(GridPane gridPane,
                                                     int rowIndex,
                                                     String title1,
                                                     String title2) {
        return add2Buttons(gridPane, rowIndex, title1, title2, 0);
    }

    public static Tuple2<Button, Button> add2ButtonsAfterGroup(GridPane gridPane,
                                                               int rowIndex,
                                                               String title1,
                                                               String title2) {
        return add2ButtonsAfterGroup(gridPane, rowIndex, title1, title2, true);
    }

    public static Tuple2<Button, Button> add2ButtonsAfterGroup(GridPane gridPane,
                                                               int rowIndex,
                                                               String title1,
                                                               String title2,
                                                               boolean hasPrimaryButton) {
        return add2Buttons(gridPane, rowIndex, title1, title2, 15, hasPrimaryButton);
    }

    public static Tuple2<Button, Button> add2Buttons(GridPane gridPane,
                                                     int rowIndex,
                                                     String title1,
                                                     String title2,
                                                     double top) {
        return add2Buttons(gridPane, rowIndex, title1, title2, top, true);
    }

    public static Tuple2<Button, Button> add2Buttons(GridPane gridPane, int rowIndex, String title1,
                                                     String title2, double top, boolean hasPrimaryButton) {
        final Tuple3<Button, Button, HBox> buttonButtonHBoxTuple3 = add2ButtonsWithBox(gridPane, rowIndex, title1, title2, top, hasPrimaryButton);
        return new Tuple2<>(buttonButtonHBoxTuple3.first, buttonButtonHBoxTuple3.second);
    }

    public static Tuple3<Button, Button, HBox> add2ButtonsWithBox(GridPane gridPane, int rowIndex, String title1,
                                                                  String title2, double top, boolean hasPrimaryButton) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        Button button1 = new AutoTooltipButton(title1);

        if (hasPrimaryButton) {
            button1.getStyleClass().add("action-button");
            button1.setDefaultButton(true);
        }

        button1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button1, Priority.ALWAYS);

        Button button2 = new AutoTooltipButton(title2);
        button2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button2, Priority.ALWAYS);

        hBox.getChildren().addAll(button1, button2);

        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 0);
        GridPane.setMargin(hBox, new Insets(top, 10, 0, 0));
        gridPane.getChildren().add(hBox);
        return new Tuple3<>(button1, button2, hBox);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Button + Button + Button
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Tuple3<Button, Button, Button> add3Buttons(GridPane gridPane,
                                                             int rowIndex,
                                                             String title1,
                                                             String title2,
                                                             String title3) {
        return add3Buttons(gridPane, rowIndex, title1, title2, title3, 0);
    }

    public static Tuple3<Button, Button, Button> add3ButtonsAfterGroup(GridPane gridPane,
                                                                       int rowIndex,
                                                                       String title1,
                                                                       String title2,
                                                                       String title3) {
        return add3Buttons(gridPane, rowIndex, title1, title2, title3, 15);
    }

    public static Tuple3<Button, Button, Button> add3Buttons(GridPane gridPane,
                                                             int rowIndex,
                                                             String title1,
                                                             String title2,
                                                             String title3,
                                                             double top) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        Button button1 = new AutoTooltipButton(title1);

        button1.getStyleClass().add("action-button");
        button1.setDefaultButton(true);
        button1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button1, Priority.ALWAYS);

        Button button2 = new AutoTooltipButton(title2);
        button2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button2, Priority.ALWAYS);

        Button button3 = new AutoTooltipButton(title3);
        button3.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button3, Priority.ALWAYS);

        hBox.getChildren().addAll(button1, button2, button3);
        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setColumnIndex(hBox, 0);
        GridPane.setMargin(hBox, new Insets(top, 10, 0, 0));
        gridPane.getChildren().add(hBox);
        return new Tuple3<>(button1, button2, button3);
    }

    public static Tuple4<Button, BusyAnimation, Label, HBox> addButtonBusyAnimationLabelAfterGroup(GridPane gridPane,
                                                                                                   int rowIndex,
                                                                                                   int colIndex,
                                                                                                   String buttonTitle) {
        return addButtonBusyAnimationLabel(gridPane, rowIndex, colIndex, buttonTitle, 15);
    }

    public static Tuple4<Button, BusyAnimation, Label, HBox> addButtonBusyAnimationLabelAfterGroup(GridPane gridPane,
                                                                                                   int rowIndex,
                                                                                                   String buttonTitle) {
        return addButtonBusyAnimationLabelAfterGroup(gridPane, rowIndex, 0, buttonTitle);
    }

    public static Tuple4<Button, BusyAnimation, Label, HBox> addButtonBusyAnimationLabel(GridPane gridPane,
                                                                                         int rowIndex,
                                                                                         int colIndex,
                                                                                         String buttonTitle,
                                                                                         double top) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);

        Button button = new AutoTooltipButton(buttonTitle);
        button.setDefaultButton(true);
        button.getStyleClass().add("action-button");

        BusyAnimation busyAnimation = new BusyAnimation(false);

        Label label = new AutoTooltipLabel();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(button, busyAnimation, label);

        GridPane.setRowIndex(hBox, rowIndex);
        GridPane.setHalignment(hBox, HPos.LEFT);
        GridPane.setColumnIndex(hBox, colIndex);
        GridPane.setMargin(hBox, new Insets(top, 0, 0, 0));
        gridPane.getChildren().add(hBox);

        return new Tuple4<>(button, busyAnimation, label, hBox);
    }

    public static <T> Tuple3<Label, ListView<T>, VBox> addTopLabelListView(GridPane gridPane,
                                                                           int rowIndex,
                                                                           String title) {
        return addTopLabelListView(gridPane, rowIndex, title, 0);
    }

    public static <T> Tuple3<Label, ListView<T>, VBox> addTopLabelListView(GridPane gridPane,
                                                                           int rowIndex,
                                                                           String title,
                                                                           double top) {
        ListView<T> listView = new ListView<>();

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, listView, top);
        return new Tuple3<>(topLabelWithVBox.first, listView, topLabelWithVBox.second);
    }

    public static <T> Tuple3<Label, ListView<T>, VBox> addTopLabelListView(GridPane gridPane,
                                                                           int rowIndex,
                                                                           int columnIndex,
                                                                           String title) {
        return addTopLabelListView(gridPane, rowIndex, columnIndex, title, 0);
    }

    public static <T> Tuple3<Label, ListView<T>, VBox> addTopLabelListView(GridPane gridPane,
                                                                           int rowIndex,
                                                                           int columnIndex,
                                                                           String title,
                                                                           double top) {
        ListView<T> listView = new ListView<>();

        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, columnIndex, title, listView, top);
        return new Tuple3<>(topLabelWithVBox.first, listView, topLabelWithVBox.second);
    }

    public static Tuple2<Label, FlowPane> addTopLabelFlowPane(GridPane gridPane,
                                                              int rowIndex,
                                                              String title,
                                                              double top) {
        return addTopLabelFlowPane(gridPane, rowIndex, title, top, 0);
    }

    public static Tuple2<Label, FlowPane> addTopLabelFlowPane(GridPane gridPane,
                                                              int rowIndex,
                                                              String title,
                                                              double top,
                                                              double bottom) {
        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(10, 10, 10, 10));
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        final Tuple2<Label, VBox> topLabelWithVBox = addTopLabelWithVBox(gridPane, rowIndex, title, flowPane, top);

        GridPane.setMargin(topLabelWithVBox.second, new Insets(top + Layout.FLOATING_LABEL_DISTANCE,
                0, bottom, 0));

        return new Tuple2<>(topLabelWithVBox.first, flowPane);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Remove
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static void removeRowFromGridPane(GridPane gridPane, int gridRow) {
        removeRowsFromGridPane(gridPane, gridRow, gridRow);
    }

    public static void removeRowsFromGridPane(GridPane gridPane, int fromGridRow, int toGridRow) {
        Set<Node> nodes = new CopyOnWriteArraySet<>(gridPane.getChildren());
        nodes.stream()
                .filter(e -> GridPane.getRowIndex(e) != null && GridPane.getRowIndex(e) >= fromGridRow && GridPane.getRowIndex(e) <= toGridRow)
                .forEach(e -> gridPane.getChildren().remove(e));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Icons
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static Text getIconForLabel(GlyphIcons icon, String iconSize, Label label) {
        if (icon.fontFamily().equals(MATERIAL_DESIGN_ICONS)) {
            final Text textIcon = MaterialDesignIconFactory.get().createIcon(icon, iconSize);
            textIcon.setOpacity(0.7);
            label.setContentDisplay(ContentDisplay.LEFT);
            label.setGraphic(textIcon);
            return textIcon;
        } else {
            throw new IllegalArgumentException("Not supported icon type");
        }
    }

    public static Text getSmallIconForLabel(GlyphIcons icon, Label label) {
        return getIconForLabel(icon, "0.769em", label);
    }

    public static Text getRegularIconForLabel(GlyphIcons icon, Label label) {
        return getIconForLabel(icon, "1.231em", label);
    }

    public static Text getIcon(GlyphIcons icon) {
        return getIcon(icon, "1.231em");
    }

    public static Text getBigIcon(GlyphIcons icon) {
        return getIcon(icon, "2em");
    }

    public static Text getIcon(GlyphIcons icon, String iconSize) {
        Text textIcon;

        if (icon.fontFamily().equals(MATERIAL_DESIGN_ICONS)) {
            textIcon = MaterialDesignIconFactory.get().createIcon(icon, iconSize);
        } else {
            throw new IllegalArgumentException("Not supported icon type");
        }

        return textIcon;
    }


    public static Label getIcon(AwesomeIcon icon) {
        final Label label = new Label();
        AwesomeDude.setIcon(label, icon);
        return label;
    }

    public static Label getIconForLabel(AwesomeIcon icon, Label label, String fontSize) {
        AwesomeDude.setIcon(label, icon, fontSize);
        return label;
    }

    public static Button getIconButton(GlyphIcons icon) {
        return getIconButton(icon, "highlight");
    }

    public static Button getIconButton(GlyphIcons icon, String styleClass) {
        return getIconButton(icon, styleClass, "2em");
    }

    public static Button getIconButton(GlyphIcons icon, String styleClass, String iconSize) {
        if (icon.fontFamily().equals(MATERIAL_DESIGN_ICONS)) {
            Button iconButton = MaterialDesignIconFactory.get().createIconButton(icon,
                    "", iconSize, null, ContentDisplay.CENTER);
            iconButton.setId("icon-button");
            iconButton.getGraphic().getStyleClass().add(styleClass);
            iconButton.setPrefWidth(20);
            iconButton.setPrefHeight(20);
            iconButton.setPadding(new Insets(0));
            return iconButton;
        } else {
            throw new IllegalArgumentException("Not supported icon type");
        }
    }

    public static <T> TableView<T> addTableViewWithHeader(GridPane gridPane, int rowIndex, String headerText) {
        return addTableViewWithHeader(gridPane, rowIndex, headerText, 0, null);
    }

    public static <T> TableView<T> addTableViewWithHeader(GridPane gridPane,
                                                          int rowIndex,
                                                          String headerText,
                                                          String groupStyle) {
        return addTableViewWithHeader(gridPane, rowIndex, headerText, 0, groupStyle);
    }

    public static <T> TableView<T> addTableViewWithHeader(GridPane gridPane, int rowIndex, String headerText, int top) {
        return addTableViewWithHeader(gridPane, rowIndex, headerText, top, null);
    }

    public static <T> TableView<T> addTableViewWithHeader(GridPane gridPane,
                                                          int rowIndex,
                                                          String headerText,
                                                          int top,
                                                          String groupStyle) {
        TitledGroupBg titledGroupBg = addTitledGroupBg(gridPane, rowIndex, 1, headerText, top);

        if (groupStyle != null) titledGroupBg.getStyleClass().add(groupStyle);

        TableView<T> tableView = new TableView<>();
        GridPane.setRowIndex(tableView, rowIndex);
        GridPane.setMargin(tableView, new Insets(top + 30, -10, 5, -10));
        gridPane.getChildren().add(tableView);
        tableView.setPlaceholder(new AutoTooltipLabel(Resources.get("table.placeholder.noData")));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;
    }
}
