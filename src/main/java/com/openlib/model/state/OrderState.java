package com.openlib.model.state;

import com.openlib.model.Order;

public interface OrderState {
    void next(Order order);
    void cancel(Order order);
    String getStatusName();
}
