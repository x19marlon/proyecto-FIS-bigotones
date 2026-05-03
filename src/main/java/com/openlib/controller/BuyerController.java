package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.List;

public class BuyerController {

    private final DataStore store = DataStore.getInstance();

    // ---- Catalog ----

    public List<Book> getBooks(String query, String category) {
        return store.searchBooks(query, category);
    }

    public List<String> getCategories() {
        return store.getCategories();
    }

    // ---- Cart ----

    public void addToCart(Book book) {
        store.addToCart(book);
    }

    public void removeFromCart(Long bookId) {
        store.removeFromCart(bookId);
    }

    public List<CartItem> getCart() {
        return store.getCart();
    }

    public double getCartTotal() {
        return store.getCartTotal();
    }

    public int getCartCount() {
        return store.getCart().size();
    }

    // ---- Checkout ----

    public Order placeOrder() {
        return store.placeOrder();
    }

    // ---- Library ----

    public List<Order> getMyOrders() {
        return store.getOrdersByUser(store.getCurrentUser());
    }

    // ---- Navigation ----

    public void goToCatalog()   { SceneManager.getInstance().showBuyerDashboard(); }
    public void goToCart()      { SceneManager.getInstance().showCart(); }
    public void goToCheckout()  { SceneManager.getInstance().showCheckout(); }
    public void goToLibrary()   { SceneManager.getInstance().showLibrary(); }
    public void logout()        { new AuthController().logout(); }
}
