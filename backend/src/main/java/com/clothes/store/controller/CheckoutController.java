package com.clothes.store.controller;

import com.clothes.store.dto.CheckoutRequest;
import com.clothes.store.dto.CheckoutResponse;
import com.clothes.store.service.CheckoutService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) throws StripeException {
        return checkoutService.createCheckout(request);
    }
}
