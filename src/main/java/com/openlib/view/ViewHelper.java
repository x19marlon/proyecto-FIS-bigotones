package com.openlib.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.openlib.model.Book;
import java.util.function.Consumer;

public class ViewHelper {

    public static final String CSS_PATH =
            ViewHelper.class.getResource("/css/styles.css").toExternalForm();

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

    public static Label starRating(double rating) {
        int full = (int) rating;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        sb.append(String.format(" %.1f", rating));
        Label lbl = new Label(sb.toString());
        lbl.setStyle("-fx-text-fill: #C97B63; -fx-font-size: 13px; -fx-font-weight: bold;");
        return lbl;
    }

    /** Creates a free badge */
    public static Label freeBadge() {
        Label l = new Label("GRATIS");
        l.getStyleClass().add("badge");
        return l;
    }

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

    /** Generic nav button for sidebars */
    public static Button sidebarBtn(String text, boolean active, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        if (active) btn.getStyleClass().add("sidebar-btn-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

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
        VBox info = new VBox(8);
        info.getStyleClass().add("product-info-container");

        Label cat = new Label(book.getCategory().toUpperCase());
        cat.getStyleClass().add("product-badge");

        Label title = new Label(book.getTitle());
        title.getStyleClass().add("product-title");
        title.setMinHeight(50);
        title.setAlignment(Pos.TOP_LEFT);

        Label author = new Label("por " + book.getAuthor());
        author.getStyleClass().add("product-author");

        Label shortDesc = new Label("Material educativo de alta calidad disponible para descarga inmediata.");
        shortDesc.getStyleClass().add("label-small");
        shortDesc.setWrapText(true);
        shortDesc.setMaxHeight(40);

        HBox priceRow = new HBox(8);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        Label price = new Label("GRATIS");
        price.getStyleClass().add("product-price");
        priceRow.getChildren().addAll(price, spacer(), starRating(4.5));

        Button btnAdd = new Button("Agregar al carrito");
        btnAdd.getStyleClass().add("btn-accent");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setPrefHeight(40);
        btnAdd.setOnAction(e -> onAdd.accept(book));

        Button btnDetail = new Button("Ver detalle");
        btnDetail.getStyleClass().add("btn-secondary");
        btnDetail.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setOnAction(e -> onDetail.run());

        info.getChildren().addAll(cat, title, author, shortDesc, priceRow, btnAdd, btnDetail);
        card.getChildren().addAll(coverContainer, info);

        return card;
    }

    public static Label categoryChip(String text, boolean active) {
        Label l = new Label(text);
        l.getStyleClass().add("chip");
        if (active) l.getStyleClass().add("chip-active");
        return l;
    }

    /** Professional Pagination Button */
    public static Button paginationBtn(String text, boolean disabled, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("pagination-btn");
        btn.setDisable(disabled);
        btn.setOnAction(e -> action.run());
        return btn;
    }

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
}
