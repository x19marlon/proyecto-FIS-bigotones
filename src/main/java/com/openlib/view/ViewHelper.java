package com.openlib.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.openlib.model.Book;
import com.openlib.util.DataStore;
import java.util.List;
import java.util.function.Consumer;

public class ViewHelper {

    public static final String CSS_PATH =
            ViewHelper.class.getResource("/css/styles.css").toExternalForm();

    // ==================== ECOMMERCE HEADER ====================

    /**
     * Builds the ecommerce header bar with logo, search, and user zone.
     * @param searchField The search TextField to embed (can be null for non-catalog views)
     * @param onSearch Runnable to execute when search is triggered
     * @param cartCount Current cart item count
     * @param onCartClick Action when cart button is clicked
     */
    public static HBox buildEcommerceHeader(TextField searchField, Runnable onSearch,
                                             int cartCount, Runnable onCartClick) {
        HBox header = new HBox(16);
        header.getStyleClass().add("ecom-header");
        header.setAlignment(Pos.CENTER_LEFT);

        // Logo section
        VBox logoBox = new VBox(0);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setMinWidth(130);
        Label logo = new Label("📚 OpenLib");
        logo.getStyleClass().add("ecom-header-logo");
        Label logoSub = new Label("Market");
        logoSub.getStyleClass().add("ecom-header-logo-sub");
        logoBox.getChildren().addAll(logo, logoSub);

        // Search bar
        HBox searchBox = new HBox(0);
        searchBox.getStyleClass().add("ecom-search-container");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        if (searchField != null) {
            searchField.getStyleClass().clear();
            searchField.getStyleClass().add("ecom-search-field");
            searchField.setPromptText("Buscar libros, autores, ISBN...");
            HBox.setHgrow(searchField, Priority.ALWAYS);

            Button searchBtn = new Button("🔍");
            searchBtn.getStyleClass().add("ecom-search-btn");
            searchBtn.setOnAction(e -> { if (onSearch != null) onSearch.run(); });

            searchBox.getChildren().addAll(searchField, searchBtn);
        } else {
            // Placeholder search for non-catalog views
            TextField placeholder = new TextField();
            placeholder.getStyleClass().add("ecom-search-field");
            placeholder.setPromptText("Buscar libros, autores, ISBN...");
            HBox.setHgrow(placeholder, Priority.ALWAYS);
            
            Button searchBtn = new Button("🔍");
            searchBtn.getStyleClass().add("ecom-search-btn");
            searchBtn.setOnAction(e -> {
                // Navigate to catalog with search query
                com.openlib.util.SceneManager.getInstance().showBuyerDashboard();
            });

            searchBox.getChildren().addAll(placeholder, searchBtn);
        }

        // User zone
        HBox userZone = new HBox(12);
        userZone.getStyleClass().add("ecom-user-zone");
        userZone.setAlignment(Pos.CENTER_RIGHT);
        userZone.setMinWidth(Region.USE_PREF_SIZE);

        String userName = "Usuario";
        try {
            var user = DataStore.getInstance().getCurrentUser();
            if (user != null && user.getName() != null) {
                userName = user.getName().split(" ")[0];
            }
        } catch (Exception ignored) {}

        // My Account Dropdown
        MenuButton accountMenu = new MenuButton("Hola, " + userName + "\nMi Cuenta ▾");
        accountMenu.getStyleClass().add("ecom-user-menu");
        
        MenuItem profile = new MenuItem("👤 Mi Perfil");
        profile.setOnAction(e -> {
            // SceneManager.getInstance().showProfile(); 
            // For now, let's just show a simple profile alert or navigate if we implement it
            com.openlib.util.SceneManager.getInstance().showProfile();
        });

        MenuItem orders = new MenuItem("🧾 Mis Pedidos");
        orders.setOnAction(e -> com.openlib.util.SceneManager.getInstance().showOrderHistory());

        MenuItem library = new MenuItem("📖 Mi Biblioteca");
        library.setOnAction(e -> com.openlib.util.SceneManager.getInstance().showLibrary());

        MenuItem logout = new MenuItem("⬅ Cerrar sesión");
        logout.setOnAction(e -> new com.openlib.controller.AuthController().logout());

        accountMenu.getItems().addAll(profile, orders, library, new SeparatorMenuItem(), logout);

        // Cart button
        Button cartBtn = new Button("🛒 Carrito");
        cartBtn.getStyleClass().add("ecom-header-action");
        cartBtn.setOnAction(e -> { if (onCartClick != null) onCartClick.run(); });

        if (cartCount > 0) {
            Label badge = new Label(String.valueOf(cartCount));
            badge.getStyleClass().add("ecom-cart-badge");
            StackPane cartContainer = new StackPane(cartBtn, badge);
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            badge.setTranslateX(8);
            badge.setTranslateY(-8);
            userZone.getChildren().addAll(accountMenu, cartContainer);
        } else {
            userZone.getChildren().addAll(accountMenu, cartBtn);
        }

        header.getChildren().addAll(logoBox, searchBox, userZone);
        return header;
    }

    // ==================== CATEGORY NAV BAR ====================

    /**
     * Builds a secondary nav bar with category chips (Amazon-style sub-header).
     */
    public static HBox buildCategoryNavBar(List<String> categories, String activeCat,
                                            Consumer<String> onCategorySelect) {
        HBox bar = new HBox(0);
        bar.getStyleClass().add("category-nav-bar");
        bar.setAlignment(Pos.CENTER_LEFT);



        for (String cat : categories) {
            Label chip = new Label(cat);
            chip.getStyleClass().add("category-nav-chip");
            if (cat.equals(activeCat)) {
                chip.getStyleClass().add("category-nav-chip-active");
            }
            chip.setOnMouseClicked(e -> onCategorySelect.accept(cat));
            bar.getChildren().add(chip);
        }

        return bar;
    }

    // ==================== BOOK COVER ====================

    /** Creates a colored book cover placeholder */
    public static StackPane bookCover(String hexColor, String title, boolean large) {
        String styleClass = large ? "book-cover-lg" : "book-cover";
        StackPane cover = new StackPane();
        cover.getStyleClass().add(styleClass);
        cover.setStyle("-fx-background-color: " + hexColor + ";");

        // Spine line
        Rectangle spine = new Rectangle(4, large ? 140 : 110);
        spine.setFill(Color.web("#000000", 0.25));
        StackPane.setAlignment(spine, Pos.CENTER_LEFT);

        // Title letters
        Label lbl = new Label(initials(title));
        lbl.setStyle("-fx-font-size: " + (large ? "20" : "16") + "px; -fx-font-weight: bold; "
                + "-fx-text-fill: rgba(255,255,255,0.85);");

        cover.getChildren().addAll(spine, lbl);
        return cover;
    }

    private static String initials(String title) {
        if (title == null || title.isBlank()) return "?";
        String[] words = title.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, words.length); i++) {
            sb.append(words[i].charAt(0));
        }
        return sb.toString().toUpperCase();
    }

    // ==================== RATINGS ====================

    public static Label starRating(double rating) {
        int full = (int) rating;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        sb.append(String.format(" %.1f", rating));
        Label lbl = new Label(sb.toString());
        lbl.setStyle("-fx-text-fill: #C97B63; -fx-font-size: 12px; -fx-font-weight: bold;");
        return lbl;
    }

    /** Creates a free badge */
    public static Label freeBadge() {
        Label l = new Label("GRATIS");
        l.getStyleClass().add("badge");
        return l;
    }

    // ==================== SPACERS ====================

    /** Horizontal spacer */
    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /** Vertical spacer */
    public static Region vSpacer() {
        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    // ==================== SIDEBAR BUTTON ====================

    /** Generic nav button for sidebars */
    public static Button sidebarBtn(String text, boolean active, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        if (active) btn.getStyleClass().add("sidebar-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    // ==================== UTILITY COMPONENTS ====================

    public static HBox infoRow(String label, String value) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("label-small");
        lbl.setStyle("-fx-min-width: 120px;");
        Label val = new Label(value);
        val.getStyleClass().add("label-body");
        val.setStyle("-fx-font-weight: bold;");
        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Card pane */
    public static VBox card(double padding) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(padding));
        return card;
    }

    // ==================== PRODUCT CARD ====================

    /** Professional Ecommerce Product Card */
    public static VBox productCard(Book book, Consumer<Book> onAdd, Runnable onDetail) {
        VBox card = new VBox(0);
        card.getStyleClass().add("product-card");

        // Top: Cover area
        StackPane coverContainer = new StackPane();
        coverContainer.getStyleClass().add("product-cover-container");
        StackPane cover = bookCover(book.getCoverColor(), book.getTitle(), true);
        coverContainer.getChildren().add(cover);

        // Info area
        VBox info = new VBox(6);
        info.getStyleClass().add("product-info-container");

        info.getChildren().add(renderCategories(book));

        String titleStr = book.getTitle() == null ? "Sin título" : book.getTitle();
        Label title = new Label(titleStr);
        title.getStyleClass().add("product-title");
        title.setMinHeight(44);
        title.setWrapText(true);

        String authorStr = book.getAuthor() == null ? "Autor desconocido" : book.getAuthor();
        Label author = new Label("por " + authorStr);
        author.getStyleClass().add("product-author");

        HBox priceRow = new HBox(8);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        Label price = new Label("GRATIS");
        price.getStyleClass().add("product-price");
        priceRow.getChildren().addAll(price, spacer(), starRating(4.5));

        Button btnAdd = new Button("Agregar al carrito");
        btnAdd.getStyleClass().add("btn-accent");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setPrefHeight(36);
        btnAdd.setOnAction(e -> onAdd.accept(book));

        Button btnDetail = new Button("Ver detalle");
        btnDetail.getStyleClass().add("btn-secondary");
        btnDetail.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setOnAction(e -> onDetail.run());

        info.getChildren().addAll(title, author, priceRow, btnAdd, btnDetail);
        card.getChildren().addAll(coverContainer, info);

        return card;
    }

    // ==================== MULTI-CATEGORY RENDERING ====================

    public static HBox renderCategories(Book book) {
        HBox container = new HBox(4);
        container.setAlignment(Pos.CENTER_LEFT);

        java.util.List<String> allCats = book.getCategoriesList();
        int maxVisible = 3;
        int visibleCount = Math.min(allCats.size(), maxVisible);

        for (int i = 0; i < visibleCount; i++) {
            Label catLbl = new Label(allCats.get(i));
            catLbl.getStyleClass().add("product-badge");
            container.getChildren().add(catLbl);
        }

        if (allCats.size() > maxVisible) {
            int extra = allCats.size() - maxVisible;
            Label extraLbl = new Label("+" + extra);
            extraLbl.getStyleClass().add("badge-extra");
            container.getChildren().add(extraLbl);
        }

        // Add status badge if not approved
        if (book.getStatus() != null && !"APROBADO".equals(book.getStatus())) {
            Label statusBadge = new Label(book.getStatus());
            statusBadge.getStyleClass().add("badge-warn");
            container.getChildren().add(statusBadge);
        }

        return container;
    }

    // ==================== CHIPS ====================

    public static Label categoryChip(String text, boolean active) {
        Label l = new Label(text);
        l.getStyleClass().add("chip");
        if (active) l.getStyleClass().add("chip-active");
        return l;
    }

    // ==================== PAGINATION ====================

    /** Professional Pagination Button */
    public static Button paginationBtn(String text, boolean disabled, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("pagination-btn");
        btn.setDisable(disabled);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    // ==================== STATE VIEWS ====================

    /** Generic state view (Empty, Loading, Error) */
    public static VBox stateView(String icon, String title, String subtitle) {
        VBox box = new VBox(12);
        box.getStyleClass().add("state-container");
        
        Label lblIcon = new Label(icon);
        lblIcon.getStyleClass().add("state-icon");
        
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("label-h2");
        
        Label lblSub = new Label(subtitle);
        lblSub.getStyleClass().add("label-body");
        
        box.getChildren().addAll(lblIcon, lblTitle, lblSub);
        return box;
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(CSS_PATH);
        alert.showAndWait();
    }
}
