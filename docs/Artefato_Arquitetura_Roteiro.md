# Artefato de Arquitetura e Roteiro — SaaS "Filtrador de Oportunidades"

## 1. Objetivo e Escopo
- Ingerir catálogos e históricos de vendas de múltiplas fontes (ERPs/E-commerces), calcular lucratividade real por item e prever demanda futura para recomendar reposição e promoções.
- Requisitos-chave: multi-tenant, escalabilidade de ingestão, cálculos confiáveis e auditáveis, resultados rápidos para leitura, observabilidade completa e resiliência nas integrações externas.

## 2. Arquitetura de Solução (Hexagonal / Ports & Adapters)
- Camadas:
  - Domain: modelos e regras de negócio (lucro, agregações, forecast); imutáveis, sem dependências de framework.
  - Application: casos de uso (ingestão, agregação, análise, recomendação); orquestra serviços, transações e validações.
  - Integration: adapters para ERPs/e-commerces; clientes HTTP resilientes; DTOs e mapeamentos.
  - Infrastructure: persistência (repositories), Spring Batch (Jobs/Steps), cache, migrations, observabilidade.
- Módulos/pacotes sugeridos:
  - com.saas.filtro.domain.[product|sales|analysis|forecast]
  - com.saas.filtro.application.[ingestion|aggregation|analysis|recommendation]
  - com.saas.filtro.integration.[erpA|ecommerceB].client
  - com.saas.filtro.infrastructure.[persistence|batch|config|observability]
- Multi-tenant:
  - Base compartilhada com tenant_id obrigatório em todas as tabelas; índices compostos por tenant_id.
  - TenantContext + filtros em repositories; isolamento de credenciais por tenant e cifragem.
  - Auditoria por análise: analysis_run cria trilha ponta-a-ponta.

## 3. Processamento (Spring Batch vs Async)
- Use Spring Batch para ETLs volumosos:
  - ItemReader (HTTP paginado), ItemProcessor (validação + mapeamento), ItemWriter (upsert/bulk).
  - Chunk size ajustável (500–2000); commit por chunk; JobRepository para restart/resume.
  - Particionamento por janela temporal/canal/página; TaskExecutor com limites; paralelismo controlado.
  - Idempotência via JobParameters (tenant_id, source, date_window, page_token).
- Orquestração:
  - @Scheduled dispara Jobs por tenant/fonte com parâmetros; respeita rate_limit; webhooks opcionais.
- @Async:
  - Apenas para sub-tarefas curtas; não usar como substituto do Batch.

## 4. Modelagem de Dados (Desenho, Índices, Particionamento)
- Operacional:
  - tenant(id, name, status)
  - connector_config(id, tenant_id, source, auth, rate_limit_qps, status, last_sync_at)
  - product(tenant_id, sku, name, brand, category, attributes_json, status)
  - product_cost_history(tenant_id, sku, cost, fees_json, valid_from, valid_to) — SCD Tipo 2
  - price_history(tenant_id, sku, channel, price, valid_from, valid_to)
  - inventory_snapshot(tenant_id, sku, location, qty, snapshot_date)
  - sales_order(tenant_id, id_ext, channel, order_date, total, currency, status)
  - sales_order_item(tenant_id, order_id, sku, qty, unit_price, discounts, taxes, returned_qty)
- Fatos e agregados:
  - fact_sales_daily(tenant_id, sku, channel, date, units, gross_revenue, returns, net_revenue, cogs_est)
  - product_metrics_daily(tenant_id, sku, date, price_min, price_max, moving_avg_units, seasonality_idx, stockout_flag)
- Resultados e auditoria:
  - analysis_run(id, tenant_id, started_at, finished_at, scope_json, status, metrics_json)
  - product_profitability(tenant_id, analysis_run_id, sku, period_start, period_end, revenue, cogs, fees, shipping, taxes, returns_cost, gross_margin_pct, profit)
  - forecast_result(tenant_id, analysis_run_id, sku, horizon_days, model_type, params_json, forecast_series_json, mape, mae)
  - recommendation(tenant_id, analysis_run_id, sku, action, quantity, pricing_hint, rationale, confidence_pct, valid_until)
- Performance:
  - Particionar por tenant_id e mês nas tabelas grandes; índices principais: (tenant_id, date), (tenant_id, sku), (tenant_id, channel).
  - Denormalizar resultados para leitura rápida; materialized views para dashboards/queries frequentes.
  - Avaliar TimescaleDB/ClickHouse conforme crescimento; manter Postgres inicialmente.

## 5. Integração com APIs Externas
- Cliente HTTP: WebClient (preferível ao RestTemplate).
  - Timeouts: connect/read/write; pool de conexões; maxInMemorySize para payloads grandes.
  - Pode operar de forma bloqueante no service; vantagem em backpressure e pooling.
- Resiliência (Resilience4j):
  - Retry com backoff exponencial + jitter (3–5 tentativas) para 5xx, 429, timeouts.
  - CircuitBreaker por fonte/endpoint; Bulkhead para isolar concorrência; RateLimiter por rate_limit_qps.
- Paginação:
  - Persistir next_page_token por execução; retomar no último token em falha.
  - Idempotência com chave composta (tenant_id, source, date_window, page_token); ETag/If-None-Match quando houver.
- Mapeamento e validação:
  - DTOs por fonte com jakarta.validation; conversão via MapStruct para modelos internos.
- Testes de contrato:
  - WireMock para simular APIs; Testcontainers para Postgres; casos de retry/timeout/paginação.

## 6. Motor de Cálculo (Lucro, Features, Forecast, Recomendações)
- ProfitService:
  - net_revenue = Σ(unit_price * qty) − discounts − returns_refund.
  - COGS casado por data (SCD) e canal; incluir custos de devoluções/avarias.
  - Fees por canal (gateway/marketplace) e políticas por tenant; frete/impostos parametrizáveis.
  - profit = net_revenue − cogs − fees − shipping − taxes − returns_cost; gross_margin_pct = profit / net_revenue.
- FeatureEngineeringService:
  - Séries diárias por SKU/canal: rolling mean/median, seasonality index (decomposição), flags de promo/stockout/outliers.
- ForecastService (Strategy):
  - MovingAverage (baseline), Holt-Winters para sazonalidade, Croston/TSB para demanda intermitente.
  - Seleção automática por heurística (coef. de variação e intermitência); métricas MAPE/MAE persistidas.
- RecommendationService:
  - Reposição: qty = forecast(horizonte_lead_time) − estoque + segurança; ação se profit > threshold e risco de ruptura.
  - Promoção: excesso de estoque vs forecast e margem; sugerir janela/preço alvo; confiança pela estabilidade da série.
- Testabilidade:
  - Serviços puros; injeção de políticas e repositórios; fixtures de séries e casos com promo/stockout.

## 7. API Interna
- GET /tenants/{id}/products/{sku}/profitability?from&to
- GET /tenants/{id}/products/{sku}/forecast?horizon
- GET /tenants/{id}/recommendations?status
- GET /tenants/{id}/analysis-runs/{runId}
- POST /tenants/{id}/ingestions/{source}/run?from&to — dispara Job
- POST /tenants/{id}/analysis/run?scope — agrega + calcula + forecast

## 8. Observabilidade e Operação
- Logs estruturados com tenant_id, source, analysis_run_id; correlação de traces.
- Métricas: tempo por fase, throughput, taxa de erro, retries, QPS por fonte; MAPE/MAE por SKU/horizonte.
- Alertas: falhas de Jobs, partições estagnadas, throttle constante, buracos de dados por dia.

## 9. Segurança e Governança
- Segredos por tenant em vault; credenciais cifradas; escopo mínimo nas APIs.
- Catálogo de dados: mapeamento de campos por fonte; impostos/fees por canal; versionamento de políticas.
- Auditoria: analysis_run como trilha; outbox para eventos quando necessário.

## 10. Passo a Passo de Construção
- Semana 1 — Fundamentos
  - Projeto Spring Boot; camadas/pacotes; WebClient com timeouts/pool; Resilience4j.
  - Migrations: tenant, connector_config, product, sales_order, sales_order_item, fact_sales_daily, analysis_run.
  - TenantContext e repositories com filtro por tenant_id; observabilidade (Micrometer/Prometheus).
- Semana 2 — Ingestão
  - Job Batch: catálogo e pedidos (Reader HTTP → Processor valida/mapeia → Writer upsert).
  - Particionamento por janela/canal; chunkSize tuning; TaskExecutor com limites; @Scheduled por tenant/fonte.
  - Testes: WireMock, Testcontainers; cenários de retry/timeout/paginação.
- Semana 3 — Agregação e Qualidade
  - ETL para fact_sales_daily e product_metrics_daily; índices/partições; materialized views quando necessário.
  - Verificações de qualidade: lacunas, duplicidades, outliers; métricas em analysis_run.metrics_json.
- Semana 4 — Lucro (MVP)
  - ProfitService com políticas por canal; persistir product_profitability por período; endpoints de leitura.
  - Testes unitários: descontos, taxas, devoluções.
- Semana 5 — Forecast (MVP)
  - ForecastService: MovingAverage + Holt-Winters; seleção automática; persistir forecast_result + métricas.
  - Endpoint /forecast; validar MAPE/MAE por SKU/horizonte.
- Semana 6 — Recomendações
  - RecommendationService combinando forecast + margem + estoque + lead time; persistir recommendation.
  - Endpoint para listar recomendações; filtros por ação/confiança.
- Semana 7 — Hardening
  - Paralelismo, rate limit e backoff com jitter; reprocessamentos por janela.
  - Cache quente, materializações; dashboards e alertas; custos por tenant.

## 11. Critérios de Aceite por Fase
- Ingestão: retomável, respeita rate_limit, paginação robusta, auditoria em analysis_run.
- Agregação: consultas diárias < 200ms nos filtros principais por tenant/sku.
- Lucro: determinístico, políticas configuráveis; validação por amostras representativas.
- Forecast: MAPE baseline documentado; séries intermitentes tratadas; parâmetros persistidos por SKU.
- Recomendações: ações com confiança e racional claro; lista paginada; SLA de atualização por tenant.

## 12. Decisões de Arquitetura (Rationale)
- Spring Batch para ETLs volumosos; @Scheduled para orquestração.
- WebClient + Resilience4j para clientes externos, operando bloqueante se necessário.
- Resultados denormalizados para leitura rápida; particionamento por tenant e data; materialized views.
- Auditoria central em analysis_run e idempotência por JobParameters.

## 13. Roadmap Evolutivo
- Troca de modelo por SKU via A/B; incorporar promoções, feriados e preço dinâmico.
- Mensageria para eventos (ex.: Kafka) quando houver necessidade de reações em tempo real.
- Avaliar TimescaleDB/ClickHouse/Redis para caching e séries, conforme escala.

## 14. Pré-Requisitos e Setup de Ambiente
- Ferramentas: JDK 21, Git, Docker Desktop, Postgres 16, IDE (IntelliJ/VS Code), Postman/Insomnia.
- Build: escolher Maven (recomendado) ou Gradle. Padronizar com wrapper.
- Projeto: Spring Boot 3.3.x, Java 21.
- Dependências (sem código, apenas lista para adicionar no build):
  - spring-boot-starter-webflux (WebClient)
  - resilience4j-spring-boot3 (retry/circuit/bulkhead/rate limiter)
  - spring-boot-starter-batch (Spring Batch)
  - spring-boot-starter-data-jpa (ou JDBC conforme escolha)
  - flyway-core (migrations)
  - micrometer-registry-prometheus (métricas)
  - mapstruct (mapeamento DTOs → modelos internos)
  - jackson, jakarta-validation
  - testcontainers-postgresql, wiremock-jre8 (testes)
- Ambientes:
  - Dev: banco local `saas_dev`, usuário/app com permissões; variáveis em `.env.local`.
  - Test: banco isolado; rodar com Testcontainers.
  - Prod: banco gerenciado, backups e políticas de retenção.

## 15. Estrutura de Diretórios e Pacotes (a criar)
- `src/main/java/com/saas/filtro/domain/*` — modelos e regras.
- `src/main/java/com/saas/filtro/application/*` — casos de uso.
- `src/main/java/com/saas/filtro/integration/*` — clientes de APIs externas, DTOs.
- `src/main/java/com/saas/filtro/infrastructure/*` — batch, persistência, configs, observabilidade.
- `src/main/resources/db/migration` — scripts Flyway (V1__*.sql ...).
- `docs/` — artefatos e decisões de arquitetura.

## 16. Banco de Dados — Migrations a Criar (ordem sugerida)
- V1__base_tenant.sql:
  - Tabelas: tenant(id, name, status), connector_config(id, tenant_id, source, auth_enc, rate_limit_qps, status, last_sync_at).
  - Índices: (tenant_id), únicos por (tenant_id, source).
- V2__catalog_sales.sql:
  - product(tenant_id, sku, name, brand, category, attributes_json, status) — índice (tenant_id, sku).
  - product_cost_history(tenant_id, sku, cost, fees_json, valid_from, valid_to) — SCD tipo 2; índice (tenant_id, sku, valid_from).
  - price_history(tenant_id, sku, channel, price, valid_from, valid_to) — índice (tenant_id, sku, channel, valid_from).
  - inventory_snapshot(tenant_id, sku, location, qty, snapshot_date) — índice (tenant_id, sku, snapshot_date).
- V3__orders.sql:
  - sales_order(tenant_id, id_ext, channel, order_date, total, currency, status) — índice (tenant_id, order_date), único (tenant_id, id_ext).
  - sales_order_item(tenant_id, order_id, sku, qty, unit_price, discounts, taxes, returned_qty) — índice (tenant_id, order_id), (tenant_id, sku).
- V4__facts_aggregates.sql:
  - fact_sales_daily(tenant_id, sku, channel, date, units, gross_revenue, returns, net_revenue, cogs_est) — índice (tenant_id, date, sku).
  - product_metrics_daily(tenant_id, sku, date, price_min, price_max, moving_avg_units, seasonality_idx, stockout_flag).
- V5__analysis_results.sql:
  - analysis_run(id, tenant_id, started_at, finished_at, scope_json, status, metrics_json) — índice (tenant_id, started_at).
  - product_profitability(tenant_id, analysis_run_id, sku, period_start, period_end, revenue, cogs, fees, shipping, taxes, returns_cost, gross_margin_pct, profit) — índice (tenant_id, period_start, sku).
  - forecast_result(tenant_id, analysis_run_id, sku, horizon_days, model_type, params_json, forecast_series_json, mape, mae) — índice (tenant_id, sku).
  - recommendation(tenant_id, analysis_run_id, sku, action, quantity, pricing_hint, rationale, confidence_pct, valid_until) — índice (tenant_id, sku, valid_until).
- Particionamento: por mês e `tenant_id` em `sales_order`, `sales_order_item`, `fact_sales_daily`.

## 17. Configuração (a criar) — Infraestrutura
- `WebClientConfig`: timeouts (connect/read/write), pool de conexões, maxInMemorySize.
- `ResilienceConfig`: perfis por fonte (retry: tentativas/backoff/jitter; circuit breaker thresholds; bulkhead; rate limiter por QPS).
- `BatchConfig`: `JobRepository`, `TaskExecutor` com limites, transacionalidade por chunk.
- `TenantContext`: resolver tenant a partir de credencial/headers; aplicação de filtros em repositories.
- `ObservabilityConfig`: Micrometer + Prometheus; nomes de métricas por tenant/fonte/job.

## 18. Jobs Batch — Definições e Parâmetros
- `ImportCatalogJob`:
  - Parâmetros: tenant_id, source, page_token_inicial, date_window.
  - Steps: Reader (HTTP paginado) → Processor (validação/mapeamento) → Writer (upsert catálogo, SCD custo/preço).
  - Chunk: 1000 (ajustar conforme memória/IO); Partition por página/categoria.
- `ImportSalesJob`:
  - Parâmetros: tenant_id, source, date_from, date_to.
  - Steps: Reader (paginado por pedidos) → Processor → Writer (orders + items) com dedupe `id_ext`.
  - Partition por janelas de datas e canal.
- `AggregateDailyJob`:
  - Parâmetros: tenant_id, date_from, date_to.
  - Steps: gerar `fact_sales_daily` e `product_metrics_daily` (rolling, min/max, flags).
- `ProfitCalculationJob`:
  - Parâmetros: tenant_id, period_start, period_end, analysis_run_id.
  - Steps: aplicar políticas por canal; persistir `product_profitability`.
- `ForecastJob`:
  - Parâmetros: tenant_id, horizon_days, analysis_run_id.
  - Steps: seleção de modelo por SKU; persistir `forecast_result` + métricas.
- `RecommendationJob`:
  - Parâmetros: tenant_id, analysis_run_id, lead_time_days, safety_stock_policy.
  - Steps: gerar ações de reposição/promo; persistir `recommendation`.
- Orquestração: `@Scheduled` por tenant/fonte; retentativas com Resilience4j; reprocessamento por janela.

## 19. Integrações — Ports/Adapters (sem código)
- Ports (interfaces conceituais):
  - `CatalogPort`: `fetchProductsPage(tenantId, source, pageToken)` retorna lista de DTOs.
  - `OrdersPort`: `fetchOrders(tenantId, source, from, to, pageToken)`.
  - `InventoryPort`: `fetchInventorySnapshot(tenantId, source, date)`.
- Adapters:
  - `WebClient` com Resilience4j; validação via `jakarta.validation`.
  - MapStruct para conversão DTO → modelo interno; mapeadores puros.
- Paginação e rate limit: persistir `next_page_token` por execução; respeitar QPS por `connector_config`.

## 20. Serviços de Negócio — Contratos e Responsabilidades
- `ProfitService`: calcular receita líquida, COGS por data/canal, fees, frete, impostos, devoluções; persistir `product_profitability`.
- `FeatureEngineeringService`: gerar features (rolling mean/median, sazonalidade, stockout/promo flags, outliers).
- `ForecastService`: aplicar Strategy (MovingAverage, Holt-Winters, Croston/TSB); avaliar MAPE/MAE; persistir `forecast_result`.
- `RecommendationService`: combinar forecast + margem + estoque + lead time → ações com confiança.
- `AnalysisOrchestrator`: coordenar rodadas `analysis_run` ponta-a-ponta.

## 21. API — Endpoints a Implementar
- `POST /tenants/{id}/ingestions/{source}/run?from&to` — dispara `ImportCatalogJob`/`ImportSalesJob` conforme escopo.
- `POST /tenants/{id}/analysis/run?scope` — dispara Aggregate/Profit/Forecast/Recommendation e retorna `analysis_run_id`.
- `GET /tenants/{id}/products/{sku}/profitability?from&to` — leitura.
- `GET /tenants/{id}/products/{sku}/forecast?horizon` — leitura.
- `GET /tenants/{id}/recommendations?status` — leitura.
- Segurança: autenticação e escopo por tenant; paginação e filtros; rate limit por cliente.

## 22. Observabilidade — Implementação
- Logs estruturados: incluir `tenant_id`, `source`, `analysis_run_id`, `jobName`, `stepName`.
- Métricas: tempo por job/step, throughput, taxa de erro, retries; MAPE/MAE por SKU/horizonte.
- Tracing: OpenTelemetry (opcional) para correlacionar ingestão → agregação → análise.
- Dashboards: Prometheus/Grafana com painéis por tenant/fonte.

## 23. Segurança e Gestão de Segredos
- Guardar credenciais por tenant em Vault/Secrets Manager; cifrar `auth_enc` em `connector_config`.
- Aplicar TLS nas chamadas externas; sanitizar logs (sem segredos).
- Políticas de acesso: escopo mínimo nas APIs externas; rotação de segredos.

## 24. Testes — Plano Detalhado
- Unitários: ProfitService (descontos, taxas, devoluções), ForecastService (series regulares/intermitentes), FeatureEngineering.
- Integração: clientes WebClient com WireMock e Resilience4j simulando timeouts/5xx/429/paginação.
- Banco: repositórios e ETLs com Testcontainers Postgres; validar índices e partições.
- Contrato: payloads de cada fonte; versões e campos opcionais.
- Performance: carga para `fact_sales_daily` e leitura de endpoints críticos; metas de latência.
- Qualidade de dados: detectores de lacunas/duplicidades/outliers com relatórios em `analysis_run.metrics_json`.

## 25. Qualidade de Dados — Regras
- Lacunas de vendas: dias sem registros para SKUs ativos → sinalizar.
- Duplicidade de pedidos: `id_ext` único por tenant; dedupe.
- Outliers: preço e quantidade fora de z-score parametrizado → marcar.
- Stockout/promo flags: derivar de inventário e variação de preço.

## 26. Operação — Runbooks
- Falha de ingestão: identificar `analysis_run_id`, verificar retries e circuit; reexecutar janela com `JobParameters` idênticos.
- Throttle constante (429): reduzir paralelismo, aumentar backoff e respeitar `rate_limit_qps`.
- Buracos de dados: reprocessar `AggregateDailyJob` para janela afetada.
- Migrações: aplicar Flyway em startup; rollback via versões subsequentes.

## 27. Deploy e Pipeline CI/CD
- Stages: build → testes → verificação estática → pacote Docker → migrações → deploy.
- Variáveis: `DB_URL`, `TENANT_SCOPE`, segredos de fontes.
- Healthchecks: readiness/liveness; verificação de jobs pendentes.
- Rollout: azul/verde ou canário; observabilidade validando métricas pós-deploy.

## 28. Checklist de Implementação (marcar cada entrega)
- Ambiente e dependências configurados.
- Migrations V1–V5 aplicadas.
- WebClient + Resilience4j configurados.
- Jobs `ImportCatalog` e `ImportSales` funcionais com retomada.
- Agregação diária populando `fact_sales_daily` e `product_metrics_daily`.
- ProfitService persistindo `product_profitability` e endpoints de leitura.
- ForecastService com baseline e sazonalidade; métricas persistidas.
- RecommendationService gerando ações com confiança.
- Observabilidade com métricas, logs e alertas.
- Segurança de segredos e rate limit por tenant.

## 29. Cronograma Detalhado (dia a dia, exemplo)
- Dia 1–2: setup, dependências, Flyway V1–V2, WebClient/Resilience configs.
- Dia 3–4: `ImportCatalogJob` (Reader/Processor/Writer, SCD custo/preço).
- Dia 5–6: `ImportSalesJob` + dedupe; orquestração `@Scheduled`.
- Dia 7–8: `AggregateDailyJob` e `product_metrics_daily`.
- Dia 9–10: ProfitService + endpoints.
- Dia 11–12: ForecastService (MovingAverage, Holt-Winters) + métricas.
- Dia 13: RecommendationService + endpoint.
- Dia 14: observabilidade, testes de carga, hardening.

## 30. Métricas e Critérios de Saída
- Ingestão: taxa de sucesso ≥ 99%, retries dentro de limite; tempo médio por página.
- Agregação: consultas < 200ms nos filtros principais.
- Lucro: validação por amostras e reconciliação com relatórios externos.
- Forecast: MAPE/MAE melhores que baseline; logs de parâmetros.
- Recomendações: precisão das ações (auditar eficácia ao longo de 4 semanas).

## 31. Riscos e Mitigações
- Variação de contratos externos: testes de contrato + feature flags por fonte.
- Volume extremo: particionamento/índices e materializações; considerar Timescale/ClickHouse.
- Intermitência de demanda: modelos apropriados (Croston/TSB) e políticas de segurança de estoque.
- Custos e taxas variáveis: SCD tipo 2 e políticas versionadas por tenant.
