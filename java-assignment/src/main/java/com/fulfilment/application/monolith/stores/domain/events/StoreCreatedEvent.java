package com.fulfilment.application.monolith.stores.domain.events;

import com.fulfilment.application.monolith.stores.domain.models.Store;

/** Fired inside a transaction after a new Store is persisted. */
public record StoreCreatedEvent(Store store) {}
