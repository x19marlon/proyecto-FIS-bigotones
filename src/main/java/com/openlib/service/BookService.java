package com.openlib.service;

import com.openlib.model.Book;
import com.openlib.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getPublicCatalog(Pageable pageable) {
        return bookRepository.findByStatus("APROBADO", pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + id));
    }

    /**
     * Búsqueda combinada: texto libre (título/autor/isbn) + filtro de categoría.
     * Ambos parámetros son opcionales (null/"" = sin filtro).
     */
    public List<Book> searchBooks(String q, String category) {
        String qClean  = (q == null) ? "" : q.trim();
        String catClean = (category == null || category.isBlank() || "Todas".equals(category)) ? "Todas" : category.trim();
        return bookRepository.searchApproved(qClean, catClean);
    }

    /**
     * Devuelve la lista de categorías únicas construida a partir de los registros
     * de la BD (cada libro puede tener categorías separadas por coma).
     */
    public List<String> getCategories() {
        List<String> raw = bookRepository.findAllDistinctCategories();
        List<String> cats = raw.stream()
                .flatMap(c -> Arrays.stream(c.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        cats.remove("Todas");
        cats.add(0, "Todas");
        return cats;
    }

    /**
     * Devuelve hasta {@code limit} libros de la misma categoría principal que el
     * libro dado, sin incluir el libro en cuestión.
     */
    public List<Book> getRelatedBooks(Long bookId, int limit) {
        Book book = getBookById(bookId);
        // Usar la primera categoría del libro para buscar relacionados
        String primaryCat = book.getCategoriesList().isEmpty()
                ? "" : book.getCategoriesList().get(0);
        if (primaryCat.isBlank()) return List.of();
        Pageable top = PageRequest.of(0, limit);
        return bookRepository.findRelated(bookId, primaryCat, top);
    }

    /** Todos los libros (incluyendo pendientes) — para admin */
    public List<Book> getAllBooks() {
        return bookRepository.findAllByOrderByIdDesc();
    }

    /** Guardar/actualizar libro */
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    /** Eliminar libro por ID */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
