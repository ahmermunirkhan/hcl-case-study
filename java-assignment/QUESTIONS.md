# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes — I would standardize on the Repository pattern and remove the Active Record usage from Store.

Currently there are two approaches in play:

- Store uses Panache Active Record (Store extends PanacheEntity), mixing persistence
  behaviour directly into the entity class.
- Product uses a plain @Entity + ProductRepository implements PanacheRepository<Product>,
  keeping the entity as a data object.
- Warehouse goes furthest: DbWarehouse is a plain @Entity adapter, WarehouseRepository
  implements both PanacheRepository<DbWarehouse> and the domain port WarehouseStore,
  fully decoupling persistence from domain logic.

I would refactor Store to follow the same Repository pattern (extract a StoreRepository)
for two reasons:

1. Testability: Active Record entities cannot be unit-tested without a running persistence
   context. Repository beans can be substituted with in-memory fakes in use-case tests,
   which run in milliseconds with no container overhead.

2. Consistency: maintaining two patterns across a single codebase means every new
   developer has to hold both mental models simultaneously. Standardising reduces
   onboarding friction and makes the codebase predictable.

The Warehouse domain's hexagonal approach — domain port (WarehouseStore) implemented by
an infrastructure adapter (WarehouseRepository) — is the cleanest of the three. If the
Store or Product domains grew more complex business rules, I would consider applying the
same port/adapter split there too, but I would not do it preemptively; the current
Repository pattern is sufficient for their current scope.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Contract-first (OpenAPI YAML → generated interface → WarehouseResourceImpl):

Pros:
- The YAML is the single source of truth. The compiler enforces that the implementation
  satisfies the contract; drift between docs and code is structurally impossible.
- Client SDKs for consumers can be generated from the same YAML, keeping both sides in
  sync automatically.
- Request/response validation and serialisation rules are declared once in the spec, not
  scattered across JAX-RS annotations.
- Enables API-design-first workflow: the contract can be reviewed and agreed upon before
  a single line of implementation is written.

Cons:
- Adds a code-generation build step; generated sources can be confusing to navigate when
  debugging.
- Regenerating after a spec change requires care to avoid overwriting manual
  customisations in the generated output.
- Initial setup overhead (plugin config, package mapping) is non-trivial.

Code-first (hand-written JAX-RS — ProductResource, StoreResource):

Pros:
- No tooling overhead; simple to start and easy to trace end-to-end.
- Full control over every annotation and class structure.

Cons:
- The contract lives implicitly in the code. Keeping documentation (if any) accurate
  requires manual discipline that degrades over time, especially across teams.
- No generated client; consumers must read source or reverse-engineer the API.
- Both ProductResource and StoreResource contain a duplicated inner ErrorMapper class,
  which is a direct consequence of there being no shared contract layer enforcing
  consistency.

My choice: contract-first for all endpoints.

The consistency and correctness guarantees outweigh the setup cost, particularly as an
API evolves and gains multiple consumers. I would migrate Product and Store to follow the
same pattern as Warehouse — define an OpenAPI spec, generate the interface, implement it
— and consolidate the duplicated ErrorMapper into a shared provider. The one-time
migration cost pays back quickly in reduced integration bugs and easier onboarding for
new consumers.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would prioritise in three layers, ordered by feedback speed and business risk:

1. Domain use-case unit tests (highest priority)

The warehouse use-case classes (CreateWarehouseUseCase, ReplaceWarehouseUseCase,
ArchiveWarehouseUseCase) contain all the meaningful business validation — BU code
uniqueness, location validity, capacity constraints, stock matching on replace. The test
stubs already exist (CreateWarehouseUseCaseTest, ReplaceWarehouseUseCaseTest,
ArchiveWarehouseUseCaseTest) but are empty.

These tests run without a Quarkus container, using in-memory fakes of the domain ports
(WarehouseStore, LocationGateway) instead of mocking frameworks. They run in
milliseconds, can cover every validation branch cheaply, and form the primary regression
net for the most complex logic in the codebase.

2. @QuarkusTest integration tests for each resource (medium priority)

ProductEndpointTest already demonstrates this pattern — @QuarkusTest boots the full CDI
container and an in-memory H2 database, and REST-assured exercises the HTTP layer. I
would add equivalent tests for StoreResource and WarehouseResourceImpl, covering the
happy path and the key error cases (404, 409, 400) for each endpoint.

These are slower than unit tests but validate the wiring between the HTTP layer,
use-cases, and the database. They catch misconfigured CDI injection and transaction
boundary issues — precisely the kind of problem that the Store task in the assignment
addresses (ensuring LegacyStoreManagerGateway is called post-commit).

I would prefer @QuarkusTest over @QuarkusIntegrationTest (as used in
WarehouseEndpointIT) for CI, since @QuarkusIntegrationTest requires a full package build
and is significantly slower. @QuarkusIntegrationTest is valuable for pre-release
smoke-testing but should not be the primary test harness.

3. Keeping coverage effective over time

- Tie new feature PRs to tests: every new use-case method or endpoint handler should
  ship with at least one unit test and one integration test as part of the same change.
- Avoid coverage-number targets: a percentage target incentivises writing tests that
  assert nothing meaningful just to hit the number. Instead, review test quality in code
  review — does the test fail when the behaviour it describes breaks?
- Keep the test pyramid honest: if the domain use-case tests are comprehensive, the
  integration tests do not need to re-verify every validation rule — they verify the
  layer wiring, not the business logic.
```