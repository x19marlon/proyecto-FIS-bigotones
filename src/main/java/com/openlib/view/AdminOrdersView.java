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
        content.getStyleClass().add("pane-root");

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
            empty.getStyleClass().add("label-h2");
            empty.setPadding(new Insets(24));
            inner.getChildren().add(empty);
        } else {
            Label count = new Label("Total: " + orders.size() + " pedidos");
            count.getStyleClass().add("label-small");
            inner.getChildren().add(count);

            for (Order order : orders) {
                VBox card = ViewHelper.card(16);

                HBox header = new HBox(12);
                header.setAlignment(Pos.CENTER_LEFT);

                Label orderIdLbl = new Label("Orden #" + order.getId());
                orderIdLbl.getStyleClass().add("label-title");

                Label userLbl = new Label("👤 " + order.getUser().getName());
                userLbl.getStyleClass().add("label-body");

                Label dateLbl = new Label("🕐 " + order.getDate().format(fmt));
                dateLbl.getStyleClass().add("label-small");

                Region spc = ViewHelper.spacer();

                Label statusBadge = new Label(order.getStatus());
                statusBadge.getStyleClass().add("badge");
                // Style based on status
                if ("PENDING".equals(order.getStatus())) statusBadge.setStyle("-fx-background-color: #f39c12;");
                else if ("PAID".equals(order.getStatus())) statusBadge.setStyle("-fx-background-color: #3498db;");
                else if ("SHIPPED".equals(order.getStatus())) statusBadge.setStyle("-fx-background-color: #9b59b6;");
                else if ("DELIVERED".equals(order.getStatus())) statusBadge.setStyle("-fx-background-color: #27ae60;");
                else if ("CANCELLED".equals(order.getStatus())) statusBadge.setStyle("-fx-background-color: #e74c3c;");

                Label totalLbl = new Label(String.format("$%.2f", order.getTotal()));
                totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2F5D62; -fx-font-size: 15px;");

                header.getChildren().addAll(orderIdLbl, userLbl, dateLbl, spc, statusBadge, totalLbl);

                // Actions row
                HBox actions = new HBox(8);
                actions.setAlignment(Pos.CENTER_LEFT);
                actions.setPadding(new Insets(8, 0, 0, 0));

                Button btnNext = new Button("Siguiente Paso ➔");
                btnNext.getStyleClass().add("btn-small");
                btnNext.setOnAction(e -> {
                    try {
                        controller.advanceOrder(order.getId());
                        controller.goToOrders(); // Refresh
                    } catch (Exception ex) {
                        ViewHelper.showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                    }
                });

                Button btnCancel = new Button("Cancelar ✖");
                btnCancel.getStyleClass().addAll("btn-small", "btn-danger");
                btnCancel.setOnAction(e -> {
                    try {
                        controller.cancelOrder(order.getId());
                        controller.goToOrders(); // Refresh
                    } catch (Exception ex) {
                        ViewHelper.showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                    }
                });

                // Disable buttons if final states
                if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                    btnNext.setDisable(true);
                    btnCancel.setDisable(true);
                }
                if ("SHIPPED".equals(order.getStatus())) {
                    btnCancel.setDisable(true);
                }

                actions.getChildren().addAll(btnNext, btnCancel);

                // Books list
                HBox booksRow = new HBox(8);
                booksRow.setAlignment(Pos.CENTER_LEFT);
                booksRow.setPadding(new Insets(8, 0, 0, 0));
                for (com.openlib.model.OrderItem item : order.getItems()) {
                    Label bookBadge = new Label("📗 " + item.getBook().getTitle());
                    bookBadge.setStyle("-fx-background-color: #EDE3D2; -fx-text-fill: #2E2E2E; "
                            + "-fx-padding: 4 10; -fx-background-radius: 4; -fx-font-size: 12px;");
                    booksRow.getChildren().add(bookBadge);
                }

                card.getChildren().addAll(header, actions, booksRow);
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
