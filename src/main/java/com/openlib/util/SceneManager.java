package com.openlib.util;

import com.openlib.view.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("OpenLib Market");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setMaximized(true);
    }

    public Stage getStage() { return primaryStage; }

    private void setScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ---- Navigation methods ----

    public void showLogin() {
        setScene(new LoginView().buildScene());
    }

    public void showRegister() {
        setScene(new RegisterView().buildScene());
    }

    public void showBuyerDashboard() {
        setScene(new BuyerDashboardView().buildScene());
    }

    public void showCart() {
        setScene(new CartView().buildScene());
    }

    public void showCheckout() {
        setScene(new CheckoutView().buildScene());
    }

    public void showLibrary() {
        setScene(new LibraryView().buildScene());
    }

    public void showAdminDashboard() {
        setScene(new AdminDashboardView().buildScene());
    }

    public void showAdminBooks() {
        setScene(new AdminBooksView().buildScene());
    }

    public void showAdminUsers() {
        setScene(new AdminUsersView().buildScene());
    }

    public void showAdminOrders() {
        setScene(new AdminOrdersView().buildScene());
    }
}
