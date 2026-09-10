package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AssociateFulfillmentUseCase {

  static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  static final int MAX_WAREHOUSES_PER_STORE = 3;
  static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  @Inject FulfillmentRepository fulfillmentRepository;
  @Inject WarehouseRepository warehouseRepository;
  @Inject ProductRepository productRepository;

  @Transactional
  public FulfillmentAssociation associate(
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

    if (fulfillmentRepository.alreadyAssociated(warehouseBusinessUnitCode, productId, storeId)) {
      throw new BadRequestException(
          "This warehouse-product-store association already exists.");
    }

    long warehousesForProductInStore =
        fulfillmentRepository.findByProductAndStore(productId, storeId).stream()
            .map(a -> a.warehouseBusinessUnitCode)
            .distinct()
            .count();
    if (warehousesForProductInStore >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      throw new BadRequestException(
          "Product "
              + productId
              + " is already fulfilled by "
              + MAX_WAREHOUSES_PER_PRODUCT_PER_STORE
              + " warehouses for store "
              + storeId
              + ".");
    }

    long distinctWarehousesForStore =
        fulfillmentRepository.countDistinctWarehousesByStore(storeId);
    boolean storeAlreadyHasThisWarehouse =
        fulfillmentRepository.findByProductAndStore(productId, storeId).stream()
            .anyMatch(a -> a.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode))
            || fulfillmentRepository
                .find("storeId = ?1 and warehouseBusinessUnitCode = ?2", storeId, warehouseBusinessUnitCode)
                .count()
            > 0;
    if (!storeAlreadyHasThisWarehouse
        && distinctWarehousesForStore >= MAX_WAREHOUSES_PER_STORE) {
      throw new BadRequestException(
          "Store "
              + storeId
              + " is already fulfilled by "
              + MAX_WAREHOUSES_PER_STORE
              + " different warehouses.");
    }

    long distinctProductsInWarehouse =
        fulfillmentRepository.countDistinctProductsByWarehouse(warehouseBusinessUnitCode);
    boolean warehouseAlreadyHasThisProduct =
        fulfillmentRepository
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

    FulfillmentAssociation association =
        new FulfillmentAssociation(warehouseBusinessUnitCode, productId, storeId);
    fulfillmentRepository.persist(association);
    return association;
  }
}
