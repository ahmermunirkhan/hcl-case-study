package com.fulfilment.application.monolith.allocation.adapters.database;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AllocationRepository implements PanacheRepository<WarehouseAllocation> {

  public List<WarehouseAllocation> findByProductAndStore(Long productId, Long storeId) {
    return list("productId = ?1 and storeId = ?2", productId, storeId);
  }

  public long countDistinctWarehousesByStore(Long storeId) {
    return find("storeId = ?1", storeId)
        .stream()
        .map(a -> a.warehouseBusinessUnitCode)
        .distinct()
        .count();
  }

  public long countDistinctProductsByWarehouse(String warehouseBusinessUnitCode) {
    return find("warehouseBusinessUnitCode = ?1", warehouseBusinessUnitCode)
        .stream()
        .map(a -> a.productId)
        .distinct()
        .count();
  }

  public boolean alreadyAllocated(String warehouseBusinessUnitCode, Long productId, Long storeId) {
    return count(
            "warehouseBusinessUnitCode = ?1 and productId = ?2 and storeId = ?3",
            warehouseBusinessUnitCode,
            productId,
            storeId)
        > 0;
  }
}
