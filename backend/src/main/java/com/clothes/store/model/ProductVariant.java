package com.clothes.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String sku;

    @Column(nullable = false, length = 24)
    private String size;

    @Column(nullable = false, length = 40)
    private String color;

    @Column(nullable = false)
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected ProductVariant() {
    }

    public ProductVariant(String sku, String size, String color, int stockQuantity) {
        this.sku = sku;
        this.size = size;
        this.color = color;
        this.stockQuantity = stockQuantity;
    }

    public boolean hasStock(int quantity) {
        return stockQuantity >= quantity;
    }

    public void reserve(int quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalArgumentException("Not enough stock for " + sku);
        }
        stockQuantity -= quantity;
    }

    public void release(int quantity) {
        stockQuantity += quantity;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Product getProduct() {
        return product;
    }

    void setProduct(Product product) {
        this.product = product;
    }
}
