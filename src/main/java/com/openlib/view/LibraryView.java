package com.openlib.view;

import com.openlib.controller.BuyerController;
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
            emptyLbl.getStyleClass().add("label-h2");
            Label emptyHint = new Label("Explora el catálogo y agrega libros a tu carrito.");
            emptyHint.getStyleClass().add("label-body");
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
                orderCard.setMaxWidth(400);

                HBox orderHeader = new HBox(12);
                orderHeader.setAlignment(Pos.CENTER_LEFT);
                Label orderIdLbl = new Label("Orden #" + order.getId());
                orderIdLbl.getStyleClass().add("label-title");
                Label dateLbl = new Label(order.getDate().format(fmt));
                dateLbl.getStyleClass().add("label-small");
                Label statusBadge = new Label("✓ " + order.getStatus());
                statusBadge.getStyleClass().add("badge");
                Region spc = ViewHelper.spacer();
                Label totalLbl = new Label(String.format("$%.2f", order.getTotal()));
                totalLbl.getStyleClass().add("label-accent");
                orderHeader.getChildren().addAll(orderIdLbl, dateLbl, spc, statusBadge, totalLbl);

                Separator sep = new Separator();
                sep.getStyleClass().add("separator-light");

                FlowPane booksFlow = new FlowPane(12, 12);
                for (com.openlib.model.OrderItem item : order.getItems()) {
                    VBox assetBox = new VBox(10);
                    assetBox.setAlignment(Pos.TOP_LEFT);
                    assetBox.setStyle("-fx-background-color: #EDE3D2; -fx-background-radius: 12; -fx-padding: 16; -fx-pref-width: 200;");

                    StackPane cover = ViewHelper.bookCover(item.getBook().getCoverColor(),
                            item.getBook().getTitle(), false);

                    Label tLbl = new Label(item.getBook().getTitle());
                    tLbl.getStyleClass().add("label-title");
                    tLbl.setWrapText(true);
                    tLbl.setMaxWidth(170);

                    Button dlBtn = new Button("⬇ Descargar PDF");
                    dlBtn.getStyleClass().add("btn-accent");
                    dlBtn.setMaxWidth(Double.MAX_VALUE);
                    dlBtn.setOnAction(ev -> showDownloadDialog(item.getBook().getTitle()));

                    assetBox.getChildren().addAll(cover, tLbl, ViewHelper.vSpacer(), dlBtn);
                    booksFlow.getChildren().add(assetBox);
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
