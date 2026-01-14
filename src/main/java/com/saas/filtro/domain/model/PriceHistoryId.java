package com.saas.filtro.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PriceHistoryId implements Serializable {

    // Agora eu vou usar quatro chaves
    // Pois nesse caso, diferente do custo, o preço pode variar
    // Pelo tenantId, sku, channel e validFrom
    private UUID tenantId;
    private String sku;
    private String channel;
    private LocalDateTime validFrom;

    // * Como é uma classe de ID vamos seguir os mesmos padrões do Spring Data JPA
    // */
    // Ent eu sempre vou implementar os métodos equals e hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PriceHistoryId that = (PriceHistoryId) o;
        return tenantId.equals(that.tenantId) && sku.equals(that.sku) && channel.equals(that.channel)
                && validFrom.equals(that.validFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, sku, channel, validFrom);
    }

    public PriceHistoryId() {
    }

    public PriceHistoryId(UUID tenantId, String sku, String channel, LocalDateTime validFrom) {
        this.tenantId = tenantId;
        this.sku = sku;
        this.channel = channel;
        this.validFrom = validFrom;
    }

    // * Agora vou implementar os getters e setters, como de costume kkkk*/
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

}
