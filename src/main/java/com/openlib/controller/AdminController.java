package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.model.Order;
import com.openlib.model.User;
import com.openlib.util.ApiClient;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminController {

    private final DataStore store = DataStore.getInstance();

    // ---- Stats ----
    public int getTotalUsers() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = ApiClient.get("/users/stats", Map.class);
            Object val = stats.get("totalBuyers");
            return val instanceof Number ? ((Number) val).intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalBooks() {
        return getAllBooks().size();
    }

    public int getTotalOrders() {
        return getAllOrders().size();
    }

    public Book getMostDownloaded() {
        return getAllBooks().stream()
                .max(java.util.Comparator.comparingInt(Book::getDownloads))
                .orElse(null);
    }

    // ---- Books ----
    public List<Book> getAllBooks() {
        try {
            Book[] books = ApiClient.get("/books/all", Book[].class);
            return Arrays.asList(books);
        } catch (Exception e) {
            System.err.println("[AdminController] Error obteniendo libros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public String addBook(String title, String author, String isbn,
                          String category, String description) {
        if (title == null || title.isBlank()) return "El título es requerido.";
        if (author == null || author.isBlank()) return "El autor es requerido.";

        try {
            Map<String, String> body = Map.of(
                    "title",       title.trim(),
                    "author",      author.trim(),
                    "isbn",        isbn == null ? "N/A" : isbn.trim(),
                    "category",    category == null ? "Sin categoría" : category.trim(),
                    "description", description == null ? "" : description.trim()
            );
            ApiClient.post("/books", body, Book.class);
            return null;
        } catch (Exception e) {
            return "Error al guardar el libro: " + e.getMessage();
        }
    }

    public boolean deleteBook(Long bookId) {
        try {
            return ApiClient.delete("/books/" + bookId);
        } catch (Exception e) {
            System.err.println("[AdminController] Error eliminando libro: " + e.getMessage());
            return false;
        }
    }

    // ---- Users ----
    public List<User> getAllUsers() {
        try {
            User[] users = ApiClient.get("/users", User[].class);
            return Arrays.asList(users);
        } catch (Exception e) {
            System.err.println("[AdminController] Error obteniendo usuarios: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean deleteUser(Long userId) {
        try {
            return ApiClient.delete("/users/" + userId);
        } catch (Exception e) {
            System.err.println("[AdminController] Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }

    // ---- Orders ----
    public List<Order> getAllOrders() {
        try {
            // Reutilizamos el OrderController existente — no hay endpoint "all orders" aún,
            // así que lo llamamos a través del usuario actual o usamos la lista local.
            // TODO: agregar GET /api/orders si se requiere en el futuro.
            return store.getAllOrders();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void advanceOrder(Long id) { store.advanceOrder(id); }
    public void cancelOrder(Long id)  { store.cancelOrder(id); }

    // ---- Navigation ----
    public Long getCurrentUserId() {
        return store.getCurrentUser() != null ? store.getCurrentUser().getId() : -1L;
    }

    public void goToDashboard() { SceneManager.getInstance().showAdminDashboard(); }
    public void goToBooks()     { SceneManager.getInstance().showAdminBooks(); }
    public void goToUsers()     { SceneManager.getInstance().showAdminUsers(); }
    public void goToOrders()    { SceneManager.getInstance().showAdminOrders(); }
    public void logout()        { new AuthController().logout(); }
}
