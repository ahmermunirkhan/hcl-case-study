package com.fulfilment.application.monolith.warehouses.adapters.gateway;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyWarehouseGateway {

  public void notifyCreated(Warehouse warehouse) {
    writeLegacyNotification("CREATED", warehouse.businessUnitCode);
  }

  public void notifyArchived(Warehouse warehouse) {
    writeLegacyNotification("ARCHIVED", warehouse.businessUnitCode);
  }

  public void notifyReplaced(Warehouse archived, Warehouse created) {
    writeLegacyNotification(
        "REPLACED", archived.businessUnitCode + " -> " + created.businessUnitCode);
  }

  private void writeLegacyNotification(String action, String detail) {
    try {
      Path tmp = Files.createTempFile("warehouse-" + action.toLowerCase() + "-", ".tmp");
      Files.writeString(tmp, action + ": " + detail);
    } catch (IOException e) {
      throw new RuntimeException("Legacy warehouse notification failed", e);
    }
  }
}
