package com.openlib.view;

import com.openlib.controller.AdminController;
import com.openlib.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminUsersView {

    private final AdminController controller = new AdminController();
    private TableView<User> table;
    private ObservableList<User> userData;

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
        Button btnBooks = ViewHelper.sidebarBtn("📚  Libros",    false, controller::goToBooks);
        Button btnUsers = ViewHelper.sidebarBtn("👥  Usuarios",  true,  controller::goToUsers);
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
        content.getStyleClass().add("pane-root");

        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Gestión de Usuarios");
        title.getStyleClass().add("topbar-title");
        topbar.getChildren().add(title);

        VBox inner = new VBox(20);
        inner.setPadding(new Insets(24));
        VBox.setVgrow(inner, Priority.ALWAYS);

        // Stats row
        HBox statsRow = new HBox(16);
        int buyers = (int) controller.getAllUsers().stream()
                .filter(u -> "BUYER".equals(u.getRole())).count();
        int admins = (int) controller.getAllUsers().stream()
                .filter(u -> "ADMIN".equals(u.getRole())).count();

        statsRow.getChildren().addAll(
                miniStat("Total usuarios", String.valueOf(controller.getAllUsers().size())),
                miniStat("Buyers", String.valueOf(buyers)),
                miniStat("Admins", String.valueOf(admins)));

        // Table
        buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        inner.getChildren().addAll(statsRow, table);

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

        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<User, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Correo");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setMaxWidth(100);
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) { setGraphic(null); return; }
                Label badge = new Label(role);
                badge.getStyleClass().add("ADMIN".equals(role) ? "badge-warn" : "badge");
                setGraphic(badge);
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setMaxWidth(110);
        Long currentUserId = controller.getCurrentUserId();
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button del = new Button("Eliminar");
            {
                del.getStyleClass().add("btn-danger");
                del.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user.getId().equals(currentUserId)) {
                        showAlert("No puedes eliminarte a ti mismo.");
                        return;
                    }
                    controller.deleteUser(user.getId());
                    refreshTable();
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : del);
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, roleCol, actionCol);
        userData = FXCollections.observableArrayList(controller.getAllUsers());
        table.setItems(userData);
    }

    private void refreshTable() {
        userData.setAll(controller.getAllUsers());
    }

    private VBox miniStat(String label, String value) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(160);
        Label v = new Label(value);
        v.getStyleClass().add("stat-number");
        Label l = new Label(label);
        l.getStyleClass().add("stat-label");
        card.getChildren().addAll(v, l);
        return card;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Atención");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
