package com.fulfilment.application.monolith.allocation.adapters.database;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import com.fulfilment.application.monolith.allocation.domain.ports.AllocationStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AllocationRepository implements AllocationStore, PanacheRepository<WarehouseAllocation> {

  @Override
  public void persist(WarehouseAllocation allocation) {
    getEntityManager().persist(allocation);
  }

  @Override
  public List<WarehouseAllocation> getAll() {
    return listAll();
  }

  @Override
  public List<WarehouseAllocation> findByWarehouse(String warehouseBusinessUnitCode) {
    return list("warehouseBusinessUnitCode = ?1", warehouseBusinessUnitCode);
  }

  @Override
  public List<WarehouseAllocation> findByStore(Long storeId) {
    return list("storeId = ?1", storeId);
  }

  @Override
  public List<WarehouseAllocation> findByProductAndStore(Long productId, Long storeId) {
    return list("productId = ?1 and storeId = ?2", productId, storeId);
  }

  @Override
  public long countDistinctWarehousesByStore(Long storeId) {
    return find("storeId = ?1", storeId)
        .stream()
        .map(a -> a.warehouseBusinessUnitCode)
        .distinct()
        .count();
  }

  @Override
  public long countDistinctProductsByWarehouse(String warehouseBusinessUnitCode) {
    return find("warehouseBusinessUnitCode = ?1", warehouseBusinessUnitCode)
        .stream()
        .map(a -> a.productId)
        .distinct()
        .count();
  }

  @Override
  public boolean alreadyAllocated(String warehouseBusinessUnitCode, Long productId, Long storeId) {
    return count(
            "warehouseBusinessUnitCode = ?1 and productId = ?2 and storeId = ?3",
            warehouseBusinessUnitCode,
            productId,
            storeId)
        > 0;
  }

  @Override
  public boolean isWarehouseInStore(String warehouseBusinessUnitCode, Long storeId) {
    return count(
            "storeId = ?1 and warehouseBusinessUnitCode = ?2",
            storeId,
            warehouseBusinessUnitCode)
        > 0;
  }

  @Override
  public boolean isProductInWarehouse(String warehouseBusinessUnitCode, Long productId) {
    return count(
            "warehouseBusinessUnitCode = ?1 and productId = ?2",
            warehouseBusinessUnitCode,
            productId)
        > 0;
  }
}
