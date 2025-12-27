# Diagrama ER — SaaS "Filtrador de Oportunidades"

```mermaid
erDiagram
    TENANT {
        uuid id PK
        string name
        string status
        timestamp created_at
    }

    CONNECTOR_CONFIG {
        uuid id PK
        uuid tenant_id FK
        string source
        text auth_enc
        int rate_limit_qps
        string status
        timestamp last_sync_at
        timestamp created_at
        timestamp updated_at
    }

    PRODUCT {
        uuid tenant_id PK
        string sku PK
        string name
        string brand
        string category
        json attributes_json
        string status
        timestamp created_at
        timestamp updated_at
    }

    PRODUCT_COST_HISTORY {
        uuid tenant_id PK
        string sku PK FK
        numeric cost
        json fees_json
        timestamp valid_from PK
        timestamp valid_to
        timestamp created_at
    }

    PRICE_HISTORY {
        uuid tenant_id PK
        string sku PK FK
        string channel PK
        numeric price
        timestamp valid_from PK
        timestamp valid_to
        timestamp created_at
    }

    INVENTORY_SNAPSHOT {
        uuid tenant_id PK
        string sku PK FK
        string location PK
        int qty
        date snapshot_date PK
        timestamp created_at
    }

    SALES_ORDER {
        uuid tenant_id PK
        string id_ext PK
        string channel
        timestamp order_date
        numeric total
        string currency
        string status
        timestamp created_at
    }

    SALES_ORDER_ITEM {
        uuid tenant_id PK
        string order_id PK FK
        string sku
        int qty
        numeric unit_price
        numeric discounts
        numeric taxes
        int returned_qty
    }

    FACT_SALES_DAILY {
        uuid tenant_id PK
        string sku PK FK
        string channel PK
        date date PK
        int units
        numeric gross_revenue
        int returns
        numeric net_revenue
        numeric cogs_est
        timestamp created_at
    }

    PRODUCT_METRICS_DAILY {
        uuid tenant_id PK
        string sku PK FK
        date date PK
        numeric price_min
        numeric price_max
        numeric moving_avg_units
        numeric seasonality_idx
        boolean stockout_flag
        timestamp created_at
    }

    ANALYSIS_RUN {
        uuid id PK
        uuid tenant_id FK
        timestamp started_at
        timestamp finished_at
        json scope_json
        string status
        json metrics_json
    }

    PRODUCT_PROFITABILITY {
        uuid tenant_id PK
        uuid analysis_run_id PK FK
        string sku PK FK
        date period_start PK
        date period_end
        numeric revenue
        numeric cogs
        numeric fees
        numeric shipping
        numeric taxes
        numeric returns_cost
        numeric gross_margin_pct
        numeric profit
    }

    FORECAST_RESULT {
        uuid tenant_id PK
        uuid analysis_run_id PK FK
        string sku PK FK
        int horizon_days PK
        string model_type
        json params_json
        json forecast_series_json
        numeric mape
        numeric mae
    }

    RECOMMENDATION {
        uuid tenant_id PK
        uuid analysis_run_id PK FK
        string sku PK FK
        string action
        int quantity
        string pricing_hint
        text rationale
        numeric confidence_pct
        timestamp valid_until
    }

    TENANT ||--o{ CONNECTOR_CONFIG : has
    TENANT ||--o{ PRODUCT : has
    TENANT ||--o{ SALES_ORDER : has
    SALES_ORDER ||--|{ SALES_ORDER_ITEM : contains

    PRODUCT ||--o{ PRODUCT_COST_HISTORY : has
    PRODUCT ||--o{ PRICE_HISTORY : has
    PRODUCT ||--o{ INVENTORY_SNAPSHOT : has

    PRODUCT ||--o{ FACT_SALES_DAILY : aggregates
    PRODUCT ||--o{ PRODUCT_METRICS_DAILY : aggregates

    TENANT ||--o{ ANALYSIS_RUN : audits
    ANALYSIS_RUN ||--o{ PRODUCT_PROFITABILITY : produces
    ANALYSIS_RUN ||--o{ FORECAST_RESULT : produces
    ANALYSIS_RUN ||--o{ RECOMMENDATION : produces

    PRODUCT ||--o{ PRODUCT_PROFITABILITY : for
    PRODUCT ||--o{ FORECAST_RESULT : for
    PRODUCT ||--o{ RECOMMENDATION : for
```

## Chaves, FKs e Índices (resumo)
- Chaves primárias compostas incluem `tenant_id` para isolamento multi-tenant.
- FKs:
  - `connector_config.tenant_id -> tenant.id`
  - `product.* -> tenant.id`
  - `product_cost_history(tenant_id, sku) -> product(tenant_id, sku)`
  - `price_history(tenant_id, sku) -> product(tenant_id, sku)`
  - `inventory_snapshot(tenant_id, sku) -> product(tenant_id, sku)`
  - `sales_order.tenant_id -> tenant.id`
  - `sales_order_item(tenant_id, order_id) -> sales_order(tenant_id, id_ext)`
  - `fact_sales_daily(tenant_id, sku) -> product(tenant_id, sku)`
  - `product_metrics_daily(tenant_id, sku) -> product(tenant_id, sku)`
  - `analysis_run.tenant_id -> tenant.id`
  - `product_profitability.analysis_run_id -> analysis_run.id` e `(tenant_id, sku) -> product`
  - `forecast_result.analysis_run_id -> analysis_run.id` e `(tenant_id, sku) -> product`
  - `recommendation.analysis_run_id -> analysis_run.id` e `(tenant_id, sku) -> product`
- Índices recomendados:
  - `(tenant_id, date)` em tabelas diárias; `(tenant_id, sku)` em tabelas por produto; únicos: `(tenant_id, source)` em `connector_config` e `(tenant_id, id_ext)` em `sales_order`.
- Particionamento sugerido:
  - Por mês e `tenant_id` em `sales_order`, `sales_order_item`, `fact_sales_daily`.

