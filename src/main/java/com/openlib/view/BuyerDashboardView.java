package com.openlib.view;

import com.openlib.controller.BuyerController;
import com.openlib.model.Book;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;
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

        // Hero Section
        VBox hero = new VBox(12);
        hero.getStyleClass().add("hero-section");
        Label heroTitle = new Label("Explora nuestra biblioteca académica");
        heroTitle.getStyleClass().add("hero-title");
        Label heroSub = new Label("Encuentra los mejores recursos digitales para potenciar tu aprendizaje.");
        heroSub.getStyleClass().add("hero-subtitle");
        hero.getChildren().addAll(heroTitle, heroSub);

        // Control Bar (Filters & Search)
        HBox controlBar = new HBox(16);
        controlBar.getStyleClass().add("control-bar");

        searchField = new TextField();
        searchField.setPromptText("Buscar por título, autor o ISBN...");
        searchField.getStyleClass().add("input-field");
        searchField.setPrefWidth(400);

        HBox chips = new HBox(10);
        chips.setAlignment(Pos.CENTER_LEFT);
        String currentCat = categoryCombo == null ? "Todas" : categoryCombo.getValue();
        for (String cat : controller.getCategories()) {
            Label chip = ViewHelper.categoryChip(cat, cat.equals(currentCat));
            chip.setOnMouseClicked(e -> {
                categoryCombo.setValue(cat);
                refreshBooks(searchField.getText(), cat);
            });
            chips.getChildren().add(chip);
        }

        // Hidden combo for logic compatibility
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(controller.getCategories());
        categoryCombo.setValue("Todas");
        categoryCombo.setVisible(false);
        categoryCombo.setManaged(false);

        controlBar.getChildren().addAll(searchField, chips, ViewHelper.spacer(), categoryCombo);

        searchField.setOnAction(e -> refreshBooks(searchField.getText(), categoryCombo.getValue()));

        // Books grid
        booksGrid = new FlowPane();
        booksGrid.setHgap(30);
        booksGrid.setVgap(30);
        booksGrid.setPadding(new Insets(32));
        booksGrid.getStyleClass().add("pane-root");
        booksGrid.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(booksGrid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(hero, controlBar, scroll);
        refreshBooks("", "Todas");
        return content;
    }

    private void refreshBooks(String query, String category) {
        booksGrid.getChildren().clear();
        List<Book> books = controller.getBooks(query, category);

        if (books.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(100));
            Label icon = new Label("🔍");
            icon.setStyle("-fx-font-size: 64px;");
            Label empty = new Label("No encontramos resultados");
            empty.getStyleClass().add("label-h2");
            Label subEmpty = new Label("Intenta ajustar tus filtros o buscar algo diferente.");
            subEmpty.getStyleClass().add("label-body");
            emptyBox.getChildren().addAll(icon, empty, subEmpty);
            booksGrid.getChildren().add(emptyBox);
            return;
        }

        for (Book book : books) {
            VBox card = ViewHelper.productCard(
                    book,
                    b -> {
                        controller.addToCart(b);
                        SceneManager.getInstance().showBuyerDashboard(); // refresh to update cart badge
                    },
                    () -> showBookDetail(book)
            );
            booksGrid.getChildren().add(card);
        }
    }

    private void showBookDetail(Book book) {
        // Simple detail overlay
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle del Libro");
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(ViewHelper.CSS_PATH);
        pane.getStyleClass().add("pane-root");
        
        HBox content = new HBox(30);
        content.setPadding(new Insets(30));
        content.setPrefWidth(700);

        StackPane cover = ViewHelper.bookCover(book.getCoverColor(), book.getTitle(), true);
        cover.setScaleX(1.5);
        cover.setScaleY(1.5);
        StackPane coverWrapper = new StackPane(cover);
        coverWrapper.setPadding(new Insets(40));

        VBox info = new VBox(15);
        Label title = new Label(book.getTitle());
        title.getStyleClass().add("label-h1");
        title.setWrapText(true);

        Label author = new Label("Autor: " + book.getAuthor());
        author.getStyleClass().add("label-h2");

        Label desc = new Label("Esta es una descripción detallada del libro académico. "
                + "Aquí se incluirían los objetivos de aprendizaje, el resumen del contenido y "
                + "la relevancia del material para el curso.");
        desc.getStyleClass().add("label-body");
        desc.setWrapText(true);

        Label isbn = new Label("ISBN: " + (book.getIsbn() != null ? book.getIsbn() : "N/A"));
        isbn.getStyleClass().add("label-small");

        Button addBtn = new Button("Comprar ahora - GRATIS");
        addBtn.getStyleClass().add("btn-accent");
        addBtn.setPrefWidth(300);
        addBtn.setPrefHeight(50);
        addBtn.setOnAction(e -> {
            controller.addToCart(book);
            dialog.close();
            SceneManager.getInstance().showBuyerDashboard();
        });

        info.getChildren().addAll(title, author, desc, isbn, ViewHelper.vSpacer(), addBtn);
        content.getChildren().addAll(coverWrapper, info);

        pane.setContent(content);
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
