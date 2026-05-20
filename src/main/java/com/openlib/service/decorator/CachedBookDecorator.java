package com.openlib.service.decorator;

import com.openlib.model.Book;
import java.util.List;

public class CachedBookDecorator extends BookServiceDecorator {

    private List<Book> cache;

    public CachedBookDecorator(BookService wrapped) {
        super(wrapped);
    }

    @Override
    public List<Book> getAllBooks() {
        if (cache == null || cache.isEmpty()) {
            System.out.println("[DECORATOR] Cache empty. Delegating to wrapped service...");
            cache = super.getAllBooks();
        } else {
            System.out.println("[DECORATOR] Returning books from Cache.");
        }
        return cache;
    }

    public void clearCache() {
        this.cache = null;
    }
}
