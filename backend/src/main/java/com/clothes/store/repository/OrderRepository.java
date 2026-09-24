package com.clothes.store.repository;

import com.clothes.store.model.PurchaseOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Optional<PurchaseOrder> findByStripeSessionId(String stripeSessionId);
}
