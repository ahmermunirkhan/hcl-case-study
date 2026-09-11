package com.fulfilment.application.monolith.stores.domain.events;

import com.fulfilment.application.monolith.stores.domain.models.Store;

/** Fired inside a transaction after a Store is deleted. */
public record StoreDeletedEvent(Store store) {}
