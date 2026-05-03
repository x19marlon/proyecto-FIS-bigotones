package com.openlib.view;

import com.openlib.controller.AuthController;
import com.openlib.util.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.animation.FadeTransition;

public class LoginView {

    private final AuthController controller = new AuthController();

    public Scene buildScene() {
        // Background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0D1117;");

        // Decorative circles
        Circle c1 = new Circle(200);
        c1.setFill(Color.web("#2EA043", 0.04));
        c1.setTranslateX(-350);
        c1.setTranslateY(-200);

        Circle c2 = new Circle(150);
        c2.setFill(Color.web("#2EA043", 0.06));
        c2.setTranslateX(400);
        c2.setTranslateY(300);

        // Auth card
        VBox card = new VBox(18);
        card.getStyleClass().add("auth-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(420);

        // Logo
        Label logo = new Label("📚 OpenLib Market");
        logo.getStyleClass().add("auth-logo");
        Label tagline = new Label("La biblioteca digital de tu institución");
        tagline.getStyleClass().add("auth-tagline");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #30363D;");

        Label title = new Label("Iniciar sesión");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F0F6FC;");

        // Fields
        Label emailLbl = new Label("Correo electrónico");
        emailLbl.getStyleClass().add("input-label");
        TextField emailField = new TextField();
        emailField.setPromptText("usuario@ejemplo.com");
        emailField.getStyleClass().add("input-field");
        emailField.setMaxWidth(Double.MAX_VALUE);

        Label passLbl = new Label("Contraseña");
        passLbl.getStyleClass().add("input-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.getStyleClass().add("input-field");
        passField.setMaxWidth(Double.MAX_VALUE);

        // Error label
        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("alert-error");
        errorLbl.setWrapText(true);
        errorLbl.setMaxWidth(Double.MAX_VALUE);
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        // Login button
        Button loginBtn = new Button("Ingresar");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(44);

        // Demo hint
        VBox demoBox = new VBox(4);
        demoBox.setStyle("-fx-background-color: #21262D; -fx-background-radius: 6; -fx-padding: 12;");
        Label demoTitle = new Label("Cuentas de demo:");
        demoTitle.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label demoAdmin = new Label("🔐 Admin → admin@openlib.com / admin123");
        demoAdmin.setStyle("-fx-text-fill: #6E7681; -fx-font-size: 12px;");
        Label demoBuyer = new Label("👤 Buyer → danna@javeriana.edu.co / buyer123");
        demoBuyer.setStyle("-fx-text-fill: #6E7681; -fx-font-size: 12px;");
        demoBox.getChildren().addAll(demoTitle, demoAdmin, demoBuyer);

        // Register link
        HBox regRow = new HBox(6);
        regRow.setAlignment(Pos.CENTER);
        Label regLbl = new Label("¿No tienes cuenta?");
        regLbl.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 13px;");
        Button regBtn = new Button("Regístrate");
        regBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2EA043; "
                + "-fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true; -fx-border-width: 0;");
        regBtn.setOnAction(e -> SceneManager.getInstance().showRegister());
        regRow.getChildren().addAll(regLbl, regBtn);

        card.getChildren().addAll(logo, tagline, sep, title,
                emailLbl, emailField, passLbl, passField,
                errorLbl, loginBtn, demoBox, regRow);

        // Login action
        Runnable doLogin = () -> {
            String err = controller.login(emailField.getText(), passField.getText());
            if (err != null) {
                errorLbl.setText(err);
                errorLbl.setVisible(true);
                errorLbl.setManaged(true);
            }
        };
        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());

        // Fade in
        FadeTransition ft = new FadeTransition(Duration.millis(500), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        root.getChildren().addAll(c1, c2, card);
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }
}
