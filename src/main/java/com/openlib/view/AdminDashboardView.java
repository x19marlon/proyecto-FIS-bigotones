package com.openlib.view;

import com.openlib.controller.AdminController;
import com.openlib.model.Book;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboardView {

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

        Button btnDash = ViewHelper.sidebarBtn("📊  Dashboard", true, controller::goToDashboard);
        Button btnBooks = ViewHelper.sidebarBtn("📚  Libros", false, controller::goToBooks);
        Button btnUsers = ViewHelper.sidebarBtn("👥  Usuarios", false, controller::goToUsers);
        Button btnOrders = ViewHelper.sidebarBtn("🧾  Pedidos", false, controller::goToOrders);

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
        Label title = new Label("Dashboard de Métricas");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox inner = new VBox(24);
        inner.setPadding(new Insets(24));

        // Stat cards
        Label statsTitle = new Label("Resumen general");
        statsTitle.getStyleClass().add("label-h2");

        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.getChildren().addAll(
                statCard("👥", "Usuarios registrados", String.valueOf(controller.getTotalUsers()), null),
                statCard("📚", "Libros en catálogo", String.valueOf(controller.getTotalBooks()), null),
                statCard("🧾", "Pedidos realizados", String.valueOf(controller.getTotalOrders()), null));

        // Most downloaded
        Book topBook = controller.getMostDownloaded();
        VBox topCard = ViewHelper.card(20);
        Label topTitle = new Label("📈 Libro más descargado");
        topTitle.getStyleClass().add("label-h2");

        if (topBook != null) {
            HBox topRow = new HBox(16);
            topRow.setAlignment(Pos.CENTER_LEFT);
            StackPane cover = ViewHelper.bookCover(topBook.getCoverColor(), topBook.getTitle(), true);
            VBox info = new VBox(8);
            Label tLbl = new Label(topBook.getTitle());
            tLbl.getStyleClass().add("label-h2");
            Label aLbl = new Label("por " + topBook.getAuthor());
            aLbl.getStyleClass().add("label-body");
            Label dlLbl = new Label("⬇  " + topBook.getDownloads() + " descargas");
            dlLbl.getStyleClass().add("label-accent");
            Label catBadge = new Label(topBook.getCategory());
            catBadge.getStyleClass().add("badge");
            info.getChildren().addAll(tLbl, aLbl, dlLbl, catBadge);
            topRow.getChildren().addAll(cover, info);
            topCard.getChildren().addAll(topTitle, topRow);
        } else {
            topCard.getChildren().addAll(topTitle, new Label("Sin datos aún."));
        }

        // Quick actions
        Label actTitle = new Label("Acciones rápidas");
        actTitle.getStyleClass().add("label-h2");

        HBox actions = new HBox(12);
        Button goBooks = new Button("➕ Agregar libro");
        goBooks.getStyleClass().add("btn-primary");
        goBooks.setOnAction(e -> controller.goToBooks());

        Button goUsers = new Button("👥 Ver usuarios");
        goUsers.getStyleClass().add("btn-secondary");
        goUsers.setOnAction(e -> controller.goToUsers());

        Button goOrders = new Button("🧾 Ver pedidos");
        goOrders.getStyleClass().add("btn-secondary");
        goOrders.setOnAction(e -> controller.goToOrders());

        actions.getChildren().addAll(goBooks, goUsers, goOrders);

        inner.getChildren().addAll(statsTitle, statsRow, topCard, actTitle, actions);

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }

    private VBox statCard(String icon, String label, String value, String extra) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 24px;");
        Label valLbl = new Label(value);
        valLbl.getStyleClass().add("stat-number");
        Label lblLbl = new Label(label);
        lblLbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconLbl, valLbl, lblLbl);
        if (extra != null) {
            Label extraLbl = new Label(extra);
            extraLbl.getStyleClass().add("label-small");
            card.getChildren().add(extraLbl);
        }
        return card;
    }
}
