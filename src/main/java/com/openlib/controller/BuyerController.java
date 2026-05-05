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
        try {
            if (store.getCurrentUser() == null) return null;
            
            List<Long> bookIds = store.getCart().stream()
                    .map(item -> item.getBook().getId())
                    .collect(java.util.stream.Collectors.toList());

            com.openlib.controller.OrderController.OrderRequest request = 
                    new com.openlib.controller.OrderController.OrderRequest();
            request.setUserId(store.getCurrentUser().getId());
            request.setBookIds(bookIds);

            Order order = com.openlib.util.ApiClient.post("/orders", request, Order.class);
            
            // Sync local memory (optional, but good for current session)
            store.clearCart();
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> getMyOrders() {
        try {
            if (store.getCurrentUser() == null) return java.util.Collections.emptyList();
            
            Order[] orders = com.openlib.util.ApiClient.get(
                    "/orders/user/" + store.getCurrentUser().getId(), 
                    Order[].class);
            return java.util.Arrays.asList(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    // ---- Navigation ----

    public void goToCatalog()   { SceneManager.getInstance().showBuyerDashboard(); }
    public void goToCart()      { SceneManager.getInstance().showCart(); }
    public void goToCheckout()  { SceneManager.getInstance().showCheckout(); }
    public void goToLibrary()   { SceneManager.getInstance().showLibrary(); }
    public void logout()        { new AuthController().logout(); }
}
