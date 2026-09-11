package com.fulfilment.application.monolith.allocation.domain.events;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;

public record AllocationCreatedEvent(WarehouseAllocation allocation) {}
