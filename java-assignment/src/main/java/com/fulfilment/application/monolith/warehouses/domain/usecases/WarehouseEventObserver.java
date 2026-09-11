package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.gateway.LegacyWarehouseGateway;
import com.fulfilment.application.monolith.warehouses.domain.events.WarehouseArchivedEvent;
import com.fulfilment.application.monolith.warehouses.domain.events.WarehouseCreatedEvent;
import com.fulfilment.application.monolith.warehouses.domain.events.WarehouseReplacedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class WarehouseEventObserver {

  @Inject LegacyWarehouseGateway legacyWarehouseGateway;

  public void onCreated(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) WarehouseCreatedEvent event) {
    legacyWarehouseGateway.notifyCreated(event.warehouse());
  }

  public void onArchived(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) WarehouseArchivedEvent event) {
    legacyWarehouseGateway.notifyArchived(event.warehouse());
  }

  public void onReplaced(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) WarehouseReplacedEvent event) {
    legacyWarehouseGateway.notifyReplaced(event.archived(), event.created());
  }
}
