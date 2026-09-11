package com.fulfilment.application.monolith.products.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  private static final String PATH = "product";

  // ── List ────────────────────────────────────────────────────

  @Test
  public void testListProductsReturnsInitialData() {
    given()
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body(containsString("KALLAX"), containsString("BESTÅ"));
  }

  // ── Get single ──────────────────────────────────────────────

  @Test
  public void testGetSingleProductReturnsOk() {
    // Product 3 (BESTÅ) from import.sql — never deleted by any test
    given()
        .when()
        .get(PATH + "/3")
        .then()
        .statusCode(200)
        .body(containsString("BESTÅ"));
  }

  @Test
  public void testGetNonExistentProductReturns404() {
    given().when().get(PATH + "/99999").then().statusCode(404);
  }

  // ── Create ──────────────────────────────────────────────────

  @Test
  public void testCreateProductHappyPath() {
    String body = "{\"name\":\"TEST_CREATE_PRODUCT\",\"stock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(201)
        .body(containsString("TEST_CREATE_PRODUCT"));
  }

  @Test
  public void testCreateProductWithPresetIdReturns422() {
    String body = "{\"id\":999,\"name\":\"BAD_PRODUCT\",\"stock\":0}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post(PATH)
        .then()
        .statusCode(422);
  }

  // ── Update ──────────────────────────────────────────────────

  @Test
  public void testUpdateProductHappyPath() {
    // Create a dedicated product, then update it
    String createBody = "{\"name\":\"PRODUCT_FOR_UPDATE\",\"stock\":3}";
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

    String updateBody = "{\"name\":\"PRODUCT_UPDATED\",\"stock\":7,\"description\":\"updated\"}";
    given()
        .contentType(ContentType.JSON)
        .body(updateBody)
        .when()
        .put(PATH + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("PRODUCT_UPDATED"));
  }

  @Test
  public void testUpdateProductWithNullNameReturns422() {
    // Null-name check fires before findById, so id=99999 is fine here
    String body = "{\"stock\":5}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .put(PATH + "/99999")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateNonExistentProductReturns404() {
    String body = "{\"name\":\"GHOST_PRODUCT\",\"stock\":0}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .put(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  // ── Delete ──────────────────────────────────────────────────

  @Test
  public void testDeleteProductHappyPath() {
    // Create a dedicated product and delete it
    String createBody = "{\"name\":\"PRODUCT_TO_DELETE\",\"stock\":1}";
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

    given().when().delete(PATH + "/" + id).then().statusCode(204);

    // Confirm gone
    given().when().get(PATH + "/" + id).then().statusCode(404);
  }

  @Test
  public void testDeleteNonExistentProductReturns404() {
    given().when().delete(PATH + "/99999").then().statusCode(404);
  }

  // ── Legacy combined test (kept for backwards compat) ────────

  @Test
  public void testCrudProduct() {
    // List all — products 2 and 3 always present
    given()
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body(containsString("KALLAX"), containsString("BESTÅ"));

    // Create and delete within the same test so state is localised
    String createBody = "{\"name\":\"TONSTAD_LEGACY\",\"stock\":10}";
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

    given().when().delete(PATH + "/" + id).then().statusCode(204);

    given()
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body(not(containsString("TONSTAD_LEGACY")));
  }
}
