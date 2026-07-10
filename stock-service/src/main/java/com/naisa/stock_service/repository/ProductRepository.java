package com.naisa.stock_service.repository;

import com.naisa.stock_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE "
            + "(:id IS NULL OR p.id = :id) AND "
            + "(:sku IS NULL OR :sku = '' OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :sku, '%'))) AND "
            + "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
            + "(:stockQuantity IS NULL OR p.stockQuantity = :stockQuantity) AND "
            + "(:vendor IS NULL OR :vendor = '' OR LOWER(p.vendor) LIKE LOWER(CONCAT('%', :vendor, '%')))"
    )
    List<Product> findProductsByParam(
            @Param("id") UUID id,
            @Param("sku") String sku,
            @Param("name") String name,
            @Param("stockQuantity") Integer stockQuantity,
            @Param("vendor") String vendor
    );

    @Query("SELECT p FROM Product p WHERE p.vendor = :vendor AND p.sku = :sku")
    Optional<Product> findByVendorAndSku(@Param("vendor") String vendor, @Param("sku") String sku);
}
