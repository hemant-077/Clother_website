package com.clothes.store.repository;

import com.clothes.store.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "variants")
    Optional<Product> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = "variants")
    @Query("""
            select distinct p
            from Product p
            where p.active = true
              and (:category is null or lower(p.category) = lower(:category))
              and (
                    :search is null
                    or lower(p.name) like lower(concat('%', :search, '%'))
                    or lower(p.brand) like lower(concat('%', :search, '%'))
                    or lower(p.description) like lower(concat('%', :search, '%'))
                  )
            order by p.createdAt desc
            """)
    List<Product> search(@Param("search") String search, @Param("category") String category);
}
