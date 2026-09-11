package com.fulfilment.application.monolith.allocation.domain.ports;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import java.util.List;

public interface AllocationStore {

  void persist(WarehouseAllocation allocation);

  List<WarehouseAllocation> getAll();

  List<WarehouseAllocation> findByWarehouse(String warehouseBusinessUnitCode);

  List<WarehouseAllocation> findByStore(Long storeId);

  List<WarehouseAllocation> findByProductAndStore(Long productId, Long storeId);

  long countDistinctWarehousesByStore(Long storeId);

  long countDistinctProductsByWarehouse(String warehouseBusinessUnitCode);

  boolean alreadyAllocated(String warehouseBusinessUnitCode, Long productId, Long storeId);

  boolean isWarehouseInStore(String warehouseBusinessUnitCode, Long storeId);

  boolean isProductInWarehouse(String warehouseBusinessUnitCode, Long productId);
}
