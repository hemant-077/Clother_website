package com.clothes.store.controller;

import com.clothes.store.dto.OrderResponse;
import com.clothes.store.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse getOrder(@PathVariable String orderNumber,
                                  @RequestParam(name = "session_id", required = false) String checkoutSessionId)
            throws StripeException {
        return orderService.getOrder(orderNumber, checkoutSessionId);
    }
}
