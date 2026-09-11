package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.events.WarehouseCreatedEvent;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject Event<WarehouseCreatedEvent> warehouseCreatedEvent;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new BadRequestException(
          "A warehouse with business unit code '" + warehouse.businessUnitCode + "' already exists.");
    }

    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new BadRequestException("Location '" + warehouse.location + "' is not valid.");
    }

    List<Warehouse> activeAtLocation = warehouseStore.findActiveByLocation(warehouse.location);
    if (activeAtLocation.size() >= location.maxNumberOfWarehouses) {
      throw new BadRequestException(
          "Maximum number of warehouses at location '" + warehouse.location + "' has been reached.");
    }

    int totalCapacity =
        activeAtLocation.stream().mapToInt(w -> w.capacity).sum() + warehouse.capacity;
    if (totalCapacity > location.maxCapacity) {
      throw new BadRequestException(
          "Total capacity at location '"
              + warehouse.location
              + "' would exceed the maximum allowed ("
              + location.maxCapacity
              + ").");
    }

    if (warehouse.stock != null && warehouse.capacity < warehouse.stock) {
      throw new BadRequestException("Warehouse capacity cannot be less than the current stock.");
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(warehouse);
    if (warehouseCreatedEvent != null) {
      warehouseCreatedEvent.fire(new WarehouseCreatedEvent(warehouse));
    }
  }
}
