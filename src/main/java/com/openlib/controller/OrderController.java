package com.openlib.controller;

import com.openlib.model.Order;
import com.openlib.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request.getUserId(), request.getItems());
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/{id}/next")
    public Order advanceOrder(@PathVariable Long id) {
        return orderService.advanceOrder(id);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @Data
    public static class OrderRequest {
        private Long userId;
        private List<OrderService.OrderItemRequest> items;
    }
}
