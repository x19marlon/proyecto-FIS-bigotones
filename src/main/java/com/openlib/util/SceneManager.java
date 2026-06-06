package com.openlib.util;

import com.openlib.view.factory.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private final ViewFactory viewFactory = new OpenLibViewFactory();

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

    private void setScene(String viewType) {
        Scene scene = viewFactory.createView(viewType);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.requestFocus();
    }

    // ---- Navigation methods using Factory ----

    public void showLogin() { setScene("LOGIN"); }
    public void showRegister() { setScene("REGISTER"); }
    public void showBuyerDashboard() { setScene("BUYER_DASHBOARD"); }
    public void showProfile() { setScene("PROFILE"); }
    public void showCart() { setScene("CART"); }
    public void showCheckout() { setScene("CHECKOUT"); }
    public void showOrderHistory() { setScene("ORDER_HISTORY"); }
    public void showLibrary() { setScene("LIBRARY"); }
    public void showAdminDashboard() { setScene("ADMIN_DASHBOARD"); }
    public void showAdminBooks() { setScene("ADMIN_BOOKS"); }
    public void showAdminUsers() { setScene("ADMIN_USERS"); }
    public void showAdminOrders() { setScene("ADMIN_ORDERS"); }
}
