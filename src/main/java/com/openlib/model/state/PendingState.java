package com.openlib.model.state;

import com.openlib.model.Order;

public class PendingState implements OrderState {
    @Override
    public void next(Order order) {
        order.setCurrentState(new PaidState());
    }

    @Override
    public void cancel(Order order) {
        order.setCurrentState(new CancelledState());
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }
}
