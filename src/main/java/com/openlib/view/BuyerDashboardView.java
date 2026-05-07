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
    private HBox categoryNavBar;
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

        // Header at top
        searchField = new TextField();
        HBox header = ViewHelper.buildEcommerceHeader(
                searchField,
                this::applyFilters,
                controller.getCartCount(),
                controller::goToCart
        );
        searchField.setOnAction(e -> applyFilters());

        // Category nav bar below header
        categoryCombo = new ComboBox<>();
        categoryCombo.setValue("Todas");
        categoryCombo.setVisible(false);
        categoryCombo.setManaged(false);

        categoryNavBar = buildCategoryNav();

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(header, categoryNavBar);
        root.setTop(topSection);

        // Main content (No sidebar)
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1200, 780);
        scene.getStylesheets().add(ViewHelper.CSS_PATH);
        return scene;
    }

    private HBox buildCategoryNav() {
        List<String> categories = controller.getCategories();
        categoryCombo.getItems().setAll(categories);
        String currentCat = categoryCombo.getValue() == null ? "Todas" : categoryCombo.getValue();

        return ViewHelper.buildCategoryNavBar(categories, currentCat, cat -> {
            categoryCombo.setValue(cat);
            applyFilters();
        });
    }

    private VBox buildContent() {
        VBox content = new VBox(0);
        content.getStyleClass().add("pane-root");

        // Compact control bar
        HBox controlBar = new HBox(12);
        controlBar.getStyleClass().add("control-bar");
        controlBar.setAlignment(Pos.CENTER_LEFT);

        Label resultsTitle = new Label("📚 Catálogo de libros");
        resultsTitle.getStyleClass().add("label-title");

        Region spacer = ViewHelper.spacer();

        // Sort Combo
        Label sortLabel = new Label("Ordenar:");
        sortLabel.getStyleClass().add("label-small");
        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Más recientes", "Título (A-Z)", "Título (Z-A)");
        sortCombo.setValue("Más recientes");
        sortCombo.getStyleClass().add("combo-field");
        sortCombo.setPrefWidth(160);
        sortCombo.setOnAction(e -> applyFilters());

        controlBar.getChildren().addAll(resultsTitle, spacer, sortLabel, sortCombo, categoryCombo);

        // Books grid
        booksGrid = new FlowPane();
        booksGrid.setHgap(24);
        booksGrid.setVgap(24);
        booksGrid.setPadding(new Insets(24));
        booksGrid.getStyleClass().add("pane-root");
        booksGrid.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(booksGrid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Pagination Footer
        paginationFooter = new HBox();
        paginationFooter.getStyleClass().add("pagination-container");

        content.getChildren().addAll(controlBar, scroll, paginationFooter);
        applyFilters();
        return content;
    }

    private void applyFilters() {
        currentPage = 1;
        // Refresh category nav
        VBox topSection = (VBox) categoryNavBar.getParent();
        if (topSection != null) {
            int idx = topSection.getChildren().indexOf(categoryNavBar);
            categoryNavBar = buildCategoryNav();
            if (idx >= 0) {
                topSection.getChildren().set(idx, categoryNavBar);
            }
        }
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
            e.printStackTrace();
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

        Label sizeLbl = new Label("Por página:");
        sizeLbl.getStyleClass().add("results-counter");
        
        pageSizeCombo = new ComboBox<>();
        pageSizeCombo.getItems().addAll(5, 10, 20, 30);
        pageSizeCombo.setValue(pageSize);
        pageSizeCombo.getStyleClass().add("combo-field");
        pageSizeCombo.setPrefWidth(70);
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
        info.getChildren().add(ViewHelper.renderCategories(book));

        String titleStr = book.getTitle() == null ? "Sin título" : book.getTitle();
        Label title = new Label(titleStr);
        title.getStyleClass().add("label-h1");
        title.setWrapText(true);

        Label author = new Label("Autor: " + book.getAuthor());
        author.getStyleClass().add("label-h2");

        Label desc = new Label(book.getDescription() != null ? book.getDescription() 
                : "Material educativo de alta calidad disponible para descarga inmediata.");
        desc.getStyleClass().add("label-body");
        desc.setWrapText(true);

        Label isbn = new Label("ISBN: " + (book.getIsbn() != null ? book.getIsbn() : "N/A"));
        isbn.getStyleClass().add("label-small");

        Button addBtn = new Button("Comprar ahora - GRATIS");
        addBtn.getStyleClass().add("btn-accent");
        addBtn.setPrefWidth(300);
        addBtn.setPrefHeight(48);
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
