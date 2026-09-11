package com.fulfilment.application.monolith.allocation.domain.usecases;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import com.fulfilment.application.monolith.allocation.domain.ports.AllocationStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryAllocationRepository implements AllocationStore {

  private final List<WarehouseAllocation> data = new ArrayList<>();
  private final AtomicLong idSeq = new AtomicLong(1);

  @Override
  public void persist(WarehouseAllocation a) {
    a.id = idSeq.getAndIncrement();
    data.add(a);
  }

  @Override
  public List<WarehouseAllocation> getAll() {
    return List.copyOf(data);
  }

  @Override
  public List<WarehouseAllocation> findByWarehouse(String warehouseBusinessUnitCode) {
    return data.stream()
        .filter(a -> a.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode))
        .toList();
  }

  @Override
  public List<WarehouseAllocation> findByStore(Long storeId) {
    return data.stream().filter(a -> a.storeId.equals(storeId)).toList();
  }

  @Override
  public List<WarehouseAllocation> findByProductAndStore(Long productId, Long storeId) {
    return data.stream()
        .filter(a -> a.productId.equals(productId) && a.storeId.equals(storeId))
        .toList();
  }

  @Override
  public long countDistinctWarehousesByStore(Long storeId) {
    return data.stream()
        .filter(a -> a.storeId.equals(storeId))
        .map(a -> a.warehouseBusinessUnitCode)
        .distinct()
        .count();
  }

  @Override
  public long countDistinctProductsByWarehouse(String buCode) {
    return data.stream()
        .filter(a -> a.warehouseBusinessUnitCode.equals(buCode))
        .map(a -> a.productId)
        .distinct()
        .count();
  }

  @Override
  public boolean alreadyAllocated(String buCode, Long productId, Long storeId) {
    return data.stream()
        .anyMatch(
            a ->
                a.warehouseBusinessUnitCode.equals(buCode)
                    && a.productId.equals(productId)
                    && a.storeId.equals(storeId));
  }

  @Override
  public boolean isWarehouseInStore(String warehouseBusinessUnitCode, Long storeId) {
    return data.stream()
        .anyMatch(
            a ->
                a.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode)
                    && a.storeId.equals(storeId));
  }

  @Override
  public boolean isProductInWarehouse(String warehouseBusinessUnitCode, Long productId) {
    return data.stream()
        .anyMatch(
            a ->
                a.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode)
                    && a.productId.equals(productId));
  }
}
