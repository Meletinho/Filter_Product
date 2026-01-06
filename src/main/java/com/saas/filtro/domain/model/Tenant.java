package com.saas.filtro.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity // Siginifica que se referencia a uma tabela
@Table(name = "tenant") // Nomeia a tabela

/*
 * É a raiz de tudo, nenhum dado existe sem um dono
 * É a unidade de negócio
 * Nos SaaS, id's sequênciais são inseguros e difícieis de migrar entre bancos
 * Por isso o UUID é universalmente aceito
 */
public class Tenant {

    @Id // Siginifica que se refere a uma coluna
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) // Siginifica que a coluna não pode ser nula
    private String name;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @CreationTimestamp // Significa que a coluna será preenchida automaticamente com a data de criação
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Tenant() {
    }

    public Tenant(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
