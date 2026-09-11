package com.fulfilment.application.monolith.allocation.domain.usecases;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory test fake for {@link com.fulfilment.application.monolith.allocation.adapters.database.AllocationRepository}. */
public class InMemoryAllocationRepository {

  private final List<WarehouseAllocation> data = new ArrayList<>();
  private final AtomicLong idSeq = new AtomicLong(1);

  public void persist(WarehouseAllocation a) {
    a.id = idSeq.getAndIncrement();
    data.add(a);
  }

  public List<WarehouseAllocation> findByProductAndStore(Long productId, Long storeId) {
    return data.stream()
        .filter(a -> a.productId.equals(productId) && a.storeId.equals(storeId))
        .toList();
  }

  public long countDistinctWarehousesByStore(Long storeId) {
    return data.stream()
        .filter(a -> a.storeId.equals(storeId))
        .map(a -> a.warehouseBusinessUnitCode)
        .distinct()
        .count();
  }

  public long countDistinctProductsByWarehouse(String buCode) {
    return data.stream()
        .filter(a -> a.warehouseBusinessUnitCode.equals(buCode))
        .map(a -> a.productId)
        .distinct()
        .count();
  }

  public boolean alreadyAllocated(String buCode, Long productId, Long storeId) {
    return data.stream()
        .anyMatch(
            a ->
                a.warehouseBusinessUnitCode.equals(buCode)
                    && a.productId.equals(productId)
                    && a.storeId.equals(storeId));
  }

  public long countByStoreAndWarehouse(Long storeId, String buCode) {
    return data.stream()
        .filter(a -> a.storeId.equals(storeId) && a.warehouseBusinessUnitCode.equals(buCode))
        .count();
  }

  public long countByWarehouseAndProduct(String buCode, Long productId) {
    return data.stream()
        .filter(a -> a.warehouseBusinessUnitCode.equals(buCode) && a.productId.equals(productId))
        .count();
  }

  public List<WarehouseAllocation> all() {
    return List.copyOf(data);
  }
}
