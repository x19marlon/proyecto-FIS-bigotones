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
            // Solo hacer seed si la base de datos está vacía
            if (bookRepository.count() > 0) {
                System.out.println("[DataInitializer] La BD ya contiene datos, omitiendo seed.");
                return;
            }
            System.out.println("[DataInitializer] BD vacía — insertando datos iniciales...");

            String[] titles = {
                "Don Quijote de la Mancha", "Clean Code", "Design Patterns",
                "The Pragmatic Programmer", "Introduction to Algorithms", "Artificial Intelligence: A Modern Approach",
                "Cracking the Coding Interview", "The Mythical Man-Month", "Refactoring",
                "Code Complete", "Test Driven Development", "Domain-Driven Design",
                "Effective Java", "Java Concurrency in Practice", "Patterns of Enterprise Application Architecture",
                "Cien años de soledad", "1984", "El principito", "Ficciones",
                "Orgullo y prejuicio", "Crimen y castigo", "La Odisea", "Hamlet"
            };
            String[] authors = {
                "Miguel de Cervantes", "Robert C. Martin", "Gang of Four",
                "Andrew Hunt", "Thomas H. Cormen", "Stuart Russell",
                "Gayle Laakmann", "Fred Brooks", "Martin Fowler",
                "Steve McConnell", "Kent Beck", "Eric Evans",
                "Joshua Bloch", "Brian Goetz", "Martin Fowler",
                "Gabriel García Márquez", "George Orwell", "Antoine de Saint-Exupéry", "Jorge Luis Borges",
                "Jane Austen", "Fiódor Dostoyevski", "Homero", "William Shakespeare"
            };
            // Categorías generales: Literatura | Ingeniería de Software | Ciencias | Matemáticas | Gestión
            String[] categories = {
                "Literatura",                         // Don Quijote
                "Ingeniería de Software",             // Clean Code
                "Ingeniería de Software",             // Design Patterns
                "Ingeniería de Software",             // Pragmatic Programmer
                "Matemáticas",                        // Introduction to Algorithms
                "Ciencias",                           // Artificial Intelligence
                "Ingeniería de Software",             // Cracking the Coding Interview
                "Gestión",                            // The Mythical Man-Month
                "Ingeniería de Software",             // Refactoring
                "Ingeniería de Software",             // Code Complete
                "Ingeniería de Software",             // Test Driven Development
                "Ingeniería de Software",             // Domain-Driven Design
                "Ingeniería de Software",             // Effective Java
                "Ingeniería de Software",             // Java Concurrency
                "Ingeniería de Software",             // Patterns of Enterprise Application Architecture
                "Literatura",                         // Cien años de soledad
                "Literatura",                         // 1984
                "Literatura",                         // El principito
                "Literatura",                         // Ficciones
                "Literatura",                         // Orgullo y prejuicio
                "Literatura",                         // Crimen y castigo
                "Literatura",                         // La Odisea
                "Literatura"                          // Hamlet
            };
            String[] colors = {
                "#2F5D62", "#C97B63", "#2D6A4F", "#1A3C5E", "#7B2D8B",
                "#B5451B", "#C9882A", "#1E6B6B", "#5C2D91", "#1A3A1A",
                "#3D405B", "#81B29A", "#F2CC8F", "#E07A5F", "#3D405B",
                "#8E24AA", "#3949AB", "#00ACC1", "#43A047", "#D81B60",
                "#5D4037", "#00897B", "#FB8C00", "#E53935"
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
                        .coverColor(colors[i % colors.length])
                        .build());
            }

            bookRepository.save(Book.builder()
                    .title("Borrador de Investigación")
                    .author("Investigador Incógnito")
                    .isbn("ISBN-PEND")
                    .category("Ciencias")
                    .status("PENDIENTE")
                    .build());

            // Seed Users
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .name("Admin OpenLib")
                        .email("admin@openlib.com")
                        .password("admin123")
                        .role("ADMIN")
                        .address("Oficina Central, Edificio Principal")
                        .build());

                userRepository.save(User.builder()
                        .name("a")
                        .email("a@test.co")
                        .password("000000")
                        .role("BUYER")
                        .address("Campus Norte, Facultad de Ingeniería")
                        .build());

                userRepository.save(User.builder()
                        .name("Carlos Ramírez")
                        .email("carlos@openlib.edu.co")
                        .password("buyer123")
                        .role("BUYER")
                        .address("Campus Central, Edificio de Biblioteca")
                        .build());
            }

            System.out.println("[DataInitializer] Seed completado.");
        };
    }
}
