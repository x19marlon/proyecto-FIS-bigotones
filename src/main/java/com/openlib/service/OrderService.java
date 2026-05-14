package com.openlib.service;

import com.openlib.model.Book;
import com.openlib.model.Order;
import com.openlib.model.OrderItem;
import com.openlib.model.User;
import com.openlib.repository.BookRepository;
import com.openlib.repository.OrderRepository;
import com.openlib.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.openlib.util.observer.*;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderEventManager eventManager;

    @PostConstruct
    public void init() {
        // Registrar observadores por defecto
        eventManager.subscribe(new EmailNotificationObserver());
        eventManager.subscribe(new AdminLogObserver());
    }

    @Transactional
    public Order placeOrder(Long userId, List<OrderItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        Order order = Order.builder()
                .user(user)
                .build();
        
        // El estado se inicializará por defecto en PENDING gracias a la lógica en Order y el ciclo de vida de JPA

        for (OrderItemRequest req : items) {
            Book book = bookRepository.findById(req.getBookId())
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado: " + req.getBookId()));
            
            OrderItem item = OrderItem.builder()
                    .book(book)
                    .order(order)
                    .quantity(req.getQuantity())
                    .priceAtPurchase(book.getPrice())
                    .build();
            
            orderItems.add(item);
            total += book.getPrice() * req.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        eventManager.notify(savedOrder);
        return savedOrder;
    }

    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return orderRepository.findByUserOrderByDateDesc(user);
    }

    @Transactional
    public Order advanceOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        order.nextStep();
        Order savedOrder = orderRepository.save(order);
        eventManager.notify(savedOrder);
        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        order.cancelOrder();
        Order savedOrder = orderRepository.save(order);
        eventManager.notify(savedOrder);
        return savedOrder;
    }

    @Data
    public static class OrderItemRequest {
        private Long bookId;
        private Integer quantity;
    }
}
