package com.saas.filtro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saas.filtro.domain.model.SalesOrderItem;
import com.saas.filtro.domain.model.SalesOrderItemId;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, SalesOrderItemId> {

}
