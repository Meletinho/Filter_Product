-- =========================================
-- SCHEMA COMPLETO - FILTER PRODUCT SaaS
-- =========================================

-- 1. TENANT (Multi-tenancy Root)
CREATE TABLE tenant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    subscription_plan VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. PRODUCT (Catálogo de Produtos)
CREATE TABLE product (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    attributes_json JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, sku),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);

-- 3. PRODUCT_COST_HISTORY (SCD Type 2 - Histórico de Custos)
CREATE TABLE product_cost_history (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    cost DECIMAL(15, 2) NOT NULL,
    fees_json JSONB,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, sku, valid_from),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 4. PRICE_HISTORY (SCD Type 2 - Histórico de Preços por Canal)
CREATE TABLE price_history (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, sku, channel, valid_from),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 5. INVENTORY_SNAPSHOT (Snapshot Diário de Estoque)
CREATE TABLE inventory_snapshot (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    snapshot_date DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, sku, location, snapshot_date),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 6. SALES_ORDER (Pedidos de Venda)
CREATE TABLE sales_order (
    tenant_id UUID NOT NULL,
    order_id VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    order_date DATE NOT NULL,
    customer_id VARCHAR(100),
    total_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, order_id),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);

-- 7. SALES_ORDER_ITEM (Itens do Pedido)
CREATE TABLE sales_order_item (
    tenant_id UUID NOT NULL,
    order_id VARCHAR(100) NOT NULL,
    line_number INT NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    discounts DECIMAL(15, 2) DEFAULT 0,
    taxes DECIMAL(15, 2) DEFAULT 0,
    PRIMARY KEY (tenant_id, order_id, line_number),
    FOREIGN KEY (tenant_id, order_id) REFERENCES sales_order(tenant_id, order_id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE RESTRICT
);

-- 8. FACT_SALES_DAILY (Analytics - Vendas Agregadas Diárias)
CREATE TABLE fact_sales_daily (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    channel VARCHAR(50) NOT NULL,
    total_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0,
    total_cost DECIMAL(15, 2) NOT NULL DEFAULT 0,
    total_quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, sku, date, channel),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 9. PRODUCT_METRICS_DAILY (Métricas Diárias do Produto)
CREATE TABLE product_metrics_daily (
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    avg_stock INT,
    days_out_of_stock INT DEFAULT 0,
    return_rate DECIMAL(5, 2),
    PRIMARY KEY (tenant_id, sku, date),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 10. ANALYSIS_RUN (Registro de Análises Executadas)
CREATE TABLE analysis_run (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);

-- 11. PRODUCT_PROFITABILITY (Resultado da Análise de Lucratividade)
CREATE TABLE product_profitability (
    analysis_run_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    total_revenue DECIMAL(15, 2) NOT NULL,
    total_cost DECIMAL(15, 2) NOT NULL,
    profit_margin DECIMAL(5, 2) NOT NULL,
    recommendation_score DECIMAL(5, 2),
    PRIMARY KEY (analysis_run_id, tenant_id, sku),
    FOREIGN KEY (analysis_run_id) REFERENCES analysis_run(id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 12. FORECAST_RESULT (Previsão de Demanda - Machine Learning)
CREATE TABLE forecast_result (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    forecast_date DATE NOT NULL,
    predicted_demand INT NOT NULL,
    confidence_interval_lower INT,
    confidence_interval_upper INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 13. RECOMMENDATION (Recomendações Finais)
CREATE TABLE recommendation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    recommendation_type VARCHAR(50) NOT NULL,
    action VARCHAR(255) NOT NULL,
    priority INT NOT NULL,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (tenant_id, sku) REFERENCES product(tenant_id, sku) ON DELETE CASCADE
);

-- 14. CONNECTOR_CONFIG (Configurações de Integrações Externas)
CREATE TABLE connector_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    connector_type VARCHAR(50) NOT NULL,
    config_json JSONB NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);

-- ÍNDICES para otimização de queries

CREATE INDEX idx_product_tenant ON product(tenant_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_sales_order_date ON sales_order(order_date);
CREATE INDEX idx_sales_order_status ON sales_order(status);
CREATE INDEX idx_inventory_snapshot_date ON inventory_snapshot(snapshot_date);
CREATE INDEX idx_fact_sales_daily_date ON fact_sales_daily(date);
CREATE INDEX idx_product_profitability_score ON product_profitability(recommendation_score DESC);
