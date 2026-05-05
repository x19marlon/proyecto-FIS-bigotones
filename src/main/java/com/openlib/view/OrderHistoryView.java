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

        String userName = DataStore.getInstance().getCurrentUser().getName().split(" ")[0];
        Label sub = new Label("Hola, " + userName + " 👋");
        sub.getStyleClass().add("sidebar-subtitle");
        sub.setPadding(new Insets(0, 6, 16, 6));

        int cartCount = controller.getCartCount();
        String cartLabel = "🛒  Carrito" + (cartCount > 0 ? "  (" + cartCount + ")" : "");

        Button btnCatalog = ViewHelper.sidebarBtn("🏠  Catálogo", false, controller::goToCatalog);
        Button btnCart    = ViewHelper.sidebarBtn(cartLabel, false, controller::goToCart);
        Button btnOrders  = ViewHelper.sidebarBtn("🧾  Mis Pedidos", true, controller::goToOrderHistory);
        Button btnLibrary = ViewHelper.sidebarBtn("📖  Mi Biblioteca", false, controller::goToLibrary);

        Region spacer = ViewHelper.vSpacer();
        Button btnLogout = new Button("⬅  Cerrar sesión");
        btnLogout.getStyleClass().add("logout-btn");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> controller.logout());

        sidebar.getChildren().addAll(logo, sub, btnCatalog, btnCart, btnOrders, btnLibrary, spacer, btnLogout);
        return sidebar;
    }

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.getStyleClass().add("pane-root");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Mi Historial de Compras");
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
