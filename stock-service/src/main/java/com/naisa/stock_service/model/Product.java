package com.naisa.stock_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(name = "uk_vendor_sku", columnNames = {"vendor", "sku"})})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String sku;
    private String name;
    private Integer stockQuantity;
    @Column(nullable = false)
    private String vendor;
}
