package com.openlib.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String description;
    private double price;
    private double rating;
    private int downloads;
    private String coverColor; // Used for placeholder cover

    public Book() {}

    public Book(int id, String title, String author, String isbn,
                String category, String description, double price,
                double rating, int downloads, String coverColor) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.downloads = downloads;
        this.coverColor = coverColor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getDownloads() { return downloads; }
    public void setDownloads(int downloads) { this.downloads = downloads; }
    public String getCoverColor() { return coverColor; }
    public void setCoverColor(String coverColor) { this.coverColor = coverColor; }
}
