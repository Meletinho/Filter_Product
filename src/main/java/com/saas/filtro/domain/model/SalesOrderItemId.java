package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

//Chave TRIPLAAAAAAAAAA
//O item precisa de mais um coordenada
public class SalesOrderItemId implements Serializable {

    private UUID tenantId;
    private String orderId;
    private Integer lineNumber;

    public SalesOrderItemId() {
    }

    public SalesOrderItemId(UUID tenantId, String orderId, Integer lineNumber) {
        this.tenantId = tenantId;
        this.orderId = orderId;
        this.lineNumber = lineNumber;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SalesOrderItemId that = (SalesOrderItemId) o;
        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(orderId, that.orderId)
                && Objects.equals(lineNumber, that.lineNumber);
    }

    public int hashCode() {
        return Objects.hash(tenantId, orderId, lineNumber);
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

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

}
