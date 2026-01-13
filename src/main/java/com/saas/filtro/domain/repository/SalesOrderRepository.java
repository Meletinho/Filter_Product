package com.saas.filtro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saas.filtro.domain.model.SalesOrder;
import com.saas.filtro.domain.model.SalesOrderId;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, SalesOrderId> {

}
