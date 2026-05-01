package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class LibraryView {

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
        Label sub = new Label("Módulo Buyer");
        sub.getStyleClass().add("sidebar-subtitle");
        sub.setPadding(new Insets(0, 6, 16, 6));

        Button btnCatalog = ViewHelper.sidebarBtn("🏠  Catálogo", false, controller::goToCatalog);
        Button btnCart    = ViewHelper.sidebarBtn("🛒  Carrito", false, controller::goToCart);
        Button btnLibrary = ViewHelper.sidebarBtn("📖  Mi Biblioteca", true, controller::goToLibrary);

        Region spacer = ViewHelper.vSpacer();
        Button btnLogout = new Button("⬅  Cerrar sesión");
        btnLogout.getStyleClass().add("logout-btn");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> controller.logout());

        sidebar.getChildren().addAll(logo, sub, btnCatalog, btnCart, btnLibrary, spacer, btnLogout);
        return sidebar;
    }

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #0D1117;");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("📖 Mi Biblioteca");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox mainPad = new VBox(20);
        mainPad.setPadding(new Insets(24));
        VBox.setVgrow(mainPad, Priority.ALWAYS);

        List<Order> orders = controller.getMyOrders();

        if (orders.isEmpty()) {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            Label emptyIcon = new Label("📭");
            emptyIcon.setStyle("-fx-font-size: 48px;");
            Label emptyLbl = new Label("Tu biblioteca está vacía.");
            emptyLbl.setStyle("-fx-font-size: 18px; -fx-text-fill: #8B949E;");
            Label emptyHint = new Label("Explora el catálogo y agrega libros a tu carrito.");
            emptyHint.setStyle("-fx-font-size: 13px; -fx-text-fill: #6E7681;");
            Button exploreBtn = new Button("Explorar catálogo →");
            exploreBtn.getStyleClass().add("btn-primary");
            exploreBtn.setOnAction(e -> controller.goToCatalog());
            empty.getChildren().addAll(emptyIcon, emptyLbl, emptyHint, exploreBtn);
            VBox.setVgrow(empty, Priority.ALWAYS);
            mainPad.getChildren().add(empty);
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Order order : orders) {
                VBox orderCard = ViewHelper.card(20);

                HBox orderHeader = new HBox(12);
                orderHeader.setAlignment(Pos.CENTER_LEFT);
                Label orderIdLbl = new Label("Orden #" + order.getId());
                orderIdLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #F0F6FC;");
                Label dateLbl = new Label(order.getDate().format(fmt));
                dateLbl.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 12px;");
                Label statusBadge = new Label("✓ " + order.getStatus());
                statusBadge.getStyleClass().add("badge");
                Region spc = ViewHelper.spacer();
                Label totalLbl = new Label(String.format("$%.2f", order.getTotal()));
                totalLbl.setStyle("-fx-text-fill: #2EA043; -fx-font-weight: bold;");
                orderHeader.getChildren().addAll(orderIdLbl, dateLbl, spc, statusBadge, totalLbl);

                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: #30363D;");

                FlowPane booksFlow = new FlowPane(12, 8);
                for (CartItem item : order.getItems()) {
                    HBox bookRow = new HBox(10);
                    bookRow.setAlignment(Pos.CENTER_LEFT);
                    bookRow.setStyle("-fx-background-color: #21262D; -fx-background-radius: 6; -fx-padding: 10 14;");

                    StackPane cover = ViewHelper.bookCover(item.getBook().getCoverColor(),
                            item.getBook().getTitle(), false);
                    cover.setScaleX(0.6); cover.setScaleY(0.6);

                    VBox info = new VBox(3);
                    Label tLbl = new Label(item.getBook().getTitle());
                    tLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F0F6FC;");
                    Label aLbl = new Label(item.getBook().getAuthor());
                    aLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8B949E;");

                    Button dlBtn = new Button("⬇ Descargar");
                    dlBtn.getStyleClass().add("btn-icon");
                    dlBtn.setOnAction(ev -> showDownloadDialog(item.getBook().getTitle()));

                    info.getChildren().addAll(tLbl, aLbl);
                    bookRow.getChildren().addAll(cover, info, dlBtn);
                    booksFlow.getChildren().add(bookRow);
                }

                orderCard.getChildren().addAll(orderHeader, sep, booksFlow);
                mainPad.getChildren().add(orderCard);
            }
        }

        ScrollPane scroll = new ScrollPane(mainPad);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }

    private void showDownloadDialog(String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Descarga simulada");
        alert.setHeaderText("📥 " + title);
        alert.setContentText("En producción, aquí se generaría un enlace temporal firmado (signed URL) único para tu usuario. La descarga comenzaría automáticamente.");
        alert.showAndWait();
    }
}
