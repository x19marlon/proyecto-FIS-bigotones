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
    private ComboBox<String> sortCombo;
    private ComboBox<Integer> pageSizeCombo;
    private HBox paginationFooter;
    
    private int currentPage = 1;
    private int pageSize = 10;
    private List<Book> cachedBooks = new java.util.ArrayList<>();

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

        // Sort Combo
        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Más recientes", "Título (A-Z)", "Título (Z-A)");
        sortCombo.setValue("Más recientes");
        sortCombo.getStyleClass().add("combo-field");
        sortCombo.setPrefWidth(180);
        sortCombo.setOnAction(e -> applyFilters());

        controlBar.getChildren().addAll(searchField, chips, ViewHelper.spacer(), sortCombo, categoryCombo);

        searchField.setOnAction(e -> applyFilters());


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

        // Pagination Footer
        paginationFooter = new HBox();
        paginationFooter.getStyleClass().add("pagination-container");

        content.getChildren().addAll(hero, controlBar, scroll, paginationFooter);
        applyFilters();
        return content;
    }

    private void applyFilters() {
        currentPage = 1;
        refreshBooks(searchField.getText(), categoryCombo.getValue());
    }

    private void refreshBooks(String query, String category) {
        try {
            cachedBooks = new java.util.ArrayList<>(controller.getBooks(query, category));
            
            // Sorting logic (Frontend)
            String sort = sortCombo.getValue();
            if (sort != null) {
                if (sort.contains("Título (A-Z)")) {
                    cachedBooks.sort((a, b) -> {
                        String t1 = a.getTitle() == null ? "" : a.getTitle();
                        String t2 = b.getTitle() == null ? "" : b.getTitle();
                        return t1.compareToIgnoreCase(t2);
                    });
                } else if (sort.contains("Título (Z-A)")) {
                    cachedBooks.sort((a, b) -> {
                        String t1 = a.getTitle() == null ? "" : a.getTitle();
                        String t2 = b.getTitle() == null ? "" : b.getTitle();
                        return t2.compareToIgnoreCase(t1);
                    });
                }
            }

            renderCurrentPage();
        } catch (Exception e) {
            e.printStackTrace(); // Crucial for debugging
            booksGrid.getChildren().clear();
            booksGrid.getChildren().add(ViewHelper.stateView("⚠️", 
                "Error al cargar el catálogo", 
                "Hubo un problema inesperado. Por favor intenta de nuevo."));
            paginationFooter.setVisible(false);
        }
    }

    private void renderCurrentPage() {
        booksGrid.getChildren().clear();
        
        if (cachedBooks.isEmpty()) {
            booksGrid.getChildren().add(ViewHelper.stateView("🔍", 
                "No encontramos resultados", 
                "Intenta ajustar tus filtros o buscar algo diferente."));
            paginationFooter.setVisible(false);
            return;
        }

        paginationFooter.setVisible(true);
        
        int total = cachedBooks.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        // Safety check for current page
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Book> pageItems = cachedBooks.subList(fromIndex, toIndex);

        for (Book book : pageItems) {
            VBox card = ViewHelper.productCard(
                    book,
                    b -> {
                        controller.addToCart(b);
                        SceneManager.getInstance().showBuyerDashboard();
                    },
                    () -> showBookDetail(book)
            );
            booksGrid.getChildren().add(card);
        }

        updatePaginationUI(totalPages, total, fromIndex + 1, toIndex);
    }

    private void updatePaginationUI(int totalPages, int totalResults, int start, int end) {
        paginationFooter.getChildren().clear();

        Label resultsLbl = new Label(String.format("Mostrando %d–%d de %d libros", start, end, totalResults));
        resultsLbl.getStyleClass().add("results-counter");

        Button btnPrev = ViewHelper.paginationBtn("← Anterior", currentPage <= 1, () -> {
            currentPage--;
            renderCurrentPage();
        });

        Label pageInfo = new Label(String.format("Página %d de %d", currentPage, totalPages));
        pageInfo.getStyleClass().add("pagination-info");

        Button btnNext = ViewHelper.paginationBtn("Siguiente →", currentPage >= totalPages, () -> {
            currentPage++;
            renderCurrentPage();
        });

        Label sizeLbl = new Label("Libros por página:");
        sizeLbl.getStyleClass().add("results-counter");
        
        pageSizeCombo = new ComboBox<>();
        pageSizeCombo.getItems().addAll(5, 10, 20, 30);
        pageSizeCombo.setValue(pageSize);
        pageSizeCombo.getStyleClass().add("combo-field");
        pageSizeCombo.setPrefWidth(80);
        pageSizeCombo.setOnAction(e -> {
            pageSize = pageSizeCombo.getValue();
            currentPage = 1;
            renderCurrentPage();
        });

        paginationFooter.getChildren().addAll(
                resultsLbl, ViewHelper.spacer(), 
                btnPrev, pageInfo, btnNext, 
                ViewHelper.spacer(), 
                sizeLbl, pageSizeCombo
        );
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
        String categoryStr = book.getCategory() == null ? "GENERAL" : book.getCategory().toUpperCase();
        Label cat = new Label(categoryStr);
        cat.getStyleClass().add("product-badge");

        // Status badge
        if (book.getStatus() != null && !"APROBADO".equals(book.getStatus())) {
            Label statusBadge = new Label(book.getStatus());
            statusBadge.getStyleClass().add("badge-warn");
            HBox badges = new HBox(6, cat, statusBadge);
            info.getChildren().add(badges);
        } else {
            info.getChildren().add(cat);
        }

        String titleStr = book.getTitle() == null ? "Sin título" : book.getTitle();
        Label title = new Label(titleStr);
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
