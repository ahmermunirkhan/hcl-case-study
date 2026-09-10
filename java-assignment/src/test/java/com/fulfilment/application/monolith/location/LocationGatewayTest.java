package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  private LocationGateway gateway;

  @BeforeEach
  void setUp() {
    gateway = new LocationGateway();
  }

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    var location = gateway.resolveByIdentifier("ZWOLLE-001");

    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  public void testAllKnownLocationsCanBeResolved() {
    String[] knownLocations = {
      "ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001", "AMSTERDAM-002",
      "TILBURG-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"
    };
    for (String id : knownLocations) {
      assertNotNull(gateway.resolveByIdentifier(id), "Expected to resolve: " + id);
    }
  }

  @Test
  public void testWhenResolveUnknownLocationShouldReturnNull() {
    var location = gateway.resolveByIdentifier("UNKNOWN-999");

    assertNull(location);
  }

  @Test
  public void testLocationAttributesAreCorrect() {
    var amsterdam = gateway.resolveByIdentifier("AMSTERDAM-001");

    assertNotNull(amsterdam);
    assertEquals(5, amsterdam.maxNumberOfWarehouses);
    assertEquals(100, amsterdam.maxCapacity);
  }
}
