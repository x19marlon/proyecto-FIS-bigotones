package com.openlib.model.state;

import com.openlib.model.Order;

public class CancelledState implements OrderState {
    @Override
    public void next(Order order) {
        throw new IllegalStateException("Un pedido cancelado no puede volver a estar activo.");
    }

    @Override
    public void cancel(Order order) {
        // Ya está cancelado
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
    }
}
