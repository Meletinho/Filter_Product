package com.saas.filtro.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_cost_history")
@IdClass(ProductCostHistoryId.class)
public class ProductCostHistory {

    @Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Id
    @Column(name = "sku")
    private String sku;

    @Id
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @ManyToOne
    private Product product;
    @JoinColumns({

            @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false),

            @JoinColumn(name = "sku", referencedColumnName = "sku", insertable = false, updatable = false)
    })

    // Regras de Dados...
    @Column(nullable = false)
    private BigDecimal cost;

    // Taxas

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fees_json")
    private Map<String, BigDecimal> fees;

    private LocalDateTime validTo;

    public ProductCostHistory() {
    }

    public ProductCostHistory(UUID tenantId, String sku, LocalDateTime validFrom, LocalDateTime validTo) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.validFrom = validFrom;
        this.validTo = validTo;
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

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Map<String, BigDecimal> getFees() {
        return fees;
    }

    public void setFees(Map<String, BigDecimal> fees) {
        this.fees = fees;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

}
