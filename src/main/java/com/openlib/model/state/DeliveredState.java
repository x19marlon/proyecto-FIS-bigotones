package com.openlib.model.state;

import com.openlib.model.Order;

public class DeliveredState implements OrderState {
    @Override
    public void next(Order order) {
        // Estado final
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("No se puede cancelar un pedido que ya ha sido entregado.");
    }

    @Override
    public String getStatusName() {
        return "DELIVERED";
    }
}
