package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private InMemoryWarehouseStore store;
  private InMemoryLocationResolver locationResolver;
  private CreateWarehouseUseCase useCase;

  // Location: max 2 warehouses, max total capacity 100
  private static final Location TEST_LOCATION =
      new Location("LOC-001", 2, 100);

  @BeforeEach
  void setUp() {
    store = new InMemoryWarehouseStore();
    locationResolver = new InMemoryLocationResolver();
    locationResolver.add(TEST_LOCATION);
    useCase = new CreateWarehouseUseCase(store, locationResolver);
  }

  @Test
  void testHappyPathCreatesWarehouseWithTimestamp() {
    Warehouse w = warehouse("WH.001", "LOC-001", 60, 20);

    useCase.create(w);

    assertEquals(1, store.warehouses.size());
    Warehouse saved = store.warehouses.get(0);
    assertEquals("WH.001", saved.businessUnitCode);
    assertEquals("LOC-001", saved.location);
    assertEquals(60, saved.capacity);
    assertEquals(20, saved.stock);
    assertNotNull(saved.createdAt);
  }

  @Test
  void testDuplicateBusinessUnitCodeThrowsBadRequest() {
    store.create(warehouse("WH.001", "LOC-001", 30, 5));

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.create(warehouse("WH.001", "LOC-001", 40, 5)));

    assertContains("already exists", ex.getMessage());
  }

  @Test
  void testInvalidLocationThrowsBadRequest() {
    Warehouse w = warehouse("WH.NEW", "LOC-UNKNOWN", 30, 5);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.create(w));

    assertContains("not valid", ex.getMessage());
  }

  @Test
  void testMaxWarehousesAtLocationThrowsBadRequest() {
    // Location allows max 2 — fill both slots
    store.create(warehouse("WH.A", "LOC-001", 30, 0));
    store.create(warehouse("WH.B", "LOC-001", 30, 0));

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.create(warehouse("WH.C", "LOC-001", 10, 0)));

    assertContains("Maximum number", ex.getMessage());
  }

  @Test
  void testCapacityExceedsLocationMaxThrowsBadRequest() {
    // Location max capacity = 100; existing = 80; new = 30 → total 110 > 100
    store.create(warehouse("WH.A", "LOC-001", 80, 0));

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.create(warehouse("WH.B", "LOC-001", 30, 0)));

    assertContains("exceed", ex.getMessage());
  }

  @Test
  void testCapacityLessThanStockThrowsBadRequest() {
    Warehouse w = warehouse("WH.NEW", "LOC-001", 10, 50);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> useCase.create(w));

    assertContains("capacity", ex.getMessage());
  }

  @Test
  void testCapacityExactlyEqualToStockIsValid() {
    Warehouse w = warehouse("WH.NEW", "LOC-001", 30, 30);

    useCase.create(w);

    assertEquals(1, store.warehouses.size());
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
    assertNotNull(actual, "Exception message should not be null");
    org.junit.jupiter.api.Assertions.assertTrue(
        actual.toLowerCase().contains(expected.toLowerCase()),
        "Expected '" + expected + "' in: " + actual);
  }
}
