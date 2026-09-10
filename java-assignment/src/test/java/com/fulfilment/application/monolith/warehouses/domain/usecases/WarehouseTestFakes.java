package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class InMemoryWarehouseStore implements WarehouseStore {

  final List<Warehouse> warehouses = new ArrayList<>();

  @Override
  public List<Warehouse> getAll() {
    return new ArrayList<>(warehouses);
  }

  @Override
  public void create(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    warehouses.stream()
        .filter(w -> w.businessUnitCode.equals(warehouse.businessUnitCode) && w.archivedAt == null)
        .findFirst()
        .ifPresent(
            w -> {
              w.location = warehouse.location;
              w.capacity = warehouse.capacity;
              w.stock = warehouse.stock;
              w.archivedAt = warehouse.archivedAt;
            });
  }

  @Override
  public void remove(Warehouse warehouse) {
    warehouses.removeIf(w -> w.businessUnitCode.equals(warehouse.businessUnitCode));
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    return warehouses.stream()
        .filter(w -> w.businessUnitCode.equals(buCode) && w.archivedAt == null)
        .findFirst()
        .map(
            w -> {
              Warehouse copy = new Warehouse();
              copy.businessUnitCode = w.businessUnitCode;
              copy.location = w.location;
              copy.capacity = w.capacity;
              copy.stock = w.stock;
              copy.createdAt = w.createdAt;
              copy.archivedAt = w.archivedAt;
              return copy;
            })
        .orElse(null);
  }

  @Override
  public List<Warehouse> findActiveByLocation(String location) {
    return warehouses.stream()
        .filter(w -> w.location.equals(location) && w.archivedAt == null)
        .collect(Collectors.toList());
  }
}

class InMemoryLocationResolver implements LocationResolver {

  private final Map<String, Location> store = new HashMap<>();

  void add(Location location) {
    store.put(location.identification, location);
  }

  @Override
  public Location resolveByIdentifier(String identifier) {
    return store.get(identifier);
  }
}
