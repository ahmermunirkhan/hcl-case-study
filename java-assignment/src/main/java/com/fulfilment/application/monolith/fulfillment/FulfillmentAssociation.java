package com.fulfilment.application.monolith.fulfillment;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fulfillment_association")
public class FulfillmentAssociation extends PanacheEntity {

  @Column(nullable = false)
  public String warehouseBusinessUnitCode;

  @Column(nullable = false)
  public Long productId;

  @Column(nullable = false)
  public Long storeId;

  public FulfillmentAssociation() {}

  public FulfillmentAssociation(String warehouseBusinessUnitCode, Long productId, Long storeId) {
    this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    this.productId = productId;
    this.storeId = storeId;
  }
}
