package com.openlib.util.observer;

import com.openlib.model.Order;

public interface OrderObserver {
    void update(Order order);
}
