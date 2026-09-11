package com.fulfilment.application.monolith.stores.domain.events;

import com.fulfilment.application.monolith.stores.domain.models.Store;

/** Fired inside a transaction after a Store is updated (PUT or PATCH). */
public record StoreUpdatedEvent(Store store) {}
