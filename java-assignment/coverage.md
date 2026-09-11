# Test Coverage Report

**Project:** HCL Java Assignment — Quarkus 3.13.3  
**Date:** 2026-09-10  
**Tests:** 62 passing, 0 failures  
**Overall instruction coverage:** 96.0% (1435 / 1495 instructions)

Coverage is measured by merging two exec files:

- `jacoco-quarkus.exec` — produced by `quarkus-jacoco`, instruments classes **after** Quarkus bytecode transformation (Panache, Arc CDI proxies), capturing real `@QuarkusTest` integration-test coverage.
- `jacoco.exec` — produced by the standalone `prepare-agent`, capturing plain unit-test coverage outside the Quarkus container.

Both are combined via `jacoco:merge` into `jacoco-merged.exec`, which drives the final HTML/CSV report.

---

## Test Suites

| Suite | Tests | Kind |
|---|---|---|
| `AllocationEndpointTest` | 10 | `@QuarkusTest` — REST integration |
| `ProductEndpointTest` | 11 | `@QuarkusTest` — REST integration |
| `StoreEndpointTest` | 10 | `@QuarkusTest` — REST integration |
| `WarehouseEndpointTest` | 9 | `@QuarkusTest` — REST integration |
| `ArchiveWarehouseUseCaseTest` | 4 | Unit — in-memory fakes |
| `CreateWarehouseUseCaseTest` | 7 | Unit — in-memory fakes |
| `ReplaceWarehouseUseCaseTest` | 7 | Unit — in-memory fakes |
| `LocationGatewayTest` | 4 | Unit — plain Java |
| **Total** | **62** | |

---

## Coverage by Module

### Allocation (Bonus feature)

| Class | Instr % | Instr (miss/total) | Branch % | Branch (miss/total) |
|---|---|---|---|---|
| `WarehouseAllocation` | **100%** | 0 / 15 | — | — |
| `AllocationRepository` | **100%** | 0 / 77 | **100%** | 0 / 2 |
| `AllocationRequest` | **100%** | 0 / 3 | — | — |
| `AllocationResource` | 91.8% | 5 / 61 | 50.0% | 4 / 8 |
| `AllocateWarehouseUseCase` | 91.4% | 14 / 162 | 81.8% | 18 / 22 |

### Product

| Class | Instr % | Instr (miss/total) | Branch % | Branch (miss/total) |
|---|---|---|---|---|
| `ProductResource` | **100%** | 0 / 119 | **100%** | 0 / 10 |
| `ProductResource.ErrorMapper` | **100%** | 0 / 48 | **100%** | 0 / 4 |
| `ProductRepository` | **100%** | 0 / 3 | — | — |
| `Product` ⚠ | 33.3% | 6 / 9 | — | — |

> `Product`: the `Product(String name)` constructor is dead code — JSON deserialization uses the no-arg constructor only. Not a testing gap.

### Store

| Class | Instr % | Instr (miss/total) | Branch % | Branch (miss/total) |
|---|---|---|---|---|
| `StoreResource` | **100%** | 0 / 101 | **100%** | 0 / 8 |
| `StoreResource.ErrorMapper` | **100%** | 0 / 48 | **100%** | 0 / 4 |
| `StoreService` | **100%** | 0 / 75 | 90.0% | 1 / 10 |
| `LegacyStoreManagerGateway` | 94.9% | 3 / 59 | — | — |
| `Store` ⚠ | 33.3% | 6 / 9 | — | — |

> `Store`: the `Store(String name)` constructor is dead code — same reason as `Product` above.

### Warehouse

| Class | Instr % | Instr (miss/total) | Branch % | Branch (miss/total) |
|---|---|---|---|---|
| `WarehouseResourceImpl` | **100%** | 0 / 119 | **100%** | 0 / 2 |
| `DbWarehouse` | **100%** | 0 / 33 | — | — |
| `ArchiveWarehouseUseCase` | **100%** | 0 / 29 | **100%** | 0 / 2 |
| `CreateWarehouseUseCase` | **100%** | 0 / 105 | 91.7% | 1 / 12 |
| `Warehouse` (domain model) | **100%** | 0 / 3 | — | — |
| `Location` | **100%** | 0 / 12 | — | — |
| `ReplaceWarehouseUseCase` | 94.2% | 9 / 155 | 93.8% | 1 / 16 |
| `Warehouse` (api.beans) | 89.5% | 4 / 38 | — | — |
| `WarehouseRepository` ⚠ | 88.9% | 13 / 117 | 50.0% | 1 / 2 |

> `WarehouseRepository`: the 1 missing branch is `if (db == null) return` in `update()` — a defensive guard that is unreachable in practice because the use case validates existence before calling `update`. Not a testing gap.

### Location

| Class | Instr % | Instr (miss/total) | Branch % | Branch (miss/total) |
|---|---|---|---|---|
| `LocationGateway` | **100%** | 0 / 95 | — | — |

---

## Summary

| Layer | Classes at 100% | Classes ≥ 90% | Classes < 90% |
|---|---|---|---|
| Allocation | 3 | 5 / 5 | 0 |
| Product | 3 | 3 / 4 | `Product` (dead code) |
| Store | 3 | 4 / 5 | `Store` (dead code) |
| Warehouse | 6 | 8 / 9 | `WarehouseRepository` (dead code) |
| Location | 1 | 1 / 1 | 0 |
| **Total** | **16 / 24** | **21 / 24** | **3 (all dead code)** |

The 3 classes below 90% all contain unreachable code that cannot be exercised through HTTP integration tests. No actionable coverage gap exists.
