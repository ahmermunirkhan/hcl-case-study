package com.fulfilment.application.monolith.stores.domain.usecases;

import com.fulfilment.application.monolith.stores.domain.events.StoreCreatedEvent;
import com.fulfilment.application.monolith.stores.domain.events.StoreDeletedEvent;
import com.fulfilment.application.monolith.stores.domain.events.StoreUpdatedEvent;
import com.fulfilment.application.monolith.stores.domain.models.Store;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class StoreService {

  @Inject Event<StoreCreatedEvent> storeCreatedEvent;
  @Inject Event<StoreUpdatedEvent> storeUpdatedEvent;
  @Inject Event<StoreDeletedEvent> storeDeletedEvent;

  @Transactional
  public Store persistNew(Store store) {
    store.persist();
    storeCreatedEvent.fire(new StoreCreatedEvent(store));
    return store;
  }

  @Transactional
  public Store updateEntity(Long id, Store updatedStore) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    storeUpdatedEvent.fire(new StoreUpdatedEvent(entity));
    return entity;
  }

  @Transactional
  public Store patchEntity(Long id, Store updatedStore) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    if (entity.name != null) {
      entity.name = updatedStore.name;
    }
    if (entity.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }
    storeUpdatedEvent.fire(new StoreUpdatedEvent(entity));
    return entity;
  }

  @Transactional
  public void deleteEntity(Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    entity.delete();
    storeDeletedEvent.fire(new StoreDeletedEvent(entity));
  }
}
