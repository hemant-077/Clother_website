package com.clothes.store.dto;

import com.clothes.store.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderNumber,
        String stripeSessionId,
        String stripePaymentIntentId,
        OrderStatus status,
        String customerName,
        String customerEmail,
        BigDecimal subtotal,
        BigDecimal total,
        String currency,
        Instant createdAt,
        List<OrderLineResponse> items
) {
}
