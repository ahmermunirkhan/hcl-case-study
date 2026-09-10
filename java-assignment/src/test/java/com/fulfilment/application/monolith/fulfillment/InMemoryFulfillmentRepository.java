package com.fulfilment.application.monolith.fulfillment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryFulfillmentRepository {

  private final List<FulfillmentAssociation> data = new ArrayList<>();
  private final AtomicLong idSeq = new AtomicLong(1);

  public void persist(FulfillmentAssociation a) {
    a.id = idSeq.getAndIncrement();
    data.add(a);
  }

  public List<FulfillmentAssociation> findByProductAndStore(Long productId, Long storeId) {
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

  public boolean alreadyAssociated(String buCode, Long productId, Long storeId) {
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

  public List<FulfillmentAssociation> all() {
    return List.copyOf(data);
  }
}
