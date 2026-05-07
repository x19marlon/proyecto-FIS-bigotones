package com.openlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openlib.controller.OrderController.OrderRequest;
import com.openlib.service.OrderService.OrderItemRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateOrderSuccessfully() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(1L); // The seeded user from DataInitializer
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setBookId(1L);
        item1.setQuantity(1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setBookId(2L);
        item2.setQuantity(1);

        request.setItems(Arrays.asList(item1, item2));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("Marlon"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    public void shouldReturnErrorWhenUserNotFound() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(999L);
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setBookId(1L);
        item1.setQuantity(1);

        request.setItems(Arrays.asList(item1));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); 
    }
}
