package com.openlib.util.strategy;

import com.openlib.model.Book;
import java.util.List;

public interface SortStrategy {
    void sort(List<Book> books);
}
