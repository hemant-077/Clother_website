package com.clothes.store.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        String productName,
        String sku,
        String size,
        String color,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
