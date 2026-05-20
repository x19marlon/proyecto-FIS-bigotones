package com.openlib.service.decorator;

import com.openlib.model.Book;
import java.util.List;

public abstract class BookServiceDecorator implements BookService {

    protected final BookService wrapped;

    public BookServiceDecorator(BookService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<Book> getAllBooks() {
        return wrapped.getAllBooks();
    }
}
