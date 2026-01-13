package com.saas.filtro.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order_item")
@IdClass(SalesOrderItemId.class)
public class SalesOrderItem {

    // Chave composta sales_order_item_id
    @Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Id
    @Column(name = "line_number")
    private Integer lineNumber;

    // Relacionamento complexo
    /*
     * insertable = false, updatable = false -> Evita que o hibernate duplique
     * colunas
     */

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false),

            @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    })
    private SalesOrder salesOrder;

    // Itens do pedido
    @Column(name = "sku")
    private String sku;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "discounts")
    private BigDecimal discounts = BigDecimal.ZERO;

    @Column(name = "taxes")
    private BigDecimal taxes = BigDecimal.ZERO;

    // Construtores
    public SalesOrderItem() {
    }

    public SalesOrderItem(UUID tenantId, String orderId, Integer lineNumber, SalesOrder salesOrder, String sku,
            Integer quantity, BigDecimal unitPrice, BigDecimal discounts, BigDecimal taxes) {
        this.tenantId = tenantId;
        this.orderId = orderId;
        this.lineNumber = lineNumber;
        this.salesOrder = salesOrder;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discounts = discounts;
        this.taxes = taxes;
    }

    // Getters e Setters
    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscounts() {
        return discounts;
    }

    public void setDiscounts(BigDecimal discounts) {
        this.discounts = discounts;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

}
