package com.openlib.util;

import com.openlib.model.Book;
import com.openlib.model.User;
import com.openlib.repository.BookRepository;
import com.openlib.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepository, UserRepository userRepository) {
        return args -> {
            // Seed Books
            for (int i = 1; i <= 15; i++) {
                bookRepository.save(Book.builder()
                        .title("Libro Aprobado " + i)
                        .author("Autor " + i)
                        .isbn("ISBN-" + i)
                        .description("Descripción del libro aprobado " + i)
                        .price(10.0 + i)
                        .status("APROBADO")
                        .build());
            }
            bookRepository.save(Book.builder()
                    .title("Libro Pendiente")
                    .author("Autor X")
                    .isbn("ISBN-P")
                    .status("PENDIENTE")
                    .build());

            // Seed Users
            userRepository.save(User.builder()
                    .id(1L)
                    .name("Admin OpenLib")
                    .email("admin@openlib.com")
                    .password("admin123")
                    .role("ADMIN")
                    .build());

            userRepository.save(User.builder()
                    .id(2L)
                    .name("Danna García")
                    .email("danna@javeriana.edu.co")
                    .password("buyer123")
                    .role("BUYER")
                    .build());
        };
    }
}
