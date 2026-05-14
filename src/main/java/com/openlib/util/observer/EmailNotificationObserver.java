package com.openlib.util.observer;

import com.openlib.model.Order;

public class EmailNotificationObserver implements OrderObserver {
    @Override
    public void update(Order order) {
        String email = order.getUser() != null ? order.getUser().getEmail() : "usuario desconocido";
        System.out.println("[EMAIL SERVICE] Enviando notificación a " + email);
        System.out.println(">> Tu pedido #" + order.getId() + " ha cambiado de estado a: " + order.getStatus());
        System.out.println("--------------------------------------------------");
    }
}
