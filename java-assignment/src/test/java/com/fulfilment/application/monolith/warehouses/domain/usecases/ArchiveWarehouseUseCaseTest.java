package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  private InMemoryWarehouseStore store;
  private ArchiveWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
    store = new InMemoryWarehouseStore();
    useCase = new ArchiveWarehouseUseCase(store);
  }

  @Test
  void testArchiveActiveWarehouseSetsArchivedAt() {
    store.create(activeWarehouse("WH.001"));

    useCase.archive(stub("WH.001"));

    Warehouse archived = store.warehouses.get(0);
    assertNotNull(archived.archivedAt, "archivedAt should be set after archiving");
  }

  @Test
  void testArchivedWarehouseIsNoLongerFoundAsActive() {
    store.create(activeWarehouse("WH.001"));

    useCase.archive(stub("WH.001"));

    assertNull(store.findByBusinessUnitCode("WH.001"),
        "Archived warehouse should not be returned by findByBusinessUnitCode");
  }

  @Test
  void testArchiveNonExistentWarehouseThrowsNotFound() {
    assertThrows(NotFoundException.class, () -> useCase.archive(stub("WH.GHOST")));
  }

  @Test
  void testArchiveAlreadyArchivedWarehouseThrowsNotFound() {
    Warehouse w = activeWarehouse("WH.001");
    store.create(w);
    useCase.archive(stub("WH.001")); // archive once

    // archivedAt is now set — findByBusinessUnitCode filters it out
    assertThrows(NotFoundException.class, () -> useCase.archive(stub("WH.001")));
  }

  // ── helpers ──────────────────────────────────────────────

  private Warehouse activeWarehouse(String buCode) {
    Warehouse w = new Warehouse();
    w.businessUnitCode = buCode;
    w.location = "LOC-001";
    w.capacity = 50;
    w.stock = 10;
    return w;
  }

  private Warehouse stub(String buCode) {
    Warehouse w = new Warehouse();
    w.businessUnitCode = buCode;
    return w;
  }
}
