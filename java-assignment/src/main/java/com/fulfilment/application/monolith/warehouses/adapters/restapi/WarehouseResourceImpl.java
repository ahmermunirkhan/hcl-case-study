package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject WarehouseStore warehouseStore;
  @Inject CreateWarehouseOperation createWarehouseOperation;
  @Inject ArchiveWarehouseOperation archiveWarehouseOperation;
  @Inject ReplaceWarehouseOperation replaceWarehouseOperation;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    return warehouseStore.getAll().stream().map(this::toApiWarehouse).toList();
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(
      @NotNull com.warehouse.api.beans.Warehouse data) {
    Warehouse domain = toDomainWarehouse(data);
    createWarehouseOperation.create(domain);
    return toApiWarehouse(warehouseStore.findByBusinessUnitCode(domain.businessUnitCode));
  }

  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
    Warehouse warehouse = warehouseStore.findByBusinessUnitCode(id);
    if (warehouse == null) {
      throw new NotFoundException("Warehouse with business unit code '" + id + "' not found.");
    }
    return toApiWarehouse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    Warehouse stub = new Warehouse();
    stub.businessUnitCode = id;
    archiveWarehouseOperation.archive(stub);
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull com.warehouse.api.beans.Warehouse data) {
    Warehouse newWarehouse = toDomainWarehouse(data);
    newWarehouse.businessUnitCode = businessUnitCode;
    replaceWarehouseOperation.replace(newWarehouse);
    return toApiWarehouse(warehouseStore.findByBusinessUnitCode(businessUnitCode));
  }

  private Warehouse toDomainWarehouse(com.warehouse.api.beans.Warehouse api) {
    Warehouse w = new Warehouse();
    w.businessUnitCode = api.getBusinessUnitCode();
    w.location = api.getLocation();
    w.capacity = api.getCapacity();
    w.stock = api.getStock();
    return w;
  }

  private com.warehouse.api.beans.Warehouse toApiWarehouse(Warehouse domain) {
    com.warehouse.api.beans.Warehouse response = new com.warehouse.api.beans.Warehouse();
    response.setBusinessUnitCode(domain.businessUnitCode);
    response.setLocation(domain.location);
    response.setCapacity(domain.capacity);
    response.setStock(domain.stock);
    return response;
  }
}
