package com.openlib.view.factory;

import com.openlib.view.*;
import javafx.scene.Scene;

public class OpenLibViewFactory implements ViewFactory {

    @Override
    public Scene createView(String viewType) {
        return switch (viewType.toUpperCase()) {
            case "LOGIN" -> new LoginView().buildScene();
            case "REGISTER" -> new RegisterView().buildScene();
            case "BUYER_DASHBOARD" -> new BuyerDashboardView().buildScene();
            case "PROFILE" -> new ProfileView().buildScene();
            case "CART" -> new CartView().buildScene();
            case "CHECKOUT" -> new CheckoutView().buildScene();
            case "ORDER_HISTORY" -> new OrderHistoryView().buildScene();
            case "LIBRARY" -> new LibraryView().buildScene();
            case "ADMIN_DASHBOARD" -> new AdminDashboardView().buildScene();
            case "ADMIN_BOOKS" -> new AdminBooksView().buildScene();
            case "ADMIN_USERS" -> new AdminUsersView().buildScene();
            case "ADMIN_ORDERS" -> new AdminOrdersView().buildScene();
            default -> throw new IllegalArgumentException("Unknown view type: " + viewType);
        };
    }
}
