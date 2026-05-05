package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.Book;
import com.openlib.util.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class BuyerDashboardView {

    private final BuyerController controller = new BuyerController();
    private FlowPane booksGrid;
    private TextField searchField;
    private ComboBox<String> categoryCombo;

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

        Button btnCatalog = ViewHelper.sidebarBtn("🏠  Catálogo", true, controller::goToCatalog);
        Button btnCart    = ViewHelper.sidebarBtn(cartLabel, false, controller::goToCart);
        Button btnLibrary = ViewHelper.sidebarBtn("📖  Mi Biblioteca", false, controller::goToLibrary);

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
        content.getStyleClass().add("pane-root");

        // Top bar
        HBox topbar = new HBox(12);
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Catálogo de Libros");
        title.getStyleClass().add("topbar-title");

        searchField = new TextField();
        searchField.setPromptText("🔍  Buscar por título, autor, ISBN...");
        searchField.getStyleClass().add("input-field");
        searchField.setPrefWidth(300);

        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(controller.getCategories());
        categoryCombo.setValue("Todas");
        categoryCombo.getStyleClass().add("combo-field");
        categoryCombo.setPrefWidth(150);

        Button searchBtn = new Button("Buscar");
        searchBtn.getStyleClass().add("btn-secondary");

        Runnable doSearch = () -> refreshBooks(
                searchField.getText(), categoryCombo.getValue());
        searchBtn.setOnAction(e -> doSearch.run());
        searchField.setOnAction(e -> doSearch.run());
        categoryCombo.setOnAction(e -> doSearch.run());

        topbar.getChildren().addAll(title, ViewHelper.spacer(),
                searchField, categoryCombo, searchBtn);

        // Books grid
        booksGrid = new FlowPane();
        booksGrid.setHgap(20);
        booksGrid.setVgap(20);
        booksGrid.setPadding(new Insets(32));
        // Use css class
        booksGrid.getStyleClass().add("pane-root");

        ScrollPane scroll = new ScrollPane(booksGrid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        refreshBooks("", "Todas");
        return content;
    }

    private void refreshBooks(String query, String category) {
        booksGrid.getChildren().clear();
        List<Book> books = controller.getBooks(query, category);

        if (books.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            Label icon = new Label("📚");
            icon.setStyle("-fx-font-size: 48px;");
            Label empty = new Label("No se encontraron libros.");
            empty.getStyleClass().add("label-h2");
            Label subEmpty = new Label("Intenta con otros términos de búsqueda o categoría.");
            subEmpty.getStyleClass().add("label-body");
            emptyBox.getChildren().addAll(icon, empty, subEmpty);
            booksGrid.getChildren().add(emptyBox);
            return;
        }

        for (Book book : books) {
            booksGrid.getChildren().add(buildBookCard(book));
        }
    }

    private VBox buildBookCard(Book book) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setMaxWidth(220);

        StackPane cover = ViewHelper.bookCover(book.getCoverColor(), book.getTitle(), false);

        Label titleLbl = new Label(book.getTitle());
        titleLbl.getStyleClass().add("label-title");
        titleLbl.setWrapText(true);

        Label authorLbl = new Label(book.getAuthor());
        authorLbl.getStyleClass().add("label-small");

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label catBadge = new Label(book.getCategory());
        catBadge.getStyleClass().add("badge");

        Label dlLbl = new Label("⬇ " + book.getDownloads());
        dlLbl.getStyleClass().add("label-small");
        meta.getChildren().addAll(catBadge, dlLbl);

        Label freeLbl = ViewHelper.freeBadge();

        Button addBtn = new Button("+ Agregar al carrito");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> {
            controller.addToCart(book);
            addBtn.setText("✓ Agregado");
            addBtn.setDisable(true);
        });

        card.getChildren().addAll(cover, titleLbl, authorLbl, meta, freeLbl, addBtn);
        return card;
    }
}
