package com.saas.filtro.domain.repository;

import com.saas.filtro.domain.model.Product;
import com.saas.filtro.domain.model.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, ProductId> {
}
