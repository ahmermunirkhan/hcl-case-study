package com.fulfilment.application.monolith.allocation.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_allocation")
public class WarehouseAllocation extends PanacheEntity {

  @Column(nullable = false)
  public String warehouseBusinessUnitCode;

  @Column(nullable = false)
  public Long productId;

  @Column(nullable = false)
  public Long storeId;

  public WarehouseAllocation() {}

  public WarehouseAllocation(String warehouseBusinessUnitCode, Long productId, Long storeId) {
    this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    this.productId = productId;
    this.storeId = storeId;
  }
}
