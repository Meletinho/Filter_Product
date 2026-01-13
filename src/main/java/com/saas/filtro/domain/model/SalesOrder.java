package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order")
@IdClass(SalesOrderId.class)

public class SalesOrder implements Serializable {

    // Chave Composta
    @Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Id
    @Column(name = "order_id")
    private String orderId;

    // Dados de pedido
    @Column(nullable = false)
    private String channel;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "costumer_id")
    private String costumerId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status = "PENDING";

    // Relacionamento com Itens
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items = new ArrayList<>();

    // Auditoria
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public SalesOrder() {
    }

    public SalesOrder(UUID tenantId, String orderId, String channel, LocalDate orderDate, String costumerId,
            BigDecimal totalAmount, String status, List<SalesOrderItem> items) {
        this.tenantId = tenantId;
        this.orderId = orderId;
        this.channel = channel;
        this.orderDate = orderDate;
        this.costumerId = costumerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    // Métodos de Negócio
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(SalesOrderItem item) {
        this.items.add(item);
        item.setSalesOrder(this);
    }

    public void removeItem(SalesOrderItem item) {
        this.items.remove(item);
        item.setSalesOrder(null);
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCostumerId() {
        return costumerId;
    }

    public void setCostumerId(String costumerId) {
        this.costumerId = costumerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SalesOrderItem> getItems() {
        return items;
    }

    public void setItems(List<SalesOrderItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
