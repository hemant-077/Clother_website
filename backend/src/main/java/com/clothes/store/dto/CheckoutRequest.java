package com.clothes.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutRequest(
        @NotNull @Valid CustomerRequest customer,
        @NotEmpty List<@Valid CheckoutItemRequest> items,
        String successUrl,
        String cancelUrl
) {
}
