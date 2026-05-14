package com.openlib.util.observer;

import com.openlib.model.Order;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class OrderEventManager {
    private final List<OrderObserver> observers = new ArrayList<>();

    public void subscribe(OrderObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(OrderObserver observer) {
        observers.remove(observer);
    }

    public void notify(Order order) {
        for (OrderObserver observer : observers) {
            observer.update(order);
        }
    }
}
