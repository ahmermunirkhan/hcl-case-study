package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  private InMemoryWarehouseStore store;
  private InMemoryLocationResolver locationResolver;
  private ReplaceWarehouseUseCase useCase;

  private static final Location LOCATION = new Location("LOC-001", 2, 200);

  @BeforeEach
  void setUp() {
    store = new InMemoryWarehouseStore();
    locationResolver = new InMemoryLocationResolver();
    locationResolver.add(LOCATION);
    useCase = new ReplaceWarehouseUseCase(store, locationResolver);
  }

  @Test
  void testHappyPathArchivesOldAndCreatesNew() {
    store.create(warehouse("WH.001", "LOC-001", 80, 15));

    Warehouse replacement = warehouse("WH.001", "LOC-001", 100, 15);
    useCase.replace(replacement);

    // Two records: one archived, one active
    assertEquals(2, store.warehouses.size());

    Warehouse archived = store.warehouses.stream()
        .filter(w -> w.archivedAt != null)
        .findFirst()
        .orElse(null);
    assertNotNull(archived, "Old warehouse should be archived");
    assertEquals(80, archived.capacity);

    Warehouse active = store.findByBusinessUnitCode("WH.001");
    assertNotNull(active, "New warehouse should be active");
    assertEquals(100, active.capacity);
    assertNotNull(active.createdAt);
  }

  @Test
  void testReplaceNonExistentWarehouseThrowsNotFound() {
    Warehouse replacement = warehouse("WH.GHOST", "LOC-001", 50, 10);

    assertThrows(NotFoundException.class, () -> useCase.replace(replacement));
  }

  @Test
  void testStockMismatchThrowsBadRequest() {
    store.create(warehouse("WH.001", "LOC-001", 80, 15));

    Warehouse replacement = warehouse("WH.001", "LOC-001", 80, 20); // stock 20 ≠ 15

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.replace(replacement));
    assertContains("stock", ex.getMessage());
  }

  @Test
  void testNewCapacityLessThanOldStockThrowsBadRequest() {
    store.create(warehouse("WH.001", "LOC-001", 80, 30));

    Warehouse replacement = warehouse("WH.001", "LOC-001", 20, 30); // capacity 20 < stock 30

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.replace(replacement));
    assertContains("capacity", ex.getMessage());
  }

  @Test
  void testInvalidLocationThrowsBadRequest() {
    store.create(warehouse("WH.001", "LOC-001", 80, 10));

    Warehouse replacement = warehouse("WH.001", "LOC-INVALID", 80, 10);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.replace(replacement));
    assertContains("not valid", ex.getMessage());
  }

  @Test
  void testNewLocationAtCapacityThrowsBadRequest() {
    // Add a second location that is already at capacity limit
    Location fullLocation = new Location("LOC-FULL", 1, 50);
    locationResolver.add(fullLocation);

    // Existing warehouse at LOC-FULL occupying all slots
    store.create(warehouse("WH.OTHER", "LOC-FULL", 50, 5));

    // Old warehouse at LOC-001
    store.create(warehouse("WH.001", "LOC-001", 80, 10));

    // Try to replace WH.001 and move it to already-full LOC-FULL
    Warehouse replacement = warehouse("WH.001", "LOC-FULL", 80, 10);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.replace(replacement));
    assertContains("Maximum number", ex.getMessage());
  }

  @Test
  void testReplacingWarehouseAtSameLocationCountsItOut() {
    // LOC-001 allows 2 warehouses; put one there + the one being replaced
    store.create(warehouse("WH.OTHER", "LOC-001", 50, 0));
    store.create(warehouse("WH.001", "LOC-001", 50, 10));

    // Replace WH.001 at same location — after replacing, slot count stays the same
    Warehouse replacement = warehouse("WH.001", "LOC-001", 60, 10);
    useCase.replace(replacement); // should succeed — no exception expected

    // After replace: old archived, new WH.001 active → total 2 active at LOC-001
    assertEquals(2, store.findActiveByLocation("LOC-001").size());
    assertNotNull(store.findByBusinessUnitCode("WH.001"), "New warehouse should be active");
    assertEquals(60, store.findByBusinessUnitCode("WH.001").capacity);
  }

  // ── helpers ──────────────────────────────────────────────

  private Warehouse warehouse(String buCode, String location, int capacity, int stock) {
    Warehouse w = new Warehouse();
    w.businessUnitCode = buCode;
    w.location = location;
    w.capacity = capacity;
    w.stock = stock;
    return w;
  }

  private void assertContains(String expected, String actual) {
    assertNotNull(actual);
    assertTrue(
        actual.toLowerCase().contains(expected.toLowerCase()),
        "Expected '" + expected + "' in: " + actual);
  }
}
