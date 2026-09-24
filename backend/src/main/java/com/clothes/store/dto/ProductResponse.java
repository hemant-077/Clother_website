package com.clothes.store.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String slug,
        String name,
        String brand,
        String category,
        String description,
        String material,
        String fit,
        String imageUrl,
        BigDecimal price,
        List<VariantResponse> variants
) {
}
