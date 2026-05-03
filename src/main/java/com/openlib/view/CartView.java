package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.CartItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class CartView {

    private final BuyerController controller = new BuyerController();
    private VBox itemsBox;
    private Label totalLbl;

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane-root");
        root.setLeft(buildSidebar());
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(0, 10, 20, 10));

        Label logo = new Label("📚 OpenLib");
        logo.getStyleClass().add("sidebar-title");
        logo.setPadding(new Insets(24, 6, 2, 6));
        Label sub = new Label("Módulo Buyer");
        sub.getStyleClass().add("sidebar-subtitle");
        sub.setPadding(new Insets(0, 6, 16, 6));

        Button btnCatalog = ViewHelper.sidebarBtn("🏠  Catálogo", false, controller::goToCatalog);
        Button btnCart    = ViewHelper.sidebarBtn("🛒  Carrito", true, controller::goToCart);
        Button btnLibrary = ViewHelper.sidebarBtn("📖  Mi Biblioteca", false, controller::goToLibrary);

        Region spacer = ViewHelper.vSpacer();
        Button btnLogout = new Button("⬅  Cerrar sesión");
        btnLogout.getStyleClass().add("logout-btn");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> controller.logout());

        sidebar.getChildren().addAll(logo, sub, btnCatalog, btnCart, btnLibrary, spacer, btnLogout);
        return sidebar;
    }

    private HBox buildContent() {
        HBox content = new HBox(24);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #0D1117;");

        // Left: items list
        VBox leftCol = new VBox(12);
        VBox.setVgrow(leftCol, Priority.ALWAYS);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label h1 = new Label("🛒 Mi Carrito");
        h1.getStyleClass().add("label-h1");

        itemsBox = new VBox(12);
        refreshItems();

        ScrollPane scroll = new ScrollPane(itemsBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        leftCol.getChildren().addAll(h1, scroll);

        // Right: summary
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(280);
        rightCol.setMaxWidth(280);

        VBox summaryCard = ViewHelper.card(20);
        Label summaryTitle = new Label("Resumen del pedido");
        summaryTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F0F6FC;");

        totalLbl = new Label("Total: $0.00");
        totalLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2EA043;");
        updateTotal();

        Label note = new Label("Todos los libros son de acceso libre y gratuito.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 12px;");

        Button checkoutBtn = new Button("Proceder al Checkout →");
        checkoutBtn.getStyleClass().add("btn-primary");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setPrefHeight(44);
        checkoutBtn.setOnAction(e -> {
            if (controller.getCart().isEmpty()) return;
            controller.goToCheckout();
        });

        Button continueBtn = new Button("← Seguir explorando");
        continueBtn.getStyleClass().add("btn-secondary");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setOnAction(e -> controller.goToCatalog());

        summaryCard.getChildren().addAll(summaryTitle, totalLbl, note, checkoutBtn, continueBtn);
        rightCol.getChildren().add(summaryCard);

        content.getChildren().addAll(leftCol, rightCol);
        return content;
    }

    private void refreshItems() {
        itemsBox.getChildren().clear();
        List<CartItem> cart = controller.getCart();

        if (cart.isEmpty()) {
            Label empty = new Label("Tu carrito está vacío. ¡Explora el catálogo!");
            empty.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 15px; -fx-padding: 20;");
            itemsBox.getChildren().add(empty);
            return;
        }

        for (CartItem item : cart) {
            itemsBox.getChildren().add(buildItemRow(item));
        }
    }

    private HBox buildItemRow(CartItem item) {
        HBox row = new HBox(16);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(16));
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane cover = ViewHelper.bookCover(item.getBook().getCoverColor(),
                item.getBook().getTitle(), false);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label titleLbl = new Label(item.getBook().getTitle());
        titleLbl.getStyleClass().add("label-title");
        Label authorLbl = new Label(item.getBook().getAuthor());
        authorLbl.getStyleClass().add("label-body");
        Label catLbl = new Label(item.getBook().getCategory());
        catLbl.getStyleClass().add("badge");
        info.getChildren().addAll(titleLbl, authorLbl, catLbl);

        Label price = new Label("GRATIS");
        price.setStyle("-fx-text-fill: #2EA043; -fx-font-weight: bold; -fx-font-size: 15px;");

        Button removeBtn = new Button("Quitar");
        removeBtn.getStyleClass().add("btn-danger");
        removeBtn.setOnAction(e -> {
            controller.removeFromCart(item.getBook().getId());
            refreshItems();
            updateTotal();
        });

        row.getChildren().addAll(cover, info, price, removeBtn);
        return row;
    }

    private void updateTotal() {
        totalLbl.setText(String.format("Total: $%.2f", controller.getCartTotal()));
    }
}
