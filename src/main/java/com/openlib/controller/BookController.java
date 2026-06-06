package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.service.BookService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /** GET /api/books — catálogo paginado (aprobados) */
    @GetMapping
    public Page<Book> getCatalog(Pageable pageable) {
        return bookService.getPublicCatalog(pageable);
    }

    /** GET /api/books/search?q=&cat= — búsqueda combinada */
    @GetMapping("/search")
    public List<Book> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "Todas") String cat) {
        return bookService.searchBooks(q, cat);
    }

    /** GET /api/books/categories — lista de categorías únicas desde BD */
    @GetMapping("/categories")
    public List<String> getCategories() {
        return bookService.getCategories();
    }

    /** GET /api/books/all — todos los libros incluido PENDIENTE (admin) */
    @GetMapping("/all")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    /** GET /api/books/{id} — detalle de un libro */
    @GetMapping("/{id}")
    public Book getBookDetail(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    /** GET /api/books/{id}/related?limit=4 — libros relacionados */
    @GetMapping("/{id}/related")
    public List<Book> getRelatedBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {
        return bookService.getRelatedBooks(id, limit);
    }

    /** POST /api/books — crear libro (admin) */
    @PostMapping
    public Book createBook(@RequestBody BookRequest req) {
        String[] colors = {"#2D6A4F","#1A3C5E","#7B2D8B","#B5451B","#C9882A","#1E6B6B","#5C2D91","#1A3A1A"};
        String color = colors[(int)(Math.random() * colors.length)];

        Book book = Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn() != null ? req.getIsbn() : "N/A")
                .category(req.getCategory() != null ? req.getCategory() : "Sin categoría")
                .description(req.getDescription() != null ? req.getDescription() : "")
                .price(0.0)
                .rating(0.0)
                .downloads(0)
                .coverColor(color)
                .status("APROBADO")
                .build();
        return bookService.saveBook(book);
    }

    /** DELETE /api/books/{id} — eliminar libro (admin) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /** PUT /api/books/{id}/approve — aprobar libro pendiente */
    @PutMapping("/{id}/approve")
    public Book approveBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        book.setStatus("APROBADO");
        return bookService.saveBook(book);
    }

    @Data
    public static class BookRequest {
        private String title;
        private String author;
        private String isbn;
        private String category;
        private String description;
    }
}
