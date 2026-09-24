package com.clothes.store.dto;

public record CheckoutResponse(
        String orderNumber,
        String checkoutSessionId,
        String checkoutUrl
) {
}
