package com.openlib.util;

import com.openlib.model.Book;
import com.openlib.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            for (int i = 1; i <= 15; i++) {
                repository.save(Book.builder()
                        .title("Libro Aprobado " + i)
                        .author("Autor " + i)
                        .isbn("ISBN-" + i)
                        .description("Descripción del libro aprobado " + i)
                        .price(10.0 + i)
                        .status("APROBADO")
                        .build());
            }
            repository.save(Book.builder()
                    .title("Libro Pendiente")
                    .author("Autor X")
                    .isbn("ISBN-P")
                    .status("PENDIENTE")
                    .build());
        };
    }
}
