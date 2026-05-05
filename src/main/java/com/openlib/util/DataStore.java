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
 * DataStore simulates a database in memory.
 * In a real project this would be replaced by a JPA/JDBC repository.
 */
public class DataStore {

    private static DataStore instance;

    // ---- Data collections ----
    private final List<User> users = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private User currentUser = null;
    private Long nextUserId = 10L;
    private Long nextOrderId = 100L;

    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ==================== SEED ====================

    private void seedData() {
        // Users
        users.add(new User(1L, "Admin OpenLib", "admin@openlib.com", "admin123", "ADMIN"));
        users.add(new User(2L, "Danna García", "danna@javeriana.edu.co", "buyer123", "BUYER"));
        users.add(new User(3L, "Carlos Ramírez", "carlos@javeriana.edu.co", "buyer123", "BUYER"));

        // Books
        books.add(new Book(1, "Clean Code", "Robert C. Martin", "978-0132350884",
                "Ingeniería", "A handbook of agile software craftsmanship.", 0.0, 4.8, 1240, "#2D6A4F"));
        books.add(new Book(2, "Design Patterns", "Gang of Four", "978-0201633610",
                "Ingeniería", "Elements of reusable object-oriented software.", 0.0, 4.7, 980, "#1A3C5E"));
        books.add(new Book(3, "The Pragmatic Programmer", "Hunt & Thomas", "978-0135957059",
                "Ingeniería", "Your journey to mastery in software development.", 0.0, 4.9, 1580, "#7B2D8B"));
        books.add(new Book(4, "Introduction to Algorithms", "Cormen et al.", "978-0262033848",
                "Matemáticas", "Comprehensive guide to algorithms and data structures.", 0.0, 4.6, 2200, "#B5451B"));
        books.add(new Book(5, "You Don't Know JS", "Kyle Simpson", "978-1491904244",
                "Programación", "Deep dive into JavaScript core mechanisms.", 0.0, 4.5, 870, "#C9882A"));
        books.add(new Book(6, "Sistemas Distribuidos", "Tanenbaum", "978-0132392273",
                "Sistemas", "Principles and paradigms of distributed systems.", 0.0, 4.4, 760, "#1E6B6B"));
        books.add(new Book(7, "Spring Boot in Action", "Craig Walls", "978-1617292545",
                "Frameworks", "Building microservices with Spring Boot.", 0.0, 4.3, 640, "#5C2D91"));
        books.add(new Book(8, "Database Internals", "Alex Petrov", "978-1492040347",
                "Bases de Datos", "A deep dive into how databases are built and work.", 0.0, 4.6, 520, "#1A3A1A"));
    }

    // ==================== USER ====================

    public Optional<User> authenticate(String email, String password) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst();
    }

    public boolean registerUser(String name, String email, String password) {
        boolean exists = users.stream().anyMatch(u -> u.getEmail().equals(email));
        if (exists) return false;
        users.add(User.builder()
                .id(nextUserId++)
                .name(name)
                .email(email)
                .password(password)
                .role("BUYER")
                .build());
        return true;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User u) { this.currentUser = u; }
    public void logout() { this.currentUser = null; cart.clear(); }

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public boolean deleteUser(Long userId) {
        return users.removeIf(u -> u.getId().equals(userId));
    }

    // ==================== BOOKS ====================

    public List<Book> getAllBooks() { return new ArrayList<>(books); }

    public List<Book> searchBooks(String query, String category) {
        String q = query == null ? "" : query.toLowerCase().trim();
        return books.stream()
                .filter(b -> {
                    String title = b.getTitle() == null ? "" : b.getTitle().toLowerCase();
                    String author = b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
                    String isbn = b.getIsbn() == null ? "" : b.getIsbn();
                    String bCat = b.getCategory() == null ? "" : b.getCategory();

                    boolean matchQ = q.isEmpty()
                            || title.contains(q)
                            || author.contains(q)
                            || isbn.contains(q);
                    boolean matchCat = category == null || category.equals("Todas")
                            || bCat.equals(category);
                    return matchQ && matchCat;
                })
                .collect(Collectors.toList());
    }

    public void addBook(Book book) {
        book.setId((long) (books.size() + 1));
        books.add(book);
    }

    public boolean deleteBook(Long bookId) {
        return books.removeIf(b -> b.getId().equals(bookId));
    }

    public List<String> getCategories() {
        List<String> cats = books.stream()
                .map(Book::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        cats.add(0, "Todas");
        return cats;
    }

    // ==================== CART ====================

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

    // ==================== ORDERS ====================

    public Order placeOrder() {
        if (cart.isEmpty() || currentUser == null) return null;
        List<CartItem> snapshot = new ArrayList<>(cart);
        double total = getCartTotal();
        
        Order order = Order.builder()
                .id(nextOrderId++)
                .user(currentUser)
                .total(total)
                .status("COMPLETED")
                .build();
        
        List<com.openlib.model.OrderItem> items = snapshot.stream()
                .map(ci -> com.openlib.model.OrderItem.builder()
                        .book(ci.getBook())
                        .order(order)
                        .quantity(ci.getQuantity())
                        .priceAtPurchase(ci.getBook().getPrice())
                        .build())
                .collect(Collectors.toList());
        
        order.setItems(items);
        orders.add(order);
        
        // increase downloads
        snapshot.forEach(item -> item.getBook().setDownloads(item.getBook().getDownloads() + 1));
        cart.clear();
        return order;
    }

    public List<Order> getOrdersByUser(User user) {
        return orders.stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() { return new ArrayList<>(orders); }

    // ==================== ADMIN STATS ====================

    public int getTotalUsers() { return (int) users.stream().filter(u -> u.getRole().equals("BUYER")).count(); }
    public int getTotalBooks() { return books.size(); }
    public int getTotalOrders() { return orders.size(); }
    public Book getMostDownloadedBook() {
        return books.stream().max(Comparator.comparingInt(Book::getDownloads)).orElse(null);
    }
}
