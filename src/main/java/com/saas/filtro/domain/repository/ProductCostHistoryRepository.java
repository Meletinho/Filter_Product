package com.saas.filtro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saas.filtro.domain.model.ProductCostHistory;
import com.saas.filtro.domain.model.ProductCostHistoryId;

public interface ProductCostHistoryRepository extends JpaRepository<ProductCostHistory, ProductCostHistoryId> {

}
