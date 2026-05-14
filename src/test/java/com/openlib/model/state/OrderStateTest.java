package com.openlib.model.state;

import com.openlib.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderStateTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setCurrentState(new PendingState());
    }

    @Test
    void testInitialStateIsPending() {
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    void testTransitionToPaid() {
        order.nextStep();
        assertEquals("PAID", order.getStatus());
    }

    @Test
    void testTransitionToShipped() {
        order.nextStep(); // to Paid
        order.nextStep(); // to Shipped
        assertEquals("SHIPPED", order.getStatus());
    }

    @Test
    void testTransitionToDelivered() {
        order.nextStep(); // to Paid
        order.nextStep(); // to Shipped
        order.nextStep(); // to Delivered
        assertEquals("DELIVERED", order.getStatus());
    }

    @Test
    void testCancelFromPending() {
        order.cancelOrder();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void testCancelFromPaid() {
        order.nextStep(); // to Paid
        order.cancelOrder();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void testCannotCancelFromShipped() {
        order.nextStep(); // to Paid
        order.nextStep(); // to Shipped
        assertThrows(IllegalStateException.class, () -> order.cancelOrder());
    }

    @Test
    void testCannotAdvanceFromCancelled() {
        order.cancelOrder();
        assertThrows(IllegalStateException.class, () -> order.nextStep());
    }
}
