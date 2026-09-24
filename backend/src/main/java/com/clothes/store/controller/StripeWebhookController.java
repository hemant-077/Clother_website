package com.clothes.store.controller;

import com.clothes.store.service.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final CheckoutService checkoutService;
    private final String webhookSecret;

    public StripeWebhookController(CheckoutService checkoutService,
                                   @Value("${stripe.webhook-secret:}") String webhookSecret) {
        this.checkoutService = checkoutService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody String payload,
                                                             @RequestHeader("Stripe-Signature") String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Stripe webhook secret is not configured"));
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid Stripe signature"));
        }

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (stripeObject instanceof Session session) {
            String orderNumber = session.getMetadata() == null ? null : session.getMetadata().get("orderNumber");
            switch (event.getType()) {
                case "checkout.session.completed", "checkout.session.async_payment_succeeded" ->
                        checkoutService.markPaid(session.getId(), orderNumber, session.getPaymentIntent());
                case "checkout.session.async_payment_failed" ->
                        checkoutService.markPaymentFailed(session.getId(), orderNumber);
                case "checkout.session.expired" ->
                        checkoutService.cancelAndReleaseStock(session.getId(), orderNumber);
                default -> {
                }
            }
        }

        return ResponseEntity.ok(Map.of("received", "true"));
    }
}
