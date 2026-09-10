package com.fulfilment.application.monolith.fulfillment;

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

@Path("/fulfillment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FulfillmentResource {

  @Inject AssociateFulfillmentUseCase associateFulfillmentUseCase;
  @Inject FulfillmentRepository fulfillmentRepository;

  @GET
  public List<FulfillmentAssociation> list() {
    return fulfillmentRepository.listAll();
  }

  @GET
  @Path("/warehouse/{buCode}")
  public List<FulfillmentAssociation> listByWarehouse(@PathParam("buCode") String buCode) {
    return fulfillmentRepository.list("warehouseBusinessUnitCode = ?1", buCode);
  }

  @GET
  @Path("/store/{storeId}")
  public List<FulfillmentAssociation> listByStore(@PathParam("storeId") Long storeId) {
    return fulfillmentRepository.list("storeId = ?1", storeId);
  }

  @POST
  public Response associate(FulfillmentRequest request) {
    if (request == null
        || request.warehouseBusinessUnitCode == null
        || request.productId == null
        || request.storeId == null) {
      throw new BadRequestException("warehouseBusinessUnitCode, productId and storeId are required.");
    }
    FulfillmentAssociation association =
        associateFulfillmentUseCase.associate(
            request.warehouseBusinessUnitCode, request.productId, request.storeId);
    return Response.status(201).entity(association).build();
  }

  public static class FulfillmentRequest {
    public String warehouseBusinessUnitCode;
    public Long productId;
    public Long storeId;
  }
}
