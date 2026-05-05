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
            // Seed Books with more realistic data
            String[] titles = {
                "Don Quijote de la Mancha", "Clean Code", "Design Patterns",
                "The Pragmatic Programmer", "Introduction to Algorithms", "Artificial Intelligence: A Modern Approach",
                "Cracking the Coding Interview", "The Mythical Man-Month", "Refactoring",
                "Code Complete", "Test Driven Development", "Domain-Driven Design",
                "Effective Java", "Java Concurrency in Practice", "Patterns of Enterprise Application Architecture"
            };
            String[] authors = {
                "Miguel de Cervantes", "Robert C. Martin", "Gang of Four",
                "Andrew Hunt", "Thomas H. Cormen", "Stuart Russell",
                "Gayle Laakmann", "Fred Brooks", "Martin Fowler",
                "Steve McConnell", "Kent Beck", "Eric Evans",
                "Joshua Bloch", "Brian Goetz", "Martin Fowler"
            };
            String[] categories = {
                "Literatura", "Software", "Software",
                "Software", "Ciencia", "Ciencia",
                "Software", "Gestión", "Software",
                "Software", "Software", "Software",
                "Software", "Software", "Software"
            };

            for (int i = 0; i < titles.length; i++) {
                bookRepository.save(Book.builder()
                        .title(titles[i])
                        .author(authors[i])
                        .isbn("ISBN-00" + (i + 1))
                        .category(categories[i])
                        .description("Obra maestra del conocimiento: " + titles[i])
                        .price(25.0 + i)
                        .status("APROBADO")
                        .coverColor(i % 2 == 0 ? "#2F5D62" : "#C97B63")
                        .build());
            }

            bookRepository.save(Book.builder()
                    .title("Borrador de Investigación")
                    .author("Investigador Incógnito")
                    .isbn("ISBN-PEND")
                    .category("Investigación")
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
                    .name("a")
                    .email("a@test.co")
                    .password("000000")
                    .role("BUYER")
                    .build());
        };
    }
}
