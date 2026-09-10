package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    Warehouse existing =
        warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (existing == null) {
      throw new NotFoundException(
          "Warehouse with business unit code '"
              + newWarehouse.businessUnitCode
              + "' not found.");
    }

    if (!Objects.equals(newWarehouse.stock, existing.stock)) {
      throw new BadRequestException(
          "Stock of the new warehouse must match the stock of the warehouse being replaced.");
    }

    if (newWarehouse.capacity < existing.stock) {
      throw new BadRequestException(
          "New warehouse capacity must be sufficient to accommodate the existing stock of "
              + existing.stock
              + ".");
    }

    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new BadRequestException("Location '" + newWarehouse.location + "' is not valid.");
    }

    List<Warehouse> activeAtNewLocation =
        warehouseStore.findActiveByLocation(newWarehouse.location);

    long otherWarehouseCount =
        activeAtNewLocation.stream()
            .filter(w -> !w.businessUnitCode.equals(existing.businessUnitCode))
            .count();
    if (otherWarehouseCount >= location.maxNumberOfWarehouses) {
      throw new BadRequestException(
          "Maximum number of warehouses at location '"
              + newWarehouse.location
              + "' has been reached.");
    }

    int otherCapacity =
        activeAtNewLocation.stream()
            .filter(w -> !w.businessUnitCode.equals(existing.businessUnitCode))
            .mapToInt(w -> w.capacity)
            .sum();
    if (otherCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new BadRequestException(
          "Total capacity at location '"
              + newWarehouse.location
              + "' would exceed the maximum allowed ("
              + location.maxCapacity
              + ").");
    }

    existing.archivedAt = LocalDateTime.now();
    warehouseStore.update(existing);

    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);
  }
}
