package com.openlib.view;

import com.openlib.controller.AdminController;
import com.openlib.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminBooksView {

    private final AdminController controller = new AdminController();
    private TableView<Book> table;
    private ObservableList<Book> bookData;

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

        Button btnDash  = ViewHelper.sidebarBtn("📊  Dashboard", false, controller::goToDashboard);
        Button btnBooks = ViewHelper.sidebarBtn("📚  Libros",    true,  controller::goToBooks);
        Button btnUsers = ViewHelper.sidebarBtn("👥  Usuarios",  false, controller::goToUsers);
        Button btnOrders= ViewHelper.sidebarBtn("🧾  Pedidos",   false, controller::goToOrders);

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
        content.setStyle("-fx-background-color: #0D1117;");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Libros");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox inner = new VBox(20);
        inner.setPadding(new Insets(24));
        VBox.setVgrow(inner, Priority.ALWAYS);

        // Add book form
        VBox addCard = ViewHelper.card(20);
        Label addTitle = new Label("➕ Agregar nuevo libro");
        addTitle.getStyleClass().add("label-h2");

        HBox row1 = new HBox(12);
        TextField titleField  = inputField("Título *", 200);
        TextField authorField = inputField("Autor *", 200);
        TextField isbnField   = inputField("ISBN", 140);
        TextField catField    = inputField("Categoría *", 140);
        row1.getChildren().addAll(
                labeled("Título", titleField),
                labeled("Autor", authorField),
                labeled("ISBN", isbnField),
                labeled("Categoría", catField));

        TextField descField = new TextField();
        descField.setPromptText("Descripción");
        descField.getStyleClass().add("input-field");
        descField.setMaxWidth(Double.MAX_VALUE);

        Label errLbl = new Label();
        errLbl.getStyleClass().add("alert-error");
        errLbl.setVisible(false);
        errLbl.setManaged(false);

        Label successLbl = new Label("✓ Libro agregado correctamente.");
        successLbl.getStyleClass().add("alert-success");
        successLbl.setVisible(false);
        successLbl.setManaged(false);

        Button addBtn = new Button("Agregar libro");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> {
            String err = controller.addBook(
                    titleField.getText(), authorField.getText(),
                    isbnField.getText(), catField.getText(), descField.getText());
            if (err != null) {
                errLbl.setText(err);
                errLbl.setVisible(true); errLbl.setManaged(true);
                successLbl.setVisible(false); successLbl.setManaged(false);
            } else {
                titleField.clear(); authorField.clear();
                isbnField.clear(); catField.clear(); descField.clear();
                errLbl.setVisible(false); errLbl.setManaged(false);
                successLbl.setVisible(true); successLbl.setManaged(true);
                refreshTable();
            }
        });

        addCard.getChildren().addAll(addTitle, row1, labeled("Descripción", descField),
                errLbl, successLbl, addBtn);

        // Table
        Label tableTitle = new Label("Catálogo actual");
        tableTitle.getStyleClass().add("label-h2");

        buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        inner.getChildren().addAll(addCard, tableTitle, table);

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane-transparent");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(topbar, scroll);
        return content;
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(50);

        TableColumn<Book, String> titleCol = new TableColumn<>("Título");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Autor");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> catCol = new TableColumn<>("Categoría");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Book, Integer> dlCol = new TableColumn<>("Descargas");
        dlCol.setCellValueFactory(new PropertyValueFactory<>("downloads"));
        dlCol.setMaxWidth(100);

        TableColumn<Book, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setMaxWidth(100);
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button del = new Button("Eliminar");
            {
                del.getStyleClass().add("btn-danger");
                del.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    controller.deleteBook(book.getId());
                    refreshTable();
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : del);
            }
        });

        table.getColumns().addAll(idCol, titleCol, authorCol, catCol, dlCol, actionCol);
        bookData = FXCollections.observableArrayList(controller.getAllBooks());
        table.setItems(bookData);
        table.setPrefHeight(300);
    }

    private void refreshTable() {
        bookData.setAll(controller.getAllBooks());
    }

    private TextField inputField(String prompt, double width) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.getStyleClass().add("input-field");
        f.setPrefWidth(width);
        return f;
    }

    private VBox labeled(String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl);
        l.getStyleClass().add("input-label");
        VBox box = new VBox(4, l, field);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}
