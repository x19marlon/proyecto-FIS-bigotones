package com.openlib.model;

import com.openlib.model.state.*;
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
    private String status;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private OrderState currentState;

    @PostLoad
    public void onPostLoad() {
        // Initialize currentState from persisted status string
        if (status == null) {
            this.currentState = new PendingState();
        } else {
            switch (status) {
                case "PENDING": this.currentState = new PendingState(); break;
                case "PAID": this.currentState = new PaidState(); break;
                case "SHIPPED": this.currentState = new ShippedState(); break;
                case "DELIVERED": this.currentState = new DeliveredState(); break;
                case "CANCELLED": this.currentState = new CancelledState(); break;
                default: this.currentState = new PendingState();
            }
        }
    }

    @PrePersist
    @PreUpdate
    public void syncStatus() {
        if (this.currentState != null) {
            this.status = this.currentState.getStatusName();
        }
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }

    public void setCurrentState(OrderState state) {
        if (state == null) return;
        this.currentState = state;
        this.status = state.getStatusName();
    }

    public void nextStep() {
        if (currentState == null) onPostLoad();
        currentState.next(this);
    }

    public void cancelOrder() {
        if (currentState == null) onPostLoad();
        currentState.cancel(this);
    }
}
