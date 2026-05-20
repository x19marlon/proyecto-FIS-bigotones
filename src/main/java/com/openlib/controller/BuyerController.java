package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.List;

public class BuyerController {

    private final DataStore store = DataStore.getInstance();
    private final com.openlib.service.decorator.BookService bookService = new com.openlib.service.decorator.CachedBookDecorator(new com.openlib.service.decorator.RemoteBookService());

    public com.openlib.model.User getCurrentUser() {
        return store.getCurrentUser();
    }

    // ---- Catalog ----

    public List<Book> getBooks(String query, String category) {
        try {
            List<Book> allBooks = bookService.getAllBooks();

            // Local filtering
            String q = query == null ? "" : query.toLowerCase().trim();
            return allBooks.stream()
                    .filter(b -> {
                        String title = b.getTitle() == null ? "" : b.getTitle().toLowerCase();
                        String author = b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
                        String isbn = b.getIsbn() == null ? "" : b.getIsbn();
                        String bCat = b.getCategory() == null ? "" : b.getCategory();

                        boolean matchQ = q.isEmpty() || title.contains(q) || author.contains(q) || isbn.contains(q);
                        boolean matchCat = category == null || category.equals("Todas") || bCat.equals(category);
                        return matchQ && matchCat;
                    })
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            System.err.println("Proxy Service failed, falling back to local DataStore: " + e.getMessage());
            return store.searchBooks(query, category);
        }
    }

    public List<String> getCategories() {
        // We still use DataStore for categories or we could fetch them from backend too
        return store.getCategories();
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
