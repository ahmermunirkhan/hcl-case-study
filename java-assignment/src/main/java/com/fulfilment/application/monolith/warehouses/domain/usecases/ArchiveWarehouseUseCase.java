package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existing == null) {
      throw new NotFoundException(
          "Warehouse with business unit code '"
              + warehouse.businessUnitCode
              + "' not found or already archived.");
    }
    existing.archivedAt = LocalDateTime.now();
    warehouseStore.update(existing);
  }
}
