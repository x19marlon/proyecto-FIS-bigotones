package com.openlib.util.strategy;

import com.openlib.model.Book;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentStrategy implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        // Assume higher ID means more recent
        books.sort(Comparator.comparing(Book::getId).reversed());
    }
}
