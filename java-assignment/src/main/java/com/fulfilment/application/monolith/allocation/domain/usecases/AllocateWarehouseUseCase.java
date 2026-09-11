package com.fulfilment.application.monolith.allocation.domain.usecases;

import com.fulfilment.application.monolith.allocation.adapters.database.AllocationRepository;
import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import com.fulfilment.application.monolith.products.adapters.database.ProductRepository;
import com.fulfilment.application.monolith.stores.domain.models.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AllocateWarehouseUseCase {

  static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  static final int MAX_WAREHOUSES_PER_STORE = 3;
  static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  @Inject AllocationRepository allocationRepository;
  @Inject WarehouseRepository warehouseRepository;
  @Inject ProductRepository productRepository;

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

    if (allocationRepository.alreadyAllocated(warehouseBusinessUnitCode, productId, storeId)) {
      throw new BadRequestException("This warehouse-product-store allocation already exists.");
    }

    long warehousesForProductInStore =
        allocationRepository.findByProductAndStore(productId, storeId).stream()
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

    long distinctWarehousesForStore = allocationRepository.countDistinctWarehousesByStore(storeId);
    boolean storeAlreadyHasThisWarehouse =
        allocationRepository
                .find(
                    "storeId = ?1 and warehouseBusinessUnitCode = ?2",
                    storeId,
                    warehouseBusinessUnitCode)
                .count()
            > 0;
    if (!storeAlreadyHasThisWarehouse && distinctWarehousesForStore >= MAX_WAREHOUSES_PER_STORE) {
      throw new BadRequestException(
          "Store "
              + storeId
              + " is already served by "
              + MAX_WAREHOUSES_PER_STORE
              + " different warehouses.");
    }

    long distinctProductsInWarehouse =
        allocationRepository.countDistinctProductsByWarehouse(warehouseBusinessUnitCode);
    boolean warehouseAlreadyHasThisProduct =
        allocationRepository
                .find(
                    "warehouseBusinessUnitCode = ?1 and productId = ?2",
                    warehouseBusinessUnitCode,
                    productId)
                .count()
            > 0;
    if (!warehouseAlreadyHasThisProduct
        && distinctProductsInWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
      throw new BadRequestException(
          "Warehouse '"
              + warehouseBusinessUnitCode
              + "' already stores "
              + MAX_PRODUCTS_PER_WAREHOUSE
              + " types of products.");
    }

    WarehouseAllocation allocation =
        new WarehouseAllocation(warehouseBusinessUnitCode, productId, storeId);
    allocationRepository.persist(allocation);
    return allocation;
  }
}
