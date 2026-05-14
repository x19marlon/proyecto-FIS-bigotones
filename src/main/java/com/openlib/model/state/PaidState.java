package com.openlib.model.state;

import com.openlib.model.Order;

public class PaidState implements OrderState {
    @Override
    public void next(Order order) {
        order.setCurrentState(new ShippedState());
    }

    @Override
    public void cancel(Order order) {
        order.setCurrentState(new CancelledState());
    }

    @Override
    public String getStatusName() {
        return "PAID";
    }
}
