package com.saas.filtro.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_snapshot")
@IdClass(InventorySnapshotId.class)
public class InventorySnapshot {

    @Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Id
    @Column(name = "sku")
    private String sku;

    @Id
    @Column(name = "location")
    private String location;

    @Id
    @Column(name = "snapshot_date")
    private LocalDate snapshotDate;

    @Column(name = "quantity")
    private Integer qty;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Garante a auditoria automática de atualização
    @Column(name = "updated_at", updatable = false)
    private LocalDateTime updatedAt;

    // Relacionamento com Product
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false),
            @JoinColumn(name = "sku", referencedColumnName = "sku", insertable = false, updatable = false)
    })
    private Product product;

    public InventorySnapshot() {
    }

    public InventorySnapshot(UUID tenantId, String sku, String location, LocalDate snapshotDate, Integer qty,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.location = location;
        this.snapshotDate = snapshotDate;
        this.qty = qty;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

    // Garante a auditoria automática de atualização
    // O hibernate vai preencher as datas sozinho
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
