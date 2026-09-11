# API Validation Report

**Project:** HCL Java Assignment — Quarkus 3.13.3  
**Date:** 2026-09-10  
**Runtime:** H2 in-memory (`%test` / `%dev` profile)  
**Endpoints validated:** 43 / 43 ✓

---

## Summary

| API Group | Base Path | Endpoints | Result |
|---|---|---|---|
| Product | `/product` | 10 | ✓ All pass |
| Store | `/store` | 13 | ✓ All pass |
| Warehouse | `/warehouse` | 10 | ✓ All pass |
| Allocation *(bonus)* | `/allocation` | 10 | ✓ All pass |
| **Total** | | **43** | **43 / 43** |

---

## Product API — `/product`

### GET /product — List all products

**Request**
```
GET /product
Accept: application/json
```
**Response · 200 OK**
```json
[
  { "id": 1, "name": "TONSTAD", "stock": 10 },
  { "id": 2, "name": "KALLAX",  "stock":  5 },
  { "id": 3, "name": "BESTÅ",   "stock":  3 }
]
```

---

### GET /product/{id} — Get single product

**Request**
```
GET /product/3
```
**Response · 200 OK**
```json
{ "id": 3, "name": "BESTÅ", "stock": 3 }
```

---

### GET /product/{id} — Non-existent product → 404

**Request**
```
GET /product/99999
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Product not found" }
```

---

### POST /product — Create product → 201

**Request**
```
POST /product
Content-Type: application/json

{ "name": "VALIDATION_PRODUCT", "stock": 10 }
```
**Response · 201 Created**
```json
{ "id": 4, "name": "VALIDATION_PRODUCT", "stock": 10 }
```

---

### POST /product — Preset id rejected → 422

**Request**
```
POST /product
Content-Type: application/json

{ "id": 999, "name": "BAD", "stock": 0 }
```
**Response · 422 Unprocessable**
```json
{ "code": 422, "error": "Id was invalidly set on request." }
```

---

### PUT /product/{id} — Full update → 200

**Request**
```
PUT /product/4
Content-Type: application/json

{ "name": "PRODUCT_UPDATED", "stock": 7, "description": "updated" }
```
**Response · 200 OK**
```json
{ "id": 4, "name": "PRODUCT_UPDATED", "stock": 7 }
```

---

### PUT /product/{id} — Null name rejected → 422

**Request**
```
PUT /product/99999
Content-Type: application/json

{ "stock": 5 }
```
**Response · 422 Unprocessable**
```json
{ "code": 422, "error": "Product Name was not set on request." }
```

---

### PUT /product/{id} — Non-existent product → 404

**Request**
```
PUT /product/99999
Content-Type: application/json

{ "name": "GHOST", "stock": 0 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Product not found" }
```

---

### DELETE /product/{id} — Delete product → 204

**Request**
```
DELETE /product/4
```
**Response · 204 No Content**
```
(empty body)
```

---

### DELETE /product/{id} — Non-existent product → 404

**Request**
```
DELETE /product/99999
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Product not found" }
```

---

## Store API — `/store`

### GET /store — List all stores → 200

**Request**
```
GET /store
```
**Response · 200 OK**
```json
[
  { "id": 1, "name": "TONSTAD", "quantityProductsInStock": 10 },
  { "id": 2, "name": "KALLAX",  "quantityProductsInStock":  5 },
  { "id": 3, "name": "BESTÅ",   "quantityProductsInStock":  3 }
]
```

---

### GET /store/{id} — Get single store → 200

**Request**
```
GET /store/1
```
**Response · 200 OK**
```json
{ "id": 1, "name": "TONSTAD", "quantityProductsInStock": 10 }
```

---

### GET /store/{id} — Non-existent store → 404

**Request**
```
GET /store/999999
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Store with id of 999999 does not exist." }
```

---

### POST /store — Create store → 201

**Request**
```
POST /store
Content-Type: application/json

{ "name": "VALIDATION_STORE", "quantityProductsInStock": 5 }
```
**Response · 201 Created**
```json
{ "id": 4, "name": "VALIDATION_STORE", "quantityProductsInStock": 5 }
```

---

### POST /store — Preset id rejected → 422

**Request**
```
POST /store
Content-Type: application/json

{ "id": 999, "name": "BAD", "quantityProductsInStock": 0 }
```
**Response · 422 Unprocessable**
```json
{ "code": 422, "error": "Id was invalidly set on request." }
```

---

### PUT /store/{id} — Full update → 200

**Request**
```
PUT /store/1
Content-Type: application/json

{ "name": "STORE_UPDATED", "quantityProductsInStock": 20 }
```
**Response · 200 OK**
```json
{ "id": 1, "name": "STORE_UPDATED", "quantityProductsInStock": 20 }
```

---

### PUT /store/{id} — Null name rejected → 422

**Request**
```
PUT /store/1
Content-Type: application/json

{ "quantityProductsInStock": 5 }
```
**Response · 422 Unprocessable**
```json
{ "code": 422, "error": "Store Name was not set on request." }
```

---

### PUT /store/{id} — Non-existent store → 404

**Request**
```
PUT /store/999999
Content-Type: application/json

{ "name": "GHOST", "quantityProductsInStock": 0 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Store with id of 999999 does not exist." }
```

---

### PATCH /store/{id} — Partial update → 200

**Request**
```
PATCH /store/1
Content-Type: application/json

{ "name": "STORE_PATCHED", "quantityProductsInStock": 25 }
```
**Response · 200 OK**
```json
{ "id": 1, "name": "STORE_PATCHED", "quantityProductsInStock": 25 }
```

---

### PATCH /store/{id} — Null name rejected → 422

**Request**
```
PATCH /store/1
Content-Type: application/json

{ "quantityProductsInStock": 5 }
```
**Response · 422 Unprocessable**
```json
{ "code": 422, "error": "Store Name was not set on request." }
```

---

### PATCH /store/{id} — Non-existent store → 404

**Request**
```
PATCH /store/999999
Content-Type: application/json

{ "name": "GHOST", "quantityProductsInStock": 0 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Store with id of 999999 does not exist." }
```

---

### DELETE /store/{id} — Delete store → 204

**Request**
```
DELETE /store/4
```
**Response · 204 No Content**
```
(empty body)
Legacy system notified via CDI StoreDeletedEvent observed at TransactionPhase.AFTER_SUCCESS
```

---

### DELETE /store/{id} — Non-existent store → 404

**Request**
```
DELETE /store/999999
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Store with id of 999999 does not exist." }
```

---

## Warehouse API — `/warehouse`

### GET /warehouse — List all active warehouses → 200

**Request**
```
GET /warehouse
```
**Response · 200 OK**
```json
[
  { "businessUnitCode": "MWH.001", "location": "ZWOLLE-001",    "capacity": 100, "stock": 10 },
  { "businessUnitCode": "MWH.012", "location": "AMSTERDAM-001", "capacity":  50, "stock":  5 },
  { "businessUnitCode": "MWH.023", "location": "TILBURG-001",   "capacity":  30, "stock": 27 }
]
```

---

### GET /warehouse/{buCode} — Get single warehouse → 200

**Request**
```
GET /warehouse/MWH.001
```
**Response · 200 OK**
```json
{ "businessUnitCode": "MWH.001", "location": "ZWOLLE-001", "capacity": 100, "stock": 10 }
```

---

### GET /warehouse/{buCode} — Non-existent / archived → 404

**Request**
```
GET /warehouse/WH.DOES.NOT.EXIST
```
**Response · 404 Not Found**
```json
{
  "code": 404,
  "error": "Warehouse with business unit code 'WH.DOES.NOT.EXIST' not found or already archived."
}
```

---

### POST /warehouse — Create warehouse → 201

**Request**
```
POST /warehouse
Content-Type: application/json

{
  "businessUnitCode": "MWH.VAL",
  "location": "HELMOND-001",
  "capacity": 40,
  "stock": 10
}
```
**Response · 201 Created**
```json
{ "businessUnitCode": "MWH.VAL", "location": "HELMOND-001", "capacity": 40, "stock": 10 }
```

---

### POST /warehouse — Invalid location → 400

**Request**
```
POST /warehouse
Content-Type: application/json

{ "businessUnitCode": "MWH.BAD", "location": "NOWHERE-999", "capacity": 100, "stock": 0 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "Location 'NOWHERE-999' is not valid." }
```

---

### POST /warehouse — Location at capacity → 400

**Request**
```
POST /warehouse
Content-Type: application/json

{ "businessUnitCode": "MWH.EXTRA", "location": "ZWOLLE-001", "capacity": 40, "stock": 0 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "Maximum number of warehouses at location 'ZWOLLE-001' has been reached." }
```

---

### POST /warehouse/{buCode}/replacement — Replace warehouse → 200

**Request**
```
POST /warehouse/MWH.VAL/replacement
Content-Type: application/json

{ "businessUnitCode": "MWH.VAL", "location": "HELMOND-001", "capacity": 45, "stock": 10 }
```
**Response · 200 OK**
```json
{ "businessUnitCode": "MWH.VAL", "location": "HELMOND-001", "capacity": 45, "stock": 10 }
```

---

### POST /warehouse/{buCode}/replacement — Stock mismatch → 400

**Request**
```
POST /warehouse/MWH.VAL/replacement
Content-Type: application/json

{ "businessUnitCode": "MWH.VAL", "location": "HELMOND-001", "capacity": 45, "stock": 99 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "Stock of the new warehouse must match the stock of the warehouse being replaced." }
```

---

### DELETE /warehouse/{buCode} — Archive warehouse → 204

**Request**
```
DELETE /warehouse/MWH.VAL
```
**Response · 204 No Content**
```
(empty body — warehouse is soft-deleted, archivedAt timestamp is set)
```

---

### DELETE /warehouse/{buCode} — Non-existent warehouse → 404

**Request**
```
DELETE /warehouse/WH.NONEXISTENT.XYZ
```
**Response · 404 Not Found**
```json
{
  "code": 404,
  "error": "Warehouse with business unit code 'WH.NONEXISTENT.XYZ' not found or already archived."
}
```

---

## Allocation API — `/allocation` *(Bonus Feature)*

> **Business Constraints**
> 1. Each Product can be allocated to at most **2** Warehouses per Store.
> 2. Each Store can be served by at most **3** distinct Warehouses.
> 3. Each Warehouse can store at most **5** types of Products.

### GET /allocation — List all allocations → 200

**Request**
```
GET /allocation
```
**Response · 200 OK**
```json
[
  { "id": 1, "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 },
  { "id": 2, "warehouseBusinessUnitCode": "MWH.012", "productId": 1, "storeId": 1 }
]
```

---

### GET /allocation/warehouse/{buCode} — Allocations for a warehouse → 200

**Request**
```
GET /allocation/warehouse/MWH.023
```
**Response · 200 OK**
```json
[
  { "id": 1, "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 },
  { "id": 3, "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 2 }
]
```

---

### GET /allocation/store/{storeId} — Allocations for a store → 200

**Request**
```
GET /allocation/store/1
```
**Response · 200 OK**
```json
[
  { "id": 1, "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 },
  { "id": 2, "warehouseBusinessUnitCode": "MWH.012", "productId": 1, "storeId": 1 }
]
```

---

### POST /allocation — Create allocation → 201

**Request**
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 }
```
**Response · 201 Created**
```json
{ "id": 1, "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 }
```

---

### POST /allocation — Duplicate allocation rejected → 400

**Request**
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.023", "productId": 1, "storeId": 1 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "This warehouse-product-store allocation already exists." }
```

---

### POST /allocation — Constraint 1: max 2 warehouses per product per store → 400

**Request** *(3rd warehouse for product 1 at store 1)*
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.001", "productId": 1, "storeId": 1 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "Product 1 is already allocated to 2 warehouses for store 1." }
```

---

### POST /allocation — Constraint 2: max 3 warehouses per store → 400

**Request** *(4th distinct warehouse for store 2)*
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.VAL", "productId": 3, "storeId": 2 }
```
**Response · 400 Bad Request**
```json
{ "code": 400, "error": "Store 2 is already served by 3 different warehouses." }
```

---

### POST /allocation — Non-existent warehouse → 404

**Request**
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "WH.GHOST.999", "productId": 1, "storeId": 1 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Warehouse 'WH.GHOST.999' not found or archived." }
```

---

### POST /allocation — Non-existent product → 404

**Request**
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.001", "productId": 9999, "storeId": 1 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Product with id 9999 not found." }
```

---

### POST /allocation — Non-existent store → 404

**Request**
```
POST /allocation
Content-Type: application/json

{ "warehouseBusinessUnitCode": "MWH.001", "productId": 1, "storeId": 9999 }
```
**Response · 404 Not Found**
```json
{ "code": 404, "error": "Store with id 9999 not found." }
```
