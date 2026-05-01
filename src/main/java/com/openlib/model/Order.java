package com.openlib.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private User user;
    private List<CartItem> items;
    private double total;
    private LocalDateTime date;
    private String status;

    public Order(int id, User user, List<CartItem> items, double total) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.total = total;
        this.date = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
    public LocalDateTime getDate() { return date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
