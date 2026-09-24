package com.clothes.store.service;

import com.clothes.store.dto.OrderResponse;
import com.clothes.store.model.PurchaseOrder;
import com.clothes.store.repository.OrderRepository;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CheckoutService checkoutService;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CheckoutService checkoutService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.checkoutService = checkoutService;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse getOrder(String orderNumber, String checkoutSessionId) throws StripeException {
        checkoutService.syncCheckoutSession(orderNumber, checkoutSessionId);

        PurchaseOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return orderMapper.toResponse(order);
    }
}
