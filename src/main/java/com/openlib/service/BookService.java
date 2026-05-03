package com.openlib.service;

import com.openlib.model.Book;
import com.openlib.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
