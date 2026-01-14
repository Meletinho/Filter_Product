package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

//Nesse caso, a classe InventorySnapshotId vai ser usada para identificar
//um InventorySnapshot, ou seja, um InventorySnapshotId vai ser composto
//pelo tenantId, sku e location (onde ele está), snapshotDate (quando ele foi tirado)
public class InventorySnapshotId implements Serializable {

    private UUID tenantId;
    private String sku;
    private String location;
    private LocalDate snapshotDate;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InventorySnapshotId that = (InventorySnapshotId) o;
        return tenantId.equals(that.tenantId) && sku.equals(that.sku) && location.equals(that.location)
                && snapshotDate.equals(that.snapshotDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, sku, location, snapshotDate);
    }

    public InventorySnapshotId() {
    }

    public InventorySnapshotId(UUID tenantId, String sku, String location, LocalDate snapshotDate) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.location = location;
        this.snapshotDate = snapshotDate;
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

}
