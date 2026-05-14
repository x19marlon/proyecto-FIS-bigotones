package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.model.Order;
import com.openlib.model.User;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.List;

public class AdminController {

    private final DataStore store = DataStore.getInstance();

    // ---- Stats ----
    public int getTotalUsers()  { return store.getTotalUsers(); }
    public int getTotalBooks()  { return store.getTotalBooks(); }
    public int getTotalOrders() { return store.getTotalOrders(); }
    public Book getMostDownloaded() { return store.getMostDownloadedBook(); }

    // ---- Books ----
    public List<Book> getAllBooks() { return store.getAllBooks(); }

    public String addBook(String title, String author, String isbn,
                          String category, String description) {
        if (title == null || title.isBlank()) return "El título es requerido.";
        if (author == null || author.isBlank()) return "El autor es requerido.";

        String[] colors = {"#2D6A4F","#1A3C5E","#7B2D8B","#B5451B","#C9882A","#1E6B6B","#5C2D91","#1A3A1A"};
        String color = colors[(int)(Math.random() * colors.length)];

        Book book = new Book(0, title.trim(), author.trim(),
                isbn == null ? "N/A" : isbn.trim(),
                category == null ? "" : category.trim(),
                description == null ? "" : description.trim(),
                0.0, 0.0, 0, color);
        store.addBook(book);
        return null;
    }

    public boolean deleteBook(Long bookId) {
        return store.deleteBook(bookId);
    }

    // ---- Users ----
    public List<User> getAllUsers() { return store.getAllUsers(); }
    public boolean deleteUser(Long userId) { return store.deleteUser(userId); }

    // ---- Orders ----
    public List<Order> getAllOrders() { return store.getAllOrders(); }
    public void advanceOrder(Long id) { store.advanceOrder(id); }
    public void cancelOrder(Long id) { store.cancelOrder(id); }

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
