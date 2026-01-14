package com.saas.filtro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saas.filtro.domain.model.PriceHistory;
import com.saas.filtro.domain.model.PriceHistoryId;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, PriceHistoryId> {

}
