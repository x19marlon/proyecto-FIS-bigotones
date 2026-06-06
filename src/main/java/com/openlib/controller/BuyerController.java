package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import com.openlib.util.ApiClient;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.List;

public class BuyerController {

    private final DataStore store = DataStore.getInstance();

    public com.openlib.model.User getCurrentUser() {
        return store.getCurrentUser();
    }

    // ---- Catalog ----

    public List<Book> getBooks(String query, String category) {
        try {
            // Usa el API REST para búsqueda combinada
            String q   = (query == null ? "" : java.net.URLEncoder.encode(query.trim(), java.nio.charset.StandardCharsets.UTF_8));
            String cat = (category == null || "Todas".equals(category)) ? "Todas"
                    : java.net.URLEncoder.encode(category.trim(), java.nio.charset.StandardCharsets.UTF_8);
            Book[] books = ApiClient.get("/books/search?q=" + q + "&cat=" + cat, Book[].class);
            return java.util.Arrays.asList(books);

        } catch (Exception e) {
            System.err.println("[BuyerController] Error obteniendo libros del API: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public List<String> getCategories() {
        try {
            String[] cats = ApiClient.get("/books/categories", String[].class);
            return java.util.Arrays.asList(cats);
        } catch (Exception e) {
            System.err.println("[BuyerController] Error obteniendo categorías: " + e.getMessage());
            return store.getCategories(); // fallback a memoria
        }
    }

    /**
     * Devuelve hasta {@code limit} libros relacionados (misma categoría) desde el API.
     */
    public List<Book> getRelatedBooks(Book book, int limit) {
        try {
            Book[] related = ApiClient.get("/books/" + book.getId() + "/related?limit=" + limit, Book[].class);
            return java.util.Arrays.asList(related);
        } catch (Exception e) {
            System.err.println("[BuyerController] Error obteniendo libros relacionados: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Placeholder for future backend pagination.
     * TODO: Implement server-side pagination once the API supports 'page' and 'size' parameters.
     */
    public List<Book> getBooksPaginated(String query, String category, int page, int size) {
        // Current implementation still relies on frontend slicing
        return getBooks(query, category);
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
            if (store.getCart().isEmpty()) return null;
            
            List<com.openlib.service.OrderService.OrderItemRequest> items = store.getCart().stream()
                    .map(item -> {
                        com.openlib.service.OrderService.OrderItemRequest req = 
                                new com.openlib.service.OrderService.OrderItemRequest();
                        req.setBookId(item.getBook().getId());
                        req.setQuantity(item.getQuantity());
                        return req;
                    })
                    .collect(java.util.stream.Collectors.toList());

            com.openlib.controller.OrderController.OrderRequest request = 
                    new com.openlib.controller.OrderController.OrderRequest();
            request.setUserId(store.getCurrentUser().getId());
            request.setItems(items);

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
    public void goToCheckout()      { SceneManager.getInstance().showCheckout(); }
    public void goToOrderHistory()  { SceneManager.getInstance().showOrderHistory(); }
    public void goToLibrary()       { SceneManager.getInstance().showLibrary(); }
    public void logout()        { new AuthController().logout(); }
}
