package com.openlib.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private double total;
    private LocalDateTime date;
    private String status; // "COMPLETED", "CANCELLED"

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
        if (this.status == null) {
            this.status = "COMPLETED";
        }
    }
}
