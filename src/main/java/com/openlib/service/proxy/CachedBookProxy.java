package com.openlib.service.proxy;

import com.openlib.model.Book;
import java.util.List;

public class CachedBookProxy implements BookService {

    private final BookService remoteService;
    private List<Book> cache;

    public CachedBookProxy() {
        this.remoteService = new RemoteBookService();
    }

    @Override
    public List<Book> getAllBooks() {
        if (cache == null || cache.isEmpty()) {
            System.out.println("[PROXY] Cache empty. Redirecting to Remote Service...");
            cache = remoteService.getAllBooks();
        } else {
            System.out.println("[PROXY] Returning books from Cache.");
        }
        return cache;
    }

    public void clearCache() {
        this.cache = null;
    }
}
