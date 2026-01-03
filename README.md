
Filter Product

1. Resumo Executivo
   O sistema Filter Product é um SaaS "Filtrador de Oportunidades" projetado para alta escalabilidade e inteligência de negócios. Atualmente, encontra-se na fase de Bootstrap/Esqueleto, com uma fundação robusta em Spring Boot 3.3.x e Java 21, mas sem a implementação das regras de negócio complexas descritas no diagrama ER.

O diagrama ER revela que o projeto não é apenas um "filtro" simples, mas uma Plataforma de Inteligência de Varejo Multi-Tenant, capaz de ingestão de dados, cálculo de lucratividade, previsão de demanda (forecasting) e geração de recomendações automatizadas.

2. Stack Tecnológico (Confirmado vs. Necessário)
Camada	Tecnologia (Atual)	Capacidade Implicada
Runtime	Java 21 (LTS)	Threads Virtuais para alta concorrência na ingestão de dados.
Framework	Spring Boot 3.3.4	Base segura e moderna.
Ingestão	Spring Batch + WebFlux	Processamento massivo offline e ingestão de pedidos em tempo real.
Dados	PostgreSQL + Flyway	Suporte robusto a JSONB (para atributos dinâmicos) e séries temporais.
Resiliência	Resilience4j	Proteção contra falhas em integrações externas (Connectors).
Monitoramento	Micrometer + Prometheus	Visibilidade de métricas de negócio (ex: QPS de conectores).
