package com.openlib.view;

import com.openlib.controller.AdminController;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminOrdersView {

    private final AdminController controller = new AdminController();

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
        Label sub = new Label("Panel Admin");
        sub.getStyleClass().add("sidebar-subtitle");
        sub.setPadding(new Insets(0, 6, 16, 6));

        Button btnDash  = ViewHelper.sidebarBtn("📊  Dashboard", false, controller::goToDashboard);
        Button btnBooks = ViewHelper.sidebarBtn("📚  Libros",    false, controller::goToBooks);
        Button btnUsers = ViewHelper.sidebarBtn("👥  Usuarios",  false, controller::goToUsers);
        Button btnOrders= ViewHelper.sidebarBtn("🧾  Pedidos",   true,  controller::goToOrders);

        Region spacer = ViewHelper.vSpacer();
        Button btnLogout = new Button("⬅  Cerrar sesión");
        btnLogout.getStyleClass().add("logout-btn");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> controller.logout());

        sidebar.getChildren().addAll(logo, sub, btnDash, btnBooks, btnUsers, btnOrders, spacer, btnLogout);
        return sidebar;
    }

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #0D1117;");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Historial de Pedidos");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox inner = new VBox(16);
        inner.setPadding(new Insets(24));

        List<Order> orders = controller.getAllOrders();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (orders.isEmpty()) {
            Label empty = new Label("No hay pedidos registrados aún.");
            empty.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 15px; -fx-padding: 24;");
            inner.getChildren().add(empty);
        } else {
            Label count = new Label("Total: " + orders.size() + " pedidos");
            count.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 13px;");
            inner.getChildren().add(count);

            for (Order order : orders) {
                VBox card = ViewHelper.card(16);

                HBox header = new HBox(12);
                header.setAlignment(Pos.CENTER_LEFT);

                Label orderIdLbl = new Label("Orden #" + order.getId());
                orderIdLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #F0F6FC; -fx-font-size: 14px;");

                Label userLbl = new Label("👤 " + order.getUser().getName());
                userLbl.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 13px;");

                Label dateLbl = new Label("🕐 " + order.getDate().format(fmt));
                dateLbl.setStyle("-fx-text-fill: #6E7681; -fx-font-size: 12px;");

                Region spc = ViewHelper.spacer();

                Label statusBadge = new Label("✓ " + order.getStatus());
                statusBadge.getStyleClass().add("badge");

                Label totalLbl = new Label(String.format("$%.2f", order.getTotal()));
                totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2EA043; -fx-font-size: 15px;");

                header.getChildren().addAll(orderIdLbl, userLbl, dateLbl, spc, statusBadge, totalLbl);

                // Books list
                HBox booksRow = new HBox(8);
                booksRow.setAlignment(Pos.CENTER_LEFT);
                for (com.openlib.model.OrderItem item : order.getItems()) {
                    Label bookBadge = new Label("📗 " + item.getBook().getTitle());
                    bookBadge.setStyle("-fx-background-color: #21262D; -fx-text-fill: #F0F6FC; "
                            + "-fx-padding: 4 10; -fx-background-radius: 4; -fx-font-size: 12px;");
                    booksRow.getChildren().add(bookBadge);
                }

                card.getChildren().addAll(header, booksRow);
                inner.getChildren().add(card);
            }
        }

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }
}
