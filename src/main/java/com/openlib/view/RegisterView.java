package com.openlib.view;

import com.openlib.controller.AuthController;
import com.openlib.util.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RegisterView {

    private final AuthController controller = new AuthController();

    public Scene buildScene() {
        StackPane root = new StackPane();
        root.getStyleClass().add("auth-container");

        Circle c1 = new Circle(180);
        c1.setFill(Color.web("#A7C4BC", 0.25));
        c1.setTranslateX(350); c1.setTranslateY(-250);

        VBox card = new VBox(16);
        card.getStyleClass().add("auth-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(420);

        Label logo = new Label("📚 OpenLib Market");
        logo.getStyleClass().add("auth-logo");

        Label title = new Label("Crear cuenta");
        title.getStyleClass().add("label-h2");

        Label nameLbl = new Label("Nombre completo");
        nameLbl.getStyleClass().add("input-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Tu nombre");
        nameField.getStyleClass().add("input-field");
        nameField.setMaxWidth(Double.MAX_VALUE);
        javafx.application.Platform.runLater(nameField::requestFocus);

        Label emailLbl = new Label("Correo electrónico");
        emailLbl.getStyleClass().add("input-label");
        TextField emailField = new TextField();
        emailField.setPromptText("usuario@ejemplo.com");
        emailField.getStyleClass().add("input-field");
        emailField.setMaxWidth(Double.MAX_VALUE);

        Label passLbl = new Label("Contraseña");
        passLbl.getStyleClass().add("input-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Mínimo 6 caracteres");
        passField.getStyleClass().add("input-field");
        passField.setMaxWidth(Double.MAX_VALUE);

        Label confirmLbl = new Label("Confirmar contraseña");
        confirmLbl.getStyleClass().add("input-label");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Repite la contraseña");
        confirmField.getStyleClass().add("input-field");
        confirmField.setMaxWidth(Double.MAX_VALUE);

        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("alert-error");
        errorLbl.setWrapText(true);
        errorLbl.setMaxWidth(Double.MAX_VALUE);
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        Button registerBtn = new Button("Crear cuenta");
        registerBtn.getStyleClass().add("btn-primary");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(44);

        HBox backRow = new HBox(6);
        backRow.setAlignment(Pos.CENTER);
        Label backLbl = new Label("¿Ya tienes cuenta?");
        backLbl.setStyle("-fx-text-fill: #5C5C5C; -fx-font-size: 13px;");
        Button backBtn = new Button("Inicia sesión");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2F5D62; "
                + "-fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true; -fx-border-width: 0;");
        backBtn.setOnAction(e -> SceneManager.getInstance().showLogin());
        backRow.getChildren().addAll(backLbl, backBtn);

        card.getChildren().addAll(logo, title,
                nameLbl, nameField,
                emailLbl, emailField,
                passLbl, passField,
                confirmLbl, confirmField,
                errorLbl, registerBtn, backRow);

        registerBtn.setOnAction(e -> {
            String err = controller.register(
                    nameField.getText(), emailField.getText(),
                    passField.getText(), confirmField.getText());
            if (err != null) {
                errorLbl.setText(err);
                errorLbl.setVisible(true);
                errorLbl.setManaged(true);
            }
        });

        root.getChildren().addAll(c1, card);
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }
}
