package com.saas.filtro.domain.model;

import java.util.Objects;
import java.util.UUID;

//Classe que empacota as duas chaves primarias
//Implementa Serializable
//Campos com nomes exatos da entidade SalesOrder

public class SalesOrderId {

    private UUID tenantId;
    private String orderId;

    public SalesOrderId() {
    }

    public SalesOrderId(UUID tenantId, String orderId) {
        this.tenantId = tenantId;
        this.orderId = orderId;
    }

    // Equals e HashCode são obrigatórios para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SalesOrderId that = (SalesOrderId) o;
        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, orderId);
    }

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
}
