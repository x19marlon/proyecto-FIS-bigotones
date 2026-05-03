package com.openlib.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    /** Creates the star rating label */
    public static Label starRating(double rating) {
        int full = (int) rating;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        sb.append(String.format(" %.1f", rating));
        Label lbl = new Label(sb.toString());
        lbl.setStyle("-fx-text-fill: #E3B341; -fx-font-size: 12px;");
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

    /** Creates an info row label + value */
    public static HBox infoRow(String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8B949E; -fx-min-width: 120px; -fx-font-size: 13px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #F0F6FC; -fx-font-size: 13px;");
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
}
