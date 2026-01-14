# 🚀 Filter Product System

> **SaaS de Inteligência de Varejo Multi-Tenant & Filtrador de Oportunidades**

## 📋 Resumo da Ópera

O **Filter Product** é uma plataforma projetada para alta escalabilidade e inteligência de negócios. Embora o conceito inicial remeta a um "filtrador", a arquitetura revela um sistema robusto de **Retail Intelligence**, capaz de ingestão massiva de dados, cálculo de lucratividade real, *forecasting* de demanda e geração automatizada de recomendações.

Atualmente, o projeto encontra-se na fase de **Bootstrap/Esqueleto**, construído sobre uma fundação moderna em Java 21 e Spring Boot 3.3.x.

---

## 🛠 Tech Stack & Capacidades

O sistema foi desenhado para suportar alta concorrência e processamento de dados complexos.

| Camada | Tecnologia | Capacidade / Propósito |
| :--- | :--- | :--- |
| **Runtime** | **Java 21 (LTS)** | Uso de **Virtual Threads** para alta concorrência na ingestão de dados. |
| **Framework** | **Spring Boot 3.3.4** | Base segura, moderna e opinativa. |
| **Ingestão** | **Spring Batch + WebFlux** | Processamento massivo offline e ingestão de pedidos em tempo real (reativo). |
| **Dados** | **PostgreSQL + Flyway** | Suporte a **JSONB** (atributos dinâmicos), séries temporais e versionamento de schema. |
| **Resiliência** | **Resilience4j** | Circuit Breakers e Rate Limiters para proteção contra falhas em integrações (Connectors). |
| **Observabilidade** | **Micrometer + Prometheus** | Visibilidade de métricas de negócio (ex: QPS de conectores, latência de ingestão). |

---

## 🏗 Arquitetura de Dados (Domain Analysis)

O modelo de dados (ERD) é orientado a *Analytics* e *Multi-Inquilino*, dividido em três domínios principais:

### 1. Core Domain (Núcleo)
* **Multi-Tenancy:** Isolamento lógico de dados. A maioria das tabelas possui `tenant_id` como parte da Chave Primária Composta (Composite PK).
* **Produto & Inventário:** Tabelas `PRODUCT`, `PRODUCT_COST_HISTORY` e `INVENTORY_SNAPSHOT` permitem rastreabilidade de custos e snapshots de estoque para cálculos precisos de margem ao longo do tempo.

### 2. Intelligence Brain (O Cérebro)
* **Análise & Recomendação:** Motor responsável por transformar dados em ações.
    * `ANALYSIS_RUN`: Controle de execução de jobs de IA/Heurística.
    * `FORECAST_RESULT`: Previsão de demanda.
    * `RECOMMENDATION`: Sugestões de ação (ex: "Repor estoque", "Baixar preço").
* **Lucratividade Real:** A tabela `PRODUCT_PROFITABILITY` cruza Receita, COGS (Custo das Mercadorias Vendidas), Taxas de Marketplace, Envio e Impostos para determinar a margem líquida real.

### 3. Facts & Metrics (Fatos)
* **Séries Temporais:** `FACT_SALES_DAILY` e `PRODUCT_METRICS_DAILY`.
* **Performance:** Pré-agregação de dados para viabilizar dashboards rápidos e relatórios analíticos sem onerar o banco transacional.

---

## 🔄 Fluxo Funcional (Workflow)

O sistema opera em ciclos contínuos de ingestão e refinamento de dados:

1.  **Conexão:** Configuração de `CONNECTOR_CONFIG` para integração com ERPs e E-commerces externos.
2.  **Ingestão:** Webhooks e Batch Jobs populam as tabelas `PRODUCT`, `SALES_ORDER` e `INVENTORY`.
3.  **Processamento Diário (Aggregation):**
    * Agregação de vendas em `FACT_SALES_DAILY`.
    * Cálculo de métricas em `PRODUCT_METRICS_DAILY`.
4.  **Run de Análise (Intelligence):**
    * Disparo de `ANALYSIS_RUN`.
    * Cálculo de `PRODUCT_PROFITABILITY`.
    * Geração de `FORECAST_RESULT` e criação de `RECOMMENDATION` acionáveis.

---

## 🚧 Status do Projeto & Gap Analysis

O código atual reflete o esqueleto da aplicação. Existe uma lacuna significativa entre a estrutura atual e o Diagrama Entidade-Relacionamento (ER) planejado.

| Componente | Estado Atual | Gap / O que falta |
| :--- | :--- | :--- |
| **Entidades de Domínio** | 🔴 Vazio | Implementar classes `@Entity` (`Tenant`, `Product`, `SalesOrder`, etc.) e seus relacionamentos JPA. |
| **Schema Migration** | 🔴 Vazio | Criar scripts SQL (Flyway) para gerar as 14 tabelas do diagrama ER. |
| **Lógica de Ingestão** | 🔴 Vazio | Implementar Services para popular `SALES_ORDER` ou `PRODUCT` via API. |
| **Motor de Análise** | 🔴 Vazio | Lógica de cálculo para gerar `FORECAST_RESULT` e `RECOMMENDATION`. |

---

## 🎯 Próximos Passos (Tracer Bullet)

O foco imediato é implementar uma fatia vertical funcional para validar a arquitetura:

- [ ] **Infra:** Configurar `application.yml` e conexão Postgres.
- [ ] **Schema:** Script Flyway V1 com DDL completo.
- [ ] **Core:** Entidades `Product` e `Tenant`.
- [ ] **Feature:** Endpoint simples de cadastro de Tenant e Produto.
