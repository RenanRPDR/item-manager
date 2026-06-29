# AI Session Log · item-manager

A record of all AI interactions throughout the development of this project.
Kept for traceability, decision clarity, and transparency.

---

## Table of Contents

1. [JPA Entity — Item](#1--jpa-entity--item)
2. [H2 Database Configuration](#2--h2-database-configuration)
3. [Data Seeder — CommandLineRunner](#3--data-seeder--commandlinerunner)
4. [Refactor DataLoader](#4--refactor-dataloader)
5. [Cleanup & Session Log](#5--cleanup--session-log)
6. [Rename & Redesign Log File](#6--rename--redesign-log-file)
7. [Translate Log to English](#7--translate-log-to-english)
8. [Paginated Search Query Method](#8--paginated-search-query-method)
9. [Repository Integration Tests](#9--repository-integration-tests)
10. [Suppress Hibernate SQL Output](#10--suppress-hibernate-sql-output)
11. [Keep Session Log Updated](#11--keep-session-log-updated)
12. [Service Layer — ItemService](#12--service-layer--itemservice)
13. [Service Unit Tests — ItemServiceTest](#13--service-unit-tests--itemservicetest)
14. [REST Controller — ItemController](#14--rest-controller--itemcontroller)
15. [Bulk Creation Endpoint](#15--bulk-creation-endpoint)
16. [Bulk Creation Service Test](#16--bulk-creation-service-test)
17. [Swagger API Documentation](#17--swagger-api-documentation)
18. [Angular Frontend UI](#18--angular-frontend-ui)
19. [Revert Bulk Creation Endpoint](#19--revert-bulk-creation-endpoint)
20. [ItemController Unit Tests](#20--itemcontroller-unit-tests)
21. [Standardize Test Assertions](#21--standardize-test-assertions)
22. [Test Refactoring and Cleanup](#22--test-refactoring-and-cleanup)
23. [Bulk Add Integration Test (1000+ Items)](#23--bulk-add-integration-test-1000-items)
24. [Entity ID Refactoring (Long to UUID)](#24--entity-id-refactoring-long-to-uuid)
25. [README Optimization and Documentation](#25--readme-optimization-and-documentation)
26. [Frontend UX & UI Refactoring](#26--frontend-ux--ui-refactoring)

---

## 1 · JPA Entity — Item

> *I need to create a JPA entity named `Item` for Spring Boot. It should include an auto-incrementing ID, `name` (String). Generate the class with standard getters/setters and basic annotations.*

**Why:** I started with the entity because all subsequent layers are built on top of it — the repository, service, and controller all depend on it.

---

## 2 · H2 Database Configuration

> *Create the correct setup for an H2 database working with JPA in Spring Boot. The file is located in `\item-manager\backend\src\main\resources\application.properties`.*

**Why:** With the entity in place, the next step was getting the database operational so it could receive data before building the layers above it.

---

## 3 · Data Seeder — CommandLineRunner

> *Generate a Spring Boot configuration class that uses CommandLineRunner to automatically inject 1,200 realistic item records into the H2 database table using the Repository whenever the application starts. Each item must have a unique ID and a unique name (String).*

**Why:** With the database ready, I created the seed layer so the application starts up with data already available for immediate use and testing.

---

## 4 · Refactor DataLoader

> *Refactor the seedDatabase method to use a simple implementation. Populate an array with 1,200 unique words and remove all complex logic.*

**Why:** The previous implementation used 4 arrays and nested loops with `break outer`. The goal was something simpler — a static array of 1,200 names and a straightforward `for-each` loop.

---

## 5 · Cleanup & Session Log

> *Inside the `docs/plans` folder, I need to log the history of all the commands I used to provide traceability and transparency. Keep the `DataLoader` class and remove the old logic in `DataLoaderBKP`. Are there any other unused files in the project? If so, remove them.*

**Why:** Removed the legacy backup file and started the session history log to keep the project clean and traceable.

---

## 6 · Rename & Redesign Log File

> *I need a better name for this history file, and a design that is cleaner, softer, and more meaningful and clear.*

**Why:** The previous name was informal and the format felt dense. The new name is precise and descriptive; the new layout is lighter with a clearer visual hierarchy.

---

## 7 · Translate Log to English

> *Now, translate and keep all information in English.*

**Why:** The log mixed Portuguese context notes with English commands. Keeping everything in English makes it consistent and readable for any audience.

---

## 8 · Paginated Search Query Method

> *Generate a query method in `ItemRepository` for server-side paginated search that filters by name containing a specific text fragment, ignoring case. The method must accept a `Pageable` parameter and return a `Page<Item>` object. Ensure the code follows clean code naming conventions.*

**Why:** The repository needed a derived query to support the search feature — `findByNameContainingIgnoreCase` lets Spring Data JPA generate the correct `LIKE + LOWER` SQL automatically without any manual `@Query`.

---

## 9 · Repository Integration Tests

> *Now, generate a comprehensive repository integration test class named ItemRepositoryTest using JUnit 5 and @DataJpaTest. It should test the paginated search method with different scenarios for a String.*

**Why:** Covered 12 scenarios — exact match, case-insensitive variants, shared fragment, no match, empty string, pagination size and page index, overflow, single character, and full-name match. Fixed a Spring Boot 4.x import change: `@DataJpaTest` moved to `org.springframework.boot.data.jpa.test.autoconfigure`.

---

## 10 · Suppress Hibernate SQL Output

> *How can I remove this output from the log when I start a Spring server or run a test?*

**Why:** `spring.jpa.show-sql=true` and `format_sql=true` were printing every INSERT and SELECT to the console during server startup and test runs. Set both to `false` in `application.properties` to keep the output clean.

---

## 11 · Keep Session Log Updated

> *Update the `ai-session-log` every time; in the end, we will need this history.*

**Why:** From this point forward, the log is updated at the end of every session.

---

## 12 · Service Layer — ItemService

> *Implement the service layer class ItemService annotated with @Service. It must inject ItemRepository (no @Autowired fields for better testability). Add clean methods to handle: paginated search and list fetching, saving a new item, and deleting an item by its ID. Ensure it handles exceptions gracefully if a resource is not found.*

**Why:** The service layer isolates business logic from the controller and the repository. Constructor injection was chosen over `@Autowired` fields to allow plain unit tests without a Spring context. A dedicated `ItemNotFoundException` makes the not-found case explicit and consistent.

---

## 13 · Service Unit Tests — ItemServiceTest

> *Generate the unit test class ItemServiceTest using JUnit 5 and Mockito. Write isolated unit tests to mock ItemRepository behavior and verify that ItemService correctly forwards pagination parameters, processes data transfer objects if necessary, and properly triggers repository deletions. Include a test case for edge scenarios like trying to delete a non-existent item.*

**Why:** Unit tests run without a Spring context — Mockito stubs the repository so each test is fast and isolated. 11 cases cover: `findAll` (full list and empty), `searchByName` (match, no-match, pagination parameters), `save` (return value and call count), `deleteById` (happy path, call count, not-found exception, and no-delete-on-not-found guard).

---

## 14 · REST Controller — ItemController

> *Create a REST controller class named ItemController mapped to /api/items. It should expose three endpoints: a GET request that accepts query parameters for page, size, and search to return a ResponseEntity\<Page\<Item\>\>, a POST request to create an item returning HTTP 201, and a DELETE request to remove an item by ID returning HTTP 204. Use proper RESTful API standards and constructor injection.*

**Why:** The controller is deliberately thin — it only handles HTTP concerns (status codes, request params, path variables) and delegates all logic to the service. Default values for `page` and `size` make the GET endpoint usable without required parameters. HTTP status codes follow REST conventions: `200 OK` for reads, `201 Created` for writes, `204 No Content` for deletes.

---

## 15 · Bulk Creation Endpoint

> *Create another method on the controller that receives an array of Strings and saves many new items at the same time. If it requires building something new in other layers, do that.*

**Why:** A bulk endpoint is much more efficient than hitting the server multiple times for single insertions. We utilized the `saveAll` method already present in `ItemService` and exposed it at `POST /api/items/bulk`, accepting a JSON array of strings (`List<String>`) and returning the list of created items.

---

## 16 · Bulk Creation Service Test

> *Create a test for the new method that saves items in bulk.*

**Why:** A unit test case `saveAll_shouldMapStringsToItemsAndSave` was added in `ItemServiceTest`. It mocks `ItemRepository.saveAll()` and leverages Mockito's `argThat` to capture and assert that the method correctly maps a given list of strings into a corresponding list of `Item` entities before passing them to the repository for persistence. This guarantees the bulk insertion flow operates reliably.

---

## 17 · Swagger API Documentation

> *Create API documentation using Swagger on my controller.*

**Why:** Using `springdoc-openapi-starter-webmvc-ui` in the `pom.xml`, the Swagger UI provides an interactive way to view, explore, and test the endpoints. Added OpenAPI annotations like `@Tag`, `@Operation`, `@ApiResponse`, and `@Parameter` to `ItemController` to accurately document the API's behavior, expected parameters, and HTTP response codes.

---

## 18 · Angular Frontend UI

> *Now create a lightweight UI with Angular. My frontend needs: a listing page, the ability to add and remove items, and a responsive experience with 1,000+ records in the dataset. Look at the `ItemController`...*

**Why:** A new Angular application was scaffolded in the `frontend` directory. Standalone components were used to build a streamlined architecture. The UI implements server-side pagination to efficiently handle the 1,200 seeded database records without loading them all into the browser memory at once. A premium dark-mode, glassmorphism aesthetic was applied using custom CSS and the *Inter* font. The backend `ItemController` was also updated with `@CrossOrigin` to allow requests from `localhost:4200`.

---

## 19 · Revert Bulk Creation Endpoint

> *Remove the `createItems` method and all parts necessary to remove this endpoint. If changes are needed in another layer to keep everything working, do that.*

**Why:** The `POST /api/items/bulk` endpoint was deemed no longer necessary. We cleanly removed it from `ItemController`, deleted the underlying `saveAll` business logic from `ItemService`, and removed the corresponding unit test suite from `ItemServiceTest` to ensure all layers remain in sync without dead code.

---

## 20 · ItemController Unit Tests

> *Create a unit test for the `ItemController`.*

**Why:** To ensure the API endpoints function correctly at the web layer, a comprehensive `ItemControllerTest` was created using `@WebMvcTest`. It mocks the `ItemService` and uses `MockMvc` to verify the `GET`, `POST`, and `DELETE` requests. Additionally, `@ResponseStatus(HttpStatus.NOT_FOUND)` was added to `ItemNotFoundException` to ensure the controller inherently translates that exception into a proper 404 HTTP response during the delete operation.

---

## 21 · Standardize Test Assertions

> *Verify the best way to maintain the test scenarios. Maybe it is better to use `assertThat` to keep the same strategy as the other tests. In `createItem_shouldCreateAndReturnItem` and `deleteItem_shouldReturn204`, we are using Mockito...*

**Why:** To ensure a consistent "given, when, then" strategy across the test suite, we replaced loose Mockito assertions (`Mockito.verify` with `any()`) in `ItemControllerTest` with explicit `ArgumentCaptor` and AssertJ's `assertThat`. This provides strict, type-safe validation of the arguments passed between layers and unifies the assertion style throughout the codebase.

---

## 22 · Test Refactoring and Cleanup

> *Now verify if all tests follow good practices. For example, if any setup repeats, refactor and instantiate it only once for multiple usages.*

**Why:** To adhere to DRY (Don't Repeat Yourself) principles, repetitive code like the `PageRequest.of(0, 10)` instantiation in `ItemServiceTest` was extracted into a shared `defaultPageable` property initialized centrally inside the `@BeforeEach` setup block. This makes the tests cleaner and easier to maintain.

---

## 23 · Bulk Add Integration Test (1000+ Items)

> *I need a specific test to test adding 1,000+ items. You can use the `DataLoader` class to retrieve all items from the `.csv` file.*

**Why:** To ensure the system correctly handles large datasets (1000+ items), a new integration test (`shouldHandleMoreThan1000Items`) was added to `ItemRepositoryTest`. The `seedDatabase` method in `DataLoader` was made `public` to allow the test to invoke it. This test utilizes the data loader to load from `items.csv` and verifies that the database successfully loads and persists over 1,000 items without errors.

---

## 24 · Entity ID Refactoring (Long to UUID)

> *My Entity is using `GenerationType.IDENTITY` to generate a `Long` ID, but I want to use `UUID` to provide random IDs for security and best practices. Refactor the entity to use `UUID`, along with all layers and files necessary to keep the `UUID` working.*

**Why:** Using UUIDs instead of auto-incremented `Long` IDs provides better security by preventing ID enumeration attacks and ensures global uniqueness across distributed systems. We refactored `Item.java` to use `java.util.UUID` with `@GeneratedValue(strategy = GenerationType.UUID)`. This required corresponding type updates throughout the stack: `ItemRepository` generics, method signatures in `ItemService` and `ItemController`, and updating frontend TypeScript models (`item.model.ts`) to treat IDs as `string`. Finally, we overhauled all unit and integration tests to generate and expect mock UUIDs instead of hardcoded numeric IDs.

---

## 25 · README Optimization and Documentation

> *Create a good and clean `README.md` to get the application running quickly... TL;DR principle! Direct to the point... Remove the icons, remove extensive text... add a section with frameworks, libraries, and versions used.*

**Why:** To ensure the technical exercise reviewers can get the application up and running instantly without friction, the `README.md` was rewritten following a strict "TL;DR" philosophy. We removed decorative icons and lengthy context in favor of direct, copy-pasteable startup commands (incorporating `./mvnw clean install`). Furthermore, we added a clear **Tech Stack** section detailing the exact frameworks and versions utilized in the project (e.g., Java 21, Spring Boot 4.1.0, Angular 22.0.0, JUnit 6.0.3, and clarifying the use of Vite via Angular's esbuild system without Angular Material).

---

## 26 · Frontend UX & UI Refactoring

> *Refactor the Add New Item and Search Item menus. It is a little confusing. Use good UX/UI design practices... Change the Item Vault name to Item Manager... remove the star icon in the enter new item name input.*

**Why:** The initial frontend design had disconnected "Search" and "Add Item" functions in separate, vertically-stacked glass panels, which made the UI feel fragmented. To align with modern UX best practices for data tables, we refactored these components into a unified, cohesive toolbar positioned directly above the table (Search on the left, Add Item on the right). We also removed the redundant "Item Vault" sub-header and title tags, replacing them with a cleaner "Item Manager" branding across `app.component.ts` and `index.html`. Finally, we removed unnecessary emojis (like the star icon in placeholders) to maintain a sleek, professional, and intuitive interface.
