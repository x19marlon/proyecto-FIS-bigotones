package com.openlib.view;

import com.openlib.util.SceneManager;

import com.openlib.controller.BuyerController;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.format.DateTimeFormatter;

public class CheckoutView {

    private final BuyerController controller = new BuyerController();

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane-root");
        root.setLeft(buildSidebar());
        root.setCenter(buildCheckoutForm());

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
        Label sub = new Label("Checkout");
        sub.getStyleClass().add("sidebar-subtitle");
        sub.setPadding(new Insets(0, 6, 16, 6));

        Label step1 = new Label("✅  1. Carrito");
        step1.setStyle("-fx-text-fill: #2F5D62; -fx-font-size: 13px; -fx-padding: 8 16;");
        Label step2 = new Label("➡  2. Checkout");
        step2.setStyle("-fx-text-fill: #2E2E2E; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 16;");
        Label step3 = new Label("◯  3. Confirmación");
        step3.setStyle("-fx-text-fill: #6E7681; -fx-font-size: 13px; -fx-padding: 8 16;");

        Region spacer = ViewHelper.vSpacer();
        Button btnOrders = ViewHelper.sidebarBtn("🧾  Mis Pedidos", false, controller::goToOrderHistory);
        Button backBtn = new Button("← Volver al carrito");
        backBtn.getStyleClass().add("sidebar-btn");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> controller.goToCart());

        sidebar.getChildren().addAll(logo, sub, step1, step2, step3, spacer, btnOrders, backBtn);
        return sidebar;
    }

    private HBox buildCheckoutForm() {
        HBox content = new HBox(24);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("pane-root");

        // Left: billing form
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label h1 = new Label("Información de facturación");
        h1.getStyleClass().add("label-h1");

        VBox formCard = ViewHelper.card(20);

        // Name
        Label nameLbl = new Label("Nombre completo");
        nameLbl.getStyleClass().add("input-label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("input-field");
        nameField.setMaxWidth(Double.MAX_VALUE);

        // Email
        Label emailLbl = new Label("Correo electrónico");
        emailLbl.getStyleClass().add("input-label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("input-field");
        emailField.setMaxWidth(Double.MAX_VALUE);

        // Address
        Label addrLbl = new Label("Dirección (institucional)");
        addrLbl.getStyleClass().add("input-label");
        TextField addrField = new TextField();
        addrField.setPromptText("Ej: Campus Central, Edificio A");
        addrField.getStyleClass().add("input-field");
        addrField.setMaxWidth(Double.MAX_VALUE);

        // Payment method
        Label payLbl = new Label("Método de pago");
        payLbl.getStyleClass().add("input-label");

        ToggleGroup payGroup = new ToggleGroup();
        RadioButton payFree = new RadioButton("🎓 Acceso Institucional (Gratis)");
        payFree.setToggleGroup(payGroup);
        payFree.setSelected(true);
        payFree.setStyle("-fx-text-fill: #2E2E2E;");
        RadioButton payDonation = new RadioButton("❤️  Donación simbólica ($1.00)");
        payDonation.setToggleGroup(payGroup);
        payDonation.setStyle("-fx-text-fill: #2E2E2E;");

        formCard.getChildren().addAll(
                nameLbl, nameField,
                emailLbl, emailField,
                addrLbl, addrField,
                payLbl, payFree, payDonation);

        leftCol.getChildren().addAll(h1, formCard);

        // Right: order summary
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(300);
        rightCol.setMaxWidth(300);

        VBox summaryCard = ViewHelper.card(20);
        Label sumTitle = new Label("Resumen del pedido");
        sumTitle.getStyleClass().add("label-title");
        summaryCard.getChildren().add(sumTitle);

        for (CartItem item : controller.getCart()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            String titleStr = item.getBook().getTitle() == null ? "Sin título" : item.getBook().getTitle();
            Label t = new Label(titleStr);
            t.getStyleClass().add("label-body");
            t.setWrapText(true);
            HBox.setHgrow(t, Priority.ALWAYS);
            Label p = new Label("GRATIS");
            p.getStyleClass().add("product-price");
            row.getChildren().addAll(t, p);
            summaryCard.getChildren().add(row);
        }

        Separator sep = new Separator();
        sep.getStyleClass().add("separator-light");

        Label totalLbl = new Label(String.format("Total: $%.2f", controller.getCartTotal()));
        totalLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2F5D62;");

        // Error
        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("alert-error");
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        Button confirmBtn = new Button("✓ Confirmar pedido");
        confirmBtn.getStyleClass().add("btn-primary");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setPrefHeight(44);
        confirmBtn.setOnAction(e -> {
            if (nameField.getText().isBlank() || emailField.getText().isBlank()) {
                errorLbl.setText("Por favor completa los campos requeridos.");
                errorLbl.setVisible(true);
                errorLbl.setManaged(true);
                return;
            }
            Order order = controller.placeOrder();
            if (order != null) showConfirmation(order);
        });

        summaryCard.getChildren().addAll(sep, totalLbl, errorLbl, confirmBtn);
        rightCol.getChildren().add(summaryCard);

        content.getChildren().addAll(leftCol, rightCol);
        return content;
    }

    private void showConfirmation(Order order) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(250,247,240,0.97);");

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setMaxWidth(500);

        // Success icon
        StackPane iconBg = new StackPane();
        Circle circle = new Circle(40);
        circle.setFill(Color.web("#EDE3D2"));
        Label checkLbl = new Label("✓");
        checkLbl.setStyle("-fx-font-size: 36px; -fx-text-fill: #2F5D62; -fx-font-weight: bold;");
        iconBg.getChildren().addAll(circle, checkLbl);

        Label titleLbl = new Label("¡Pedido confirmado!");
        titleLbl.getStyleClass().add("label-h1");

        Label subLbl = new Label("Orden #" + order.getId() + " — "
                + order.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        subLbl.getStyleClass().add("label-body");

        VBox orderCard = ViewHelper.card(20);
        orderCard.setMaxWidth(400);
        for (com.openlib.model.OrderItem item : order.getItems()) {
            String titleStr = item.getBook().getTitle() == null ? "Sin título" : item.getBook().getTitle();
            Label lbl = new Label("📗 " + titleStr);
            lbl.getStyleClass().add("label-body");
            orderCard.getChildren().add(lbl);
        }

        Label totalLbl = new Label(String.format("Total cobrado: $%.2f", order.getTotal()));
        totalLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2F5D62;");

        Button libBtn = new Button("📖 Ir a mi Biblioteca");
        libBtn.getStyleClass().add("btn-primary");
        libBtn.setPrefWidth(250);
        libBtn.setOnAction(e -> controller.goToLibrary());

        Button histBtn = new Button("🧾 Ver Mis Pedidos");
        histBtn.getStyleClass().add("btn-accent");
        histBtn.setPrefWidth(250);
        histBtn.setOnAction(e -> controller.goToOrderHistory());

        Button catalogBtn = new Button("Seguir explorando");
        catalogBtn.getStyleClass().add("btn-secondary");
        catalogBtn.setPrefWidth(250);
        catalogBtn.setOnAction(e -> controller.goToCatalog());

        box.getChildren().addAll(iconBg, titleLbl, subLbl, orderCard, totalLbl, libBtn, histBtn, catalogBtn);
        overlay.getChildren().add(box);

        // Replace center content
        Scene scene = SceneManager.getInstance().getStage().getScene();
        BorderPane bp = (BorderPane) scene.getRoot();
        bp.setCenter(overlay);
    }
}
