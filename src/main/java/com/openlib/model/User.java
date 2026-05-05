package com.openlib.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String role; // "BUYER" | "ADMIN"

    // Backward compatibility for old UI if needed
    public User(int id, String name, String email, String password, String role) {
        this.id = (long) id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
