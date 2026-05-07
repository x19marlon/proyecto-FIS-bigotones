package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class ProfileView {

    private final BuyerController controller = new BuyerController();

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane-root");

        // Header (No sidebar)
        HBox header = ViewHelper.buildEcommerceHeader(
                null, null,
                controller.getCartCount(),
                controller::goToCart
        );
        root.setTop(header);

        // Content
        VBox content = new VBox(24);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Mi Perfil");
        title.getStyleClass().add("label-h1");

        VBox card = ViewHelper.card(30);
        card.setMaxWidth(600);

        User user = controller.getCurrentUser();
        if (user != null) {
            card.getChildren().addAll(
                ViewHelper.infoRow("Nombre:", user.getName()),
                ViewHelper.infoRow("Correo:", user.getEmail()),
                ViewHelper.infoRow("Rol:", user.getRole()),
                ViewHelper.infoRow("Dirección:", user.getAddress() != null ? user.getAddress() : "No registrada")
            );
        } else {
            card.getChildren().add(new Label("No hay sesión activa."));
        }

        content.getChildren().addAll(title, card);
        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 780);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }
}
