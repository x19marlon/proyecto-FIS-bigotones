package com.openlib.controller;

import com.openlib.model.Book;
import com.openlib.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Page<Book> getCatalog(Pageable pageable) {
        return bookService.getPublicCatalog(pageable);
    }

    @GetMapping("/{id}")
    public Book getBookDetail(@PathVariable Long id) {
        return bookService.getBookById(id);
    }
}
