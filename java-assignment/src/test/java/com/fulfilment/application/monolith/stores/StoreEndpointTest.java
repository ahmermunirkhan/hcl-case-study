package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  private static final String PATH = "/store";

  // ── List ────────────────────────────────────────────────────

  @Test
  public void testListStoresReturnsOk() {
    given().when().get(PATH).then().statusCode(200);
  }

  // ── Full CRUD flow ──────────────────────────────────────────

  @Test
  public void testCrudStore() {
    // Create
    String body = "{\"name\":\"STORE_TEST_UNIQUE\",\"quantityProductsInStock\":7}";
    Integer id =
        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post(PATH)
            .then()
            .statusCode(201)
            .body(containsString("STORE_TEST_UNIQUE"))
            .extract()
            .path("id");

    // Read
    given()
        .when()
        .get(PATH + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("STORE_TEST_UNIQUE"));

    // Update (PUT)
    String updateBody = "{\"name\":\"STORE_TEST_UPDATED\",\"quantityProductsInStock\":15}";
    given()
        .contentType(ContentType.JSON)
        .body(updateBody)
        .when()
        .put(PATH + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("STORE_TEST_UPDATED"));

    // Patch
    String patchBody = "{\"name\":\"STORE_TEST_PATCHED\",\"quantityProductsInStock\":20}";
    given()
        .contentType(ContentType.JSON)
        .body(patchBody)
        .when()
        .patch(PATH + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("STORE_TEST_PATCHED"));

    // Delete
    given().when().delete(PATH + "/" + id).then().statusCode(204);

    // Verify deleted
    given().when().get(PATH + "/" + id).then().statusCode(404);
  }

  // ── Create error cases ──────────────────────────────────────

  @Test
  public void testCreateStoreWithPresetIdShouldReturn422() {
    String body = "{\"id\":999,\"name\":\"BAD_STORE\",\"quantityProductsInStock\":0}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(422);
  }

  // ── Get error cases ─────────────────────────────────────────

  @Test
  public void testGetNonExistentStoreShouldReturn404() {
    given().when().get(PATH + "/999999").then().statusCode(404);
  }

  // ── Update (PUT) error cases ────────────────────────────────

  @Test
  public void testUpdateNonExistentStoreShouldReturn404() {
    String body = "{\"name\":\"GHOST\",\"quantityProductsInStock\":0}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .put(PATH + "/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testUpdateStoreWithoutNameShouldReturn422() {
    String body = "{\"quantityProductsInStock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .put(PATH + "/1")
        .then()
        .statusCode(422);
  }

  // ── Patch error cases ───────────────────────────────────────

  @Test
  public void testPatchStoreWithoutNameShouldReturn422() {
    // Covers StoreResource.patch null-name check (→ 422 before calling service)
    String body = "{\"quantityProductsInStock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .patch(PATH + "/1")
        .then()
        .statusCode(422);
  }

  @Test
  public void testPatchNonExistentStoreShouldReturn404() {
    // Covers StoreService.patchEntity null branch (store not found → 404)
    String body = "{\"name\":\"GHOST\",\"quantityProductsInStock\":0}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .patch(PATH + "/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testPatchStoreWithZeroQuantityCoversNullBranch() {
    // Creates a store with quantityProductsInStock=0 so that the false branch
    // of `if (entity.quantityProductsInStock != 0)` in StoreService.patchEntity is reached.
    String createBody = "{\"name\":\"ZERO_STOCK_STORE\",\"quantityProductsInStock\":0}";
    Integer id =
        given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .when()
            .post(PATH)
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    String patchBody = "{\"name\":\"ZERO_STOCK_PATCHED\",\"quantityProductsInStock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(patchBody)
        .when()
        .patch(PATH + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("ZERO_STOCK_PATCHED"));
  }

  // ── Delete error cases ──────────────────────────────────────

  @Test
  public void testDeleteNonExistentStoreShouldReturn404() {
    // Covers StoreService.deleteEntity null branch (store not found → 404)
    given().when().delete(PATH + "/999999").then().statusCode(404);
  }
}
