package com.fulfilment.application.monolith.stores.domain.usecases;

import com.fulfilment.application.monolith.stores.adapters.gateway.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.domain.events.StoreCreatedEvent;
import com.fulfilment.application.monolith.stores.domain.events.StoreDeletedEvent;
import com.fulfilment.application.monolith.stores.domain.events.StoreUpdatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Observes store lifecycle events and notifies the legacy system AFTER the originating transaction
 * has been successfully committed ({@link TransactionPhase#AFTER_SUCCESS}).
 *
 * <p>Using {@code AFTER_SUCCESS} guarantees that the downstream legacy system only ever receives
 * data that is durably persisted in our database. If the transaction rolls back the observer never
 * fires, so the legacy system stays consistent with us.
 */
@ApplicationScoped
public class StoreEventObserver {

  private static final Logger LOGGER = Logger.getLogger(StoreEventObserver.class);

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  public void onStoreCreated(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreCreatedEvent event) {
    LOGGER.infof("Transaction committed — notifying legacy system of new store: %s", event.store().name);
    legacyStoreManagerGateway.createStoreOnLegacySystem(event.store());
  }

  public void onStoreUpdated(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreUpdatedEvent event) {
    LOGGER.infof("Transaction committed — notifying legacy system of updated store: %s", event.store().name);
    legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store());
  }

  public void onStoreDeleted(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreDeletedEvent event) {
    LOGGER.infof("Transaction committed — notifying legacy system of deleted store: %s", event.store().name);
    legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store());
  }
}
