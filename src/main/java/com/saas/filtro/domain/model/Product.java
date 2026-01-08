package com.saas.filtro.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product")
@IdClass(ProductId.class)
public class Product {

    @Id // <- PARTE 1 DA CHAVE
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Id // <- PARTE 2 DA CHAVE
    private String sku;

    @Column(nullable = false)
    private String name;

    private String brand;
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes_json")
    private Map<String, Object> attributes;

    @Column(nullable = false)
    private String status = "ACTIVE";

    // Correção: Anotações saíram do construtor e vieram para o campo
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Product() {

    }

    public Product(UUID tenantId, String sku, String name) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.name = name;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    // Geralmente não temos setCreatedAt pois é automático

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Método de callback para atualizar data antes de update (Opcional, mas útil)
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
