package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.Order;
import com.openlib.model.OrderItem;
import com.openlib.util.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHistoryView {

    private final BuyerController controller = new BuyerController();

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane-root");

        // Header
        HBox header = ViewHelper.buildEcommerceHeader(
                null, null,
                controller.getCartCount(),
                controller::goToCart
        );
        root.setTop(header);

        // Content (No sidebar)
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1200, 780);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.getStyleClass().add("pane-root");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("🧾 Mi Historial de Compras");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox inner = new VBox(16);
        inner.setPadding(new Insets(24));

        List<Order> orders = controller.getMyOrders();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (orders.isEmpty()) {
            VBox emptyBox = ViewHelper.stateView("🧾", 
                "Aún no tienes compras", 
                "Tus pedidos aparecerán aquí una vez que realices una compra.");
            inner.getChildren().add(emptyBox);
        } else {
            for (Order order : orders) {
                inner.getChildren().add(buildOrderCard(order, fmt));
            }
        }

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }

    private VBox buildOrderCard(Order order, DateTimeFormatter fmt) {
        VBox card = ViewHelper.card(20);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox meta = new VBox(4);
        Label orderIdLbl = new Label("Pedido #" + order.getId());
        orderIdLbl.getStyleClass().add("label-title");
        Label dateLbl = new Label("Realizado el " + order.getDate().format(fmt));
        dateLbl.getStyleClass().add("label-small");
        meta.getChildren().addAll(orderIdLbl, dateLbl);

        Region spc = ViewHelper.spacer();

        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusBadge = new Label(order.getStatus());
        statusBadge.getStyleClass().add("badge");
        Label totalLbl = new Label(String.format("$%.2f", order.getTotal()));
        totalLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2F5D62;");
        statusBox.getChildren().addAll(statusBadge, totalLbl);

        header.getChildren().addAll(meta, spc, statusBox);

        Separator sep = new Separator();
        sep.getStyleClass().add("separator-light");

        VBox itemsBox = new VBox(8);
        for (OrderItem item : order.getItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            
            Label bookTitle = new Label("📗 " + item.getBook().getTitle());
            bookTitle.getStyleClass().add("label-body");
            bookTitle.setWrapText(true);
            HBox.setHgrow(bookTitle, Priority.ALWAYS);

            Label qty = new Label("x" + item.getQuantity());
            qty.getStyleClass().add("label-small");
            qty.setStyle("-fx-font-weight: bold;");

            Label price = new Label(String.format("$%.2f", item.getPriceAtPurchase() * item.getQuantity()));
            price.getStyleClass().add("label-body");

            itemRow.getChildren().addAll(bookTitle, qty, price);
            itemsBox.getChildren().add(itemRow);
        }

        card.getChildren().addAll(header, sep, itemsBox);
        return card;
    }
}
