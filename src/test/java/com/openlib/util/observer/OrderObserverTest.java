package com.openlib.util.observer;

import com.openlib.model.Order;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderObserverTest {

    @Test
    void testNotifyObservers() {
        OrderEventManager manager = new OrderEventManager();
        AtomicBoolean wasNotified = new AtomicBoolean(false);

        OrderObserver observer = order -> wasNotified.set(true);
        manager.subscribe(observer);

        Order order = new Order();
        manager.notify(order);

        assertTrue(wasNotified.get(), "El observador debería haber sido notificado");
    }
}
