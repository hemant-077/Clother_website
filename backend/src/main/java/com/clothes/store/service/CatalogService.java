package com.clothes.store.service;

import com.clothes.store.dto.ProductResponse;
import com.clothes.store.dto.VariantResponse;
import com.clothes.store.model.Product;
import com.clothes.store.model.ProductVariant;
import com.clothes.store.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> listProducts(String search, String category) {
        String normalizedSearch = blankToNull(search);
        String normalizedCategory = blankToNull(category);
        return productRepository.search(normalizedSearch, normalizedCategory)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toProductResponse(product);
    }

    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSlug(),
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getDescription(),
                product.getMaterial(),
                product.getFit(),
                product.getImageUrl(),
                product.getPrice(),
                product.getVariants().stream()
                        .sorted(Comparator.comparing(ProductVariant::getSize).thenComparing(ProductVariant::getColor))
                        .map(variant -> new VariantResponse(
                                variant.getId(),
                                variant.getSku(),
                                variant.getSize(),
                                variant.getColor(),
                                variant.getStockQuantity()
                        ))
                        .toList()
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
