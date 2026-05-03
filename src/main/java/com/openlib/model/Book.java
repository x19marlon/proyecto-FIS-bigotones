package com.openlib.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    @Column(length = 1000)
    private String description;
    private String category;
    private double price;
    private double rating;
    private int downloads;
    private String coverColor;
    private String status; // "APROBADO", "PENDIENTE"

    // Compatibility constructor
    public Book(int id, String title, String author, String isbn,
                String category, String description, double price,
                double rating, int downloads, String coverColor) {
        this.id = (long) id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.downloads = downloads;
        this.coverColor = coverColor;
        this.status = "APROBADO"; // Default for legacy data
    }
}
