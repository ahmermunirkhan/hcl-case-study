package com.fulfilment.application.monolith.warehouses.domain.events;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public record WarehouseArchivedEvent(Warehouse warehouse) {}
