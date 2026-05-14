package com.openlib.util.strategy;

import com.openlib.model.Book;
import java.util.List;

public class TitleAZStrategy implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort((a, b) -> {
            String t1 = a.getTitle() == null ? "" : a.getTitle();
            String t2 = b.getTitle() == null ? "" : b.getTitle();
            return t1.compareToIgnoreCase(t2);
        });
    }
}
