package com.clothes.store.service;

import com.clothes.store.dto.CheckoutItemRequest;
import com.clothes.store.dto.CheckoutRequest;
import com.clothes.store.dto.CheckoutResponse;
import com.clothes.store.dto.CustomerRequest;
import com.clothes.store.model.OrderLine;
import com.clothes.store.model.OrderStatus;
import com.clothes.store.model.ProductVariant;
import com.clothes.store.model.PurchaseOrder;
import com.clothes.store.repository.OrderRepository;
import com.clothes.store.repository.ProductVariantRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ProductVariantRepository variantRepository;
    private final OrderRepository orderRepository;
    private final String currency;
    private final String frontendUrl;
    private final boolean stripeEnabled;
    private final String stripeSecretKey;

    public CheckoutService(ProductVariantRepository variantRepository,
                           OrderRepository orderRepository,
                           @Value("${store.currency:inr}") String currency,
                           @Value("${store.frontend-url:http://localhost:5173}") String frontendUrl,
                           @Value("${store.stripe.enabled:true}") boolean stripeEnabled,
                           @Value("${stripe.secret-key:}") String stripeSecretKey) {
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
        this.currency = currency.toLowerCase(Locale.ROOT);
        this.frontendUrl = trimTrailingSlash(frontendUrl);
        this.stripeEnabled = stripeEnabled;
        this.stripeSecretKey = stripeSecretKey;
    }

    @Transactional(rollbackFor = Exception.class)
    public CheckoutResponse createCheckout(CheckoutRequest request) throws StripeException {
        validateStripeConfiguration();

        Map<Long, Integer> requestedQuantities = mergeQuantities(request.items());
        List<ProductVariant> variants = lockVariants(requestedQuantities.keySet());
        Map<Long, ProductVariant> variantById = variants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

        PurchaseOrder order = buildReservedOrder(request.customer(), requestedQuantities, variantById);
        orderRepository.save(order);

        String successUrl = appendOrderNumber(defaultUrl(request.successUrl(), "/success"), order.getOrderNumber());
        successUrl = appendStripeSessionIdPlaceholder(successUrl);
        String cancelUrl = appendOrderNumber(defaultUrl(request.cancelUrl(), "/cart"), order.getOrderNumber());

        Stripe.apiKey = stripeSecretKey;
        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomerEmail(request.customer().email())
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("orderNumber", order.getOrderNumber())
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("orderNumber", order.getOrderNumber())
                        .build());

        for (OrderLine line : order.getItems()) {
            params.addLineItem(buildStripeLineItem(line));
        }

        Session session = Session.create(params.build());
        order.setStripeSessionId(session.getId());
        return new CheckoutResponse(order.getOrderNumber(), session.getId(), session.getUrl());
    }

    private void validateStripeConfiguration() {
        if (!stripeEnabled) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe checkout is disabled. Set STRIPE_ENABLED=true");
        }
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe secret key is not configured");
        }
        if (!stripeSecretKey.startsWith("sk_")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe secret key must start with sk_");
        }
    }

    @Transactional
    public void markPaid(String stripeSessionId, String orderNumber, String paymentIntentId) {
        PurchaseOrder order = findOrder(stripeSessionId, orderNumber);
        if (order.getStatus() == OrderStatus.PAID) {
            if ((order.getStripePaymentIntentId() == null || order.getStripePaymentIntentId().isBlank())
                    && paymentIntentId != null && !paymentIntentId.isBlank()) {
                order.markPaid(paymentIntentId);
            }
            return;
        }
        order.markPaid(paymentIntentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncCheckoutSession(String orderNumber, String checkoutSessionId) throws StripeException {
        if (checkoutSessionId == null || checkoutSessionId.isBlank()) {
            return;
        }
        validateStripeConfiguration();

        PurchaseOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.getStripeSessionId() == null || !order.getStripeSessionId().equals(checkoutSessionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe session does not match this order");
        }

        Stripe.apiKey = stripeSecretKey;
        Session session = Session.retrieve(checkoutSessionId);
        String sessionOrderNumber = session.getMetadata() == null ? null : session.getMetadata().get("orderNumber");
        if (sessionOrderNumber != null && !sessionOrderNumber.equals(orderNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe session metadata does not match this order");
        }

        if ("paid".equals(session.getPaymentStatus()) || "complete".equals(session.getStatus())) {
            order.markPaid(session.getPaymentIntent());
        } else if ("expired".equals(session.getStatus()) && order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            releaseReservedStock(order);
            order.markCanceled();
        }
    }

    @Transactional
    public void cancelAndReleaseStock(String stripeSessionId, String orderNumber) {
        PurchaseOrder order = findOrder(stripeSessionId, orderNumber);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return;
        }

        releaseReservedStock(order);
        order.markCanceled();
    }

    @Transactional
    public void markPaymentFailed(String stripeSessionId, String orderNumber) {
        PurchaseOrder order = findOrder(stripeSessionId, orderNumber);
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            releaseReservedStock(order);
            order.markPaymentFailed();
        }
    }

    private void releaseReservedStock(PurchaseOrder order) {
        Map<Long, Integer> quantities = order.getItems().stream()
                .collect(Collectors.toMap(
                        line -> line.getVariant().getId(),
                        OrderLine::getQuantity,
                        Integer::sum
                ));
        List<ProductVariant> variants = lockVariants(quantities.keySet());
        for (ProductVariant variant : variants) {
            variant.release(quantities.get(variant.getId()));
        }
    }

    private PurchaseOrder buildReservedOrder(CustomerRequest customer,
                                             Map<Long, Integer> requestedQuantities,
                                             Map<Long, ProductVariant> variantById) {
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderLine> lines = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            ProductVariant variant = variantById.get(entry.getKey());
            int quantity = entry.getValue();
            if (variant == null || !variant.getProduct().isActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A selected item is no longer available");
            }
            if (!variant.hasStock(quantity)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, variant.getProduct().getName() + " is out of stock");
            }
            variant.reserve(quantity);
            OrderLine line = new OrderLine(variant, quantity, variant.getProduct().getPrice());
            subtotal = subtotal.add(line.getLineTotal());
            lines.add(line);
        }

        PurchaseOrder order = new PurchaseOrder(
                nextOrderNumber(),
                customer.name().trim(),
                customer.email().trim().toLowerCase(Locale.ROOT),
                customer.phone().trim(),
                customer.address().trim(),
                customer.city().trim(),
                customer.state().trim(),
                customer.postalCode().trim(),
                customer.country().trim(),
                subtotal,
                subtotal,
                currency
        );
        lines.forEach(order::addItem);
        return order;
    }

    private SessionCreateParams.LineItem buildStripeLineItem(OrderLine line) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) line.getQuantity())
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency)
                        .setUnitAmount(toMinorUnit(line.getUnitPrice()))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(line.getProductName())
                                .setDescription(line.getColor() + " / " + line.getSize() + " / " + line.getSku())
                                .build())
                        .build())
                .build();
    }

    private Map<Long, Integer> mergeQuantities(List<CheckoutItemRequest> items) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (CheckoutItemRequest item : items) {
            if (item.quantity() > 20) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 20 units allowed per item");
            }
            quantities.merge(item.variantId(), item.quantity(), Integer::sum);
        }
        return quantities;
    }

    private List<ProductVariant> lockVariants(Collection<Long> ids) {
        List<ProductVariant> variants = variantRepository.findLockedByIdIn(ids);
        if (variants.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A selected item is invalid");
        }
        return variants;
    }

    private PurchaseOrder findOrder(String stripeSessionId, String orderNumber) {
        if (orderNumber != null && !orderNumber.isBlank()) {
            return orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        return orderRepository.findByStripeSessionId(Objects.requireNonNull(stripeSessionId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private String nextOrderNumber() {
        int value = 100000 + RANDOM.nextInt(900000);
        return "CLT-" + System.currentTimeMillis() + "-" + value;
    }

    private long toMinorUnit(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private String defaultUrl(String requestedUrl, String path) {
        if (requestedUrl == null || requestedUrl.isBlank()) {
            return frontendUrl + path;
        }
        return requestedUrl.trim();
    }

    private String appendOrderNumber(String url, String orderNumber) {
        return url + (url.contains("?") ? "&" : "?") + "order=" + orderNumber;
    }

    private String appendStripeSessionIdPlaceholder(String url) {
        return url + (url.contains("?") ? "&" : "?") + "session_id={CHECKOUT_SESSION_ID}";
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
