package com.openlib.service.proxy;

import com.openlib.model.Book;
import com.openlib.util.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteBookService implements BookService {

    @Override
    public List<Book> getAllBooks() {
        try {
            System.out.println("[REMOTE SERVICE] Fetching books from Backend API...");
            Map<String, Object> response = ApiClient.get("/books", Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            
            List<Book> allBooks = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            
            for (Map<String, Object> bookMap : content) {
                allBooks.add(mapper.convertValue(bookMap, Book.class));
            }
            return allBooks;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch books from remote service", e);
        }
    }
}
