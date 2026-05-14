package com.openlib.util.strategy;

import com.openlib.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SortStrategyTest {

    private List<Book> books;

    @BeforeEach
    void setUp() {
        books = new ArrayList<>();
        books.add(Book.builder().id(1L).title("C - Clean Code").build());
        books.add(Book.builder().id(2L).title("A - Algorithms").build());
        books.add(Book.builder().id(3L).title("B - Books").build());
    }

    @Test
    void testTitleAZStrategy() {
        SortStrategy strategy = new TitleAZStrategy();
        strategy.sort(books);
        assertEquals("A - Algorithms", books.get(0).getTitle());
        assertEquals("B - Books", books.get(1).getTitle());
        assertEquals("C - Clean Code", books.get(2).getTitle());
    }

    @Test
    void testTitleZAStrategy() {
        SortStrategy strategy = new TitleZAStrategy();
        strategy.sort(books);
        assertEquals("C - Clean Code", books.get(0).getTitle());
        assertEquals("B - Books", books.get(1).getTitle());
        assertEquals("A - Algorithms", books.get(2).getTitle());
    }

    @Test
    void testRecentStrategy() {
        SortStrategy strategy = new RecentStrategy();
        strategy.sort(books);
        assertEquals(3L, books.get(0).getId()); // Highest ID first
        assertEquals(2L, books.get(1).getId());
        assertEquals(1L, books.get(2).getId());
    }
}
