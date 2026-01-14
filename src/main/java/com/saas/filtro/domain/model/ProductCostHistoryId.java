package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

//Chave composta temporal 

/*Diferente da chave composta de SalesOrder, que é composta por tenantId e orderId,
 * a chave composta de ProductHistory é composta por tenantId, sku e validFrom */

//Isso permite que o mesmo produto tenha custos diferentes em datas diferentes 
public class ProductCostHistoryId implements Serializable {
    private UUID tenantId;
    private String sku;
    private LocalDateTime validFrom;

    // Equals e HashCode são obrigatórios para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductCostHistoryId that = (ProductCostHistoryId) o;
        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(sku, that.sku)
                && Objects.equals(validFrom, that.validFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, sku, validFrom);
    }

    // Construtor vazio e com parâmetros
    public ProductCostHistoryId() {
    }

    public ProductCostHistoryId(UUID tenantId, String sku, LocalDateTime validFrom) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.validFrom = validFrom;
    }

    // Getters e Setters, como de costume
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

}
