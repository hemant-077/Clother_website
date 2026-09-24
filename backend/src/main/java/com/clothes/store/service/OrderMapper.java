package com.clothes.store.service;

import com.clothes.store.dto.OrderLineResponse;
import com.clothes.store.dto.OrderResponse;
import com.clothes.store.model.OrderLine;
import com.clothes.store.model.PurchaseOrder;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(PurchaseOrder order) {
        return new OrderResponse(
                order.getOrderNumber(),
                order.getStripeSessionId(),
                order.getStripePaymentIntentId(),
                order.getStatus(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getSubtotal(),
                order.getTotal(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toLineResponse).toList()
        );
    }

    private OrderLineResponse toLineResponse(OrderLine line) {
        return new OrderLineResponse(
                line.getProductName(),
                line.getSku(),
                line.getSize(),
                line.getColor(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getLineTotal()
        );
    }
}
