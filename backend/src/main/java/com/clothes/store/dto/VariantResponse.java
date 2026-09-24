package com.clothes.store.dto;

public record VariantResponse(
        Long id,
        String sku,
        String size,
        String color,
        int stockQuantity
) {
}
