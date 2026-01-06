package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

//Ela não é uma tabela, representa apenas uma regra de igualdade entre dois objetos
public class ProductId implements Serializable {

    private UUID tenantId;
    private String sku;

    public ProductId() {
    }

    public ProductId(UUID tenantId, String sku) {
        this.tenantId = tenantId;
        this.sku = sku;
    }

    // Equals e HashCode são obrigatórios para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(tenantId, productId.tenantId)
                && Objects.equals(sku, productId.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, sku);
    }

    // Getters e Setters
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

}
