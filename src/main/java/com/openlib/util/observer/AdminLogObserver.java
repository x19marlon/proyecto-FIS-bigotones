package com.openlib.util.observer;

import com.openlib.model.Order;
import java.time.LocalDateTime;

public class AdminLogObserver implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[AUDIT LOG] " + LocalDateTime.now());
        System.out.println(">> EVENTO: Cambio de estado en Orden #" + order.getId());
        System.out.println(">> NUEVO ESTADO: " + order.getStatus());
        System.out.println(">> USUARIO: " + (order.getUser() != null ? order.getUser().getName() : "N/A"));
        System.out.println("--------------------------------------------------");
    }
}
