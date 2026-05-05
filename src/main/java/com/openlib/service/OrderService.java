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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Order placeOrder(Long userId, List<OrderItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        Order order = Order.builder()
                .user(user)
                .status("COMPLETED")
                .build();

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

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return orderRepository.findByUserOrderByDateDesc(user);
    }

    @Data
    public static class OrderItemRequest {
        private Long bookId;
        private Integer quantity;
    }
}
