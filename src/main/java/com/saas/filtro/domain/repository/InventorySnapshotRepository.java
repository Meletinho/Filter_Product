package com.saas.filtro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saas.filtro.domain.model.InventorySnapshot;
import com.saas.filtro.domain.model.InventorySnapshotId;

public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, InventorySnapshotId> {

}
