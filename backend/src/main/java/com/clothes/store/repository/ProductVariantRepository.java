package com.clothes.store.repository;

import com.clothes.store.model.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select distinct v
            from ProductVariant v
            join fetch v.product p
            where v.id in :ids
            """)
    List<ProductVariant> findLockedByIdIn(@Param("ids") Collection<Long> ids);
}
