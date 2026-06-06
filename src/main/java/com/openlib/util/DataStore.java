package com.openlib.util;

import com.openlib.model.Book;
import com.openlib.model.CartItem;
import com.openlib.model.Order;
import com.openlib.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DataStore mantiene el estado de la sesión activa y el carrito en memoria.
 * Los datos de usuarios, libros y órdenes ahora son responsabilidad del API REST
 * y la base de datos persistente (H2 en archivo).
 *
 * Funciones que permanecen aquí:
 *  - Sesión del usuario actual (currentUser)
 *  - Carrito local de compras
 *  - Lista local de órdenes para el panel de admin (hasta que se implemente GET /api/orders/all)
 */
public class DataStore {

    private static DataStore instance;

    // ---- Estado de sesión ----
    private User currentUser = null;

    // ---- Carrito local (no persiste entre sesiones — es correcto para un carrito de compras) ----
    private final List<CartItem> cart = new ArrayList<>();

    // ---- Órdenes locales (usadas por AdminController hasta que haya endpoint GET /api/orders) ----
    private final List<Order> orders = new ArrayList<>();
    private Long nextOrderId = 100L;

    private DataStore() { /* sin seed — la BD maneja los datos */ }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ==================== SESIÓN ====================

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User u) { this.currentUser = u; }
    public void logout() {
        this.currentUser = null;
        cart.clear();
    }

    // ==================== CARRITO ====================

    public List<CartItem> getCart() { return cart; }

    public void addToCart(Book book) {
        Optional<CartItem> existing = cart.stream()
                .filter(c -> c.getBook().getId().equals(book.getId()))
                .findFirst();
        if (existing.isEmpty()) {
            cart.add(new CartItem(book));
        }
    }

    public void removeFromCart(Long bookId) {
        cart.removeIf(c -> c.getBook().getId().equals(bookId));
    }

    public void clearCart() { cart.clear(); }

    public double getCartTotal() {
        return cart.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    // ==================== ÓRDENES (local — legado para admin) ====================

    public List<Order> getAllOrders() {
        orders.forEach(o -> { if (o.getCurrentState() == null) o.onPostLoad(); });
        return new ArrayList<>(orders);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void advanceOrder(Long orderId) {
        orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .ifPresent(o -> {
                    o.nextStep();
                    o.syncStatus();
                });
    }

    public void cancelOrder(Long orderId) {
        orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .ifPresent(o -> {
                    o.cancelOrder();
                    o.syncStatus();
                });
    }

    // ==================== CATEGORÍAS (fallback local) ====================

    /**
     * Genera categorías a partir de los libros del carrito actuales.
     * Solo se usa como fallback si el API de categorías no está disponible.
     */
    public List<String> getCategories() {
        List<String> cats = cart.stream()
                .map(ci -> ci.getBook().getCategory())
                .filter(c -> c != null && !c.isBlank())
                .flatMap(c -> java.util.Arrays.stream(c.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        cats.remove("Todas");
        cats.add(0, "Todas");
        return cats;
    }
}

