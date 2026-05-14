package com.openlib.model.state;

import com.openlib.model.Order;

public class ShippedState implements OrderState {
    @Override
    public void next(Order order) {
        order.setCurrentState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("No se puede cancelar un pedido que ya ha sido enviado.");
    }

    @Override
    public String getStatusName() {
        return "SHIPPED";
    }
}
