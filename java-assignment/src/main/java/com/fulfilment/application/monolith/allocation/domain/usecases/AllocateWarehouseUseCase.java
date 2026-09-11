package com.fulfilment.application.monolith.allocation.domain.usecases;

import com.fulfilment.application.monolith.allocation.domain.events.AllocationCreatedEvent;
import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import com.fulfilment.application.monolith.allocation.domain.ports.AllocationStore;
import com.fulfilment.application.monolith.products.adapters.database.ProductRepository;
import com.fulfilment.application.monolith.stores.domain.models.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AllocateWarehouseUseCase {

  static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  static final int MAX_WAREHOUSES_PER_STORE = 3;
  static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  @Inject AllocationStore allocationStore;
  @Inject WarehouseRepository warehouseRepository;
  @Inject ProductRepository productRepository;
  @Inject Event<AllocationCreatedEvent> allocationCreatedEvent;

  @Transactional
  public WarehouseAllocation allocate(
      String warehouseBusinessUnitCode, Long productId, Long storeId) {

    if (warehouseRepository.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
      throw new NotFoundException(
          "Warehouse '" + warehouseBusinessUnitCode + "' not found or archived.");
    }

    if (productRepository.findById(productId) == null) {
      throw new NotFoundException("Product with id " + productId + " not found.");
    }

    if (Store.findById(storeId) == null) {
      throw new NotFoundException("Store with id " + storeId + " not found.");
    }

    if (allocationStore.alreadyAllocated(warehouseBusinessUnitCode, productId, storeId)) {
      throw new BadRequestException("This warehouse-product-store allocation already exists.");
    }

    long warehousesForProductInStore =
        allocationStore.findByProductAndStore(productId, storeId).stream()
            .map(a -> a.warehouseBusinessUnitCode)
            .distinct()
            .count();
    if (warehousesForProductInStore >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      throw new BadRequestException(
          "Product "
              + productId
              + " is already allocated to "
              + MAX_WAREHOUSES_PER_PRODUCT_PER_STORE
              + " warehouses for store "
              + storeId
              + ".");
    }

    if (!allocationStore.isWarehouseInStore(warehouseBusinessUnitCode, storeId)
        && allocationStore.countDistinctWarehousesByStore(storeId) >= MAX_WAREHOUSES_PER_STORE) {
      throw new BadRequestException(
          "Store "
              + storeId
              + " is already served by "
              + MAX_WAREHOUSES_PER_STORE
              + " different warehouses.");
    }

    if (!allocationStore.isProductInWarehouse(warehouseBusinessUnitCode, productId)
        && allocationStore.countDistinctProductsByWarehouse(warehouseBusinessUnitCode)
            >= MAX_PRODUCTS_PER_WAREHOUSE) {
      throw new BadRequestException(
          "Warehouse '"
              + warehouseBusinessUnitCode
              + "' already stores "
              + MAX_PRODUCTS_PER_WAREHOUSE
              + " types of products.");
    }

    WarehouseAllocation allocation =
        new WarehouseAllocation(warehouseBusinessUnitCode, productId, storeId);
    allocationStore.persist(allocation);
    allocationCreatedEvent.fire(new AllocationCreatedEvent(allocation));
    return allocation;
  }
}
