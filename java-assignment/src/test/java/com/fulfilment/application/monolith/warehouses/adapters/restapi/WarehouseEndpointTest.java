package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointTest {

  private static final String PATH = "/warehouse";

  @Test
  public void testListWarehousesShouldReturnInitialData() {
    given()
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
  }

  @Test
  public void testGetWarehouseByBuCodeShouldReturn200() {
    given()
        .when()
        .get(PATH + "/MWH.012")
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"), containsString("AMSTERDAM-001"));
  }

  @Test
  public void testGetNonExistentWarehouseShouldReturn404() {
    given().when().get(PATH + "/WH.DOES.NOT.EXIST").then().statusCode(404);
  }

  @Test
  public void testCreateWithDuplicateBuCodeShouldReturn400() {
    // MWH.001 already exists in import.sql
    String body =
        "{\"businessUnitCode\":\"MWH.001\",\"location\":\"HELMOND-001\",\"capacity\":30,\"stock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(400);
  }

  @Test
  public void testCreateWithInvalidLocationShouldReturn400() {
    String body =
        "{\"businessUnitCode\":\"WH.NEW.TEST\",\"location\":\"NOWHERE-999\",\"capacity\":30,\"stock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(400);
  }

  @Test
  public void testCreateWithCapacityLessThanStockShouldReturn400() {
    String body =
        "{\"businessUnitCode\":\"WH.CAP.TEST\",\"location\":\"HELMOND-001\",\"capacity\":5,\"stock\":20}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(400);
  }

  @Test
  public void testFullCrudFlow() {
    // VETSBY-001: maxWarehouses=1, maxCapacity=90 — no warehouse there in import.sql
    String createBody =
        "{\"businessUnitCode\":\"WH.CRUD.TEST\",\"location\":\"VETSBY-001\",\"capacity\":80,\"stock\":10}";

    // Create
    given()
        .contentType(ContentType.JSON)
        .body(createBody)
        .when()
        .post(PATH)
        .then()
        .statusCode(200)
        .body(containsString("WH.CRUD.TEST"), containsString("VETSBY-001"));

    // Get
    given()
        .when()
        .get(PATH + "/WH.CRUD.TEST")
        .then()
        .statusCode(200)
        .body(containsString("WH.CRUD.TEST"));

    // Replace — same location, same stock (10), larger capacity (85)
    String replaceBody =
        "{\"businessUnitCode\":\"WH.CRUD.TEST\",\"location\":\"VETSBY-001\",\"capacity\":85,\"stock\":10}";
    given()
        .contentType(ContentType.JSON)
        .body(replaceBody)
        .when()
        .post(PATH + "/WH.CRUD.TEST/replacement")
        .then()
        .statusCode(200)
        .body(containsString("WH.CRUD.TEST"))
        .body(containsString("85"));

    // Archive (DELETE)
    given().when().delete(PATH + "/WH.CRUD.TEST").then().statusCode(204);

    // After archive — should return 404
    given().when().get(PATH + "/WH.CRUD.TEST").then().statusCode(404);
  }

  @Test
  public void testArchiveNonExistentWarehouseShouldReturn404() {
    given().when().delete(PATH + "/WH.NONEXISTENT.XYZ").then().statusCode(404);
  }

  @Test
  public void testReplaceWithStockMismatchShouldReturn400() {
    // MWH.023 at TILBURG-001 has stock=27 in import.sql
    String body =
        "{\"businessUnitCode\":\"MWH.023\",\"location\":\"TILBURG-001\",\"capacity\":30,\"stock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH + "/MWH.023/replacement")
        .then()
        .statusCode(400);
  }
}
