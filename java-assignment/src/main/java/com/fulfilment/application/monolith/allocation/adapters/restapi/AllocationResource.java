package com.fulfilment.application.monolith.allocation.adapters.restapi;

import com.fulfilment.application.monolith.allocation.domain.models.WarehouseAllocation;
import com.fulfilment.application.monolith.allocation.domain.ports.AllocationStore;
import com.fulfilment.application.monolith.allocation.domain.usecases.AllocateWarehouseUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/allocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AllocationResource {

  @Inject AllocateWarehouseUseCase allocateWarehouseUseCase;
  @Inject AllocationStore allocationStore;

  @GET
  public List<WarehouseAllocation> list() {
    return allocationStore.getAll();
  }

  @GET
  @Path("/warehouse/{buCode}")
  public List<WarehouseAllocation> listByWarehouse(@PathParam("buCode") String buCode) {
    return allocationStore.findByWarehouse(buCode);
  }

  @GET
  @Path("/store/{storeId}")
  public List<WarehouseAllocation> listByStore(@PathParam("storeId") Long storeId) {
    return allocationStore.findByStore(storeId);
  }

  @POST
  public Response allocate(AllocationRequest request) {
    if (request == null
        || request.warehouseBusinessUnitCode == null
        || request.productId == null
        || request.storeId == null) {
      throw new BadRequestException(
          "warehouseBusinessUnitCode, productId and storeId are required.");
    }
    WarehouseAllocation allocation =
        allocateWarehouseUseCase.allocate(
            request.warehouseBusinessUnitCode, request.productId, request.storeId);
    return Response.status(201).entity(allocation).build();
  }
}
