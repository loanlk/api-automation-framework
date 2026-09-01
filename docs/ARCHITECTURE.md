# Project structure standard — api-automation-framework

This document describes the **actual current structure** of the framework (verified against the codebase, not aspirational). The `Booking` CRUD flow is the most complete reference implementation; `AuthTest` is the reference for assertion style since it was the last class migrated to the current convention.

## Package layout

```
src/main/java/com/herokuapp/restfulbooker/
├── api/                       # One class per resource — builds & sends HTTP requests, returns raw Response
│   ├── BaseApi.java               # requestSpec() — the one place RequestSpecification (baseUri, contentType,
│   │                               # CurlLoggingFilter, AllureRestAssured) is built. Every *Api extends this.
│   ├── AuthApi.java                # login(), submitAuthWithoutBody()
│   ├── BookingApi.java             # full CRUD surface for /booking (get/create/update/patch/delete)
│   ├── HealthApi.java              # getPing() — GET /ping
│   └── TokenManager.java           # ThreadLocal<String> token cache — see "Auth & parallel-safety" below
├── config/
│   ├── ConfigReader.java           # Generic .properties loader — loads config/{env}.properties, where env
│   │                               # comes from -Denv=... (default "qa"; see pom.xml systemPropertyVariables)
│   ├── EnvironmentConfig.java      # Typed accessors: getBaseUrl(), getEnvironment()
│   └── Credentials.java            # username()/password() — env var (AUTH_USERNAME/AUTH_PASSWORD) first,
│                                   # falls back to qa.properties
├── constants/                  # Magic strings/numbers live here, nowhere else
│   ├── ApiEndpoints.java           # /auth, /booking, /ping
│   ├── AuthConstants.java          # TOKEN_PATTERN, BAD_CREDENTIALS
│   └── HttpStatusCodes.java        # Named status codes with inline comments documenting API-specific quirks
│                                   # (e.g. GONE_OR_INVALID_ID = 405, VALIDATION_ERROR = 500) — use these,
│                                   # not org.apache.http.HttpStatus or bare int literals
├── models/
│   ├── Booking.java                 # Canonical request/response Booking POJO — boxed Integer/Boolean so a
│   │                                 # missing field deserializes to null instead of 0/false
│   ├── BookingDates.java
│   ├── BookingFilter.java           # Typed DTO for known GET /booking query params (firstname, lastname,
│   │                                 # checkin, checkout) → .toQueryMap()
│   ├── BookingId.java                # NOT currently used anywhere — see "Known gaps"
│   ├── request/
│   │   └── AuthRequest.java         # POST /auth body — has a 2-arg convenience constructor, also reused
│   │                                 # directly as the field type in AuthTestData (no separate "Auth" POJO)
│   ├── response/
│   │   └── CreateBookingResponse.java  # { bookingid, booking: Booking } — deserialize with .as(...) rather
│   │                                     # than raw .path() extraction
│   └── testdata/
│       ├── AuthTestData.java         # 5x AuthRequest (validUser, wrongPassword, ...)
│       ├── BookingIdsTestData.java   # One field per test scenario — mixes typed POJOs (Booking, BookingFilter,
│       │                             # BookingWithMeta, IdCase) for valid-path data with raw
│       │                             # Map<String,Object> fields for deliberately malformed/negative-path
│       │                             # data (see "Typed vs raw test data" below — this split is intentional)
│       ├── BookingWithMeta.java      # Booking + Accept/Cookie/contentType/id/expected-responseBody bundled
│       │                             # together, for scenarios that need more than just a request body
│       └── IdCase.java               # { id: Object, Accept } — id is Object on purpose (holds both numeric
│                                     # and non-numeric invalid ids for negative tests)
└── utils/
    ├── TestDataUtils.java          # readJsonFile(classpathPath, Class<T>) — Jackson ObjectMapper with
    │                                 # FAIL_ON_UNKNOWN_PROPERTIES disabled
    └── CurlLoggingFilter.java      # REST Assured Filter — builds a reproducible curl command per request,
                                     # redacts Authorization/Cookie header values, stores it in a
                                     # ThreadLocal<String> (thread-safe under parallel execution)

src/test/java/com/herokuapp/restfulbooker/
├── BaseTest.java                # @BeforeMethod instantiates authApi/bookingApi/healthApi.
│                                 # getAuthToken() → TokenManager.getToken(); getBasicAuthCredentials()
│                                 # (cached per-instance) for the Basic-Auth flow.
│                                 # @AfterMethod calls CurlLoggingFilter.clear() (ThreadLocal cleanup).
│                                 # attachCurlEvidence/attachReturnValue/attachLoadedJson — Allure attachments.
├── AuthTest.java
├── CreateBookingRequestTest.java   # note the actual naming below differs from the old "<Resource>Test" rule
├── GetBookingTest.java
├── GetBookingIdsTest.java
├── UpdateBookingRequestTest.java
├── DeleteBookingRequestTest.java
├── HealthCheckTest.java
├── listeners/
│   ├── AnnotationTransformer.java    # registers RetryAnalyzer on every @Test
│   ├── RetryAnalyzer.java            # retries a failed test up to 2 times (instance field, safe per-test)
│   └── CurlOnFailureListener.java    # onTestFailure() — attaches CurlLoggingFilter.getLastCurl() to Allure
└── utils/
    ├── RetryUtils.java             # retryUntilSuccess(Supplier<Integer>, maxAttempts, waitMs) — blocking
    │                                 # Thread.sleep between attempts, used after create to poll until GET
    │                                 # returns 200 (eventual consistency)
    └── config/TestConfig.java      # MAX_RETRY_ATTEMPTS = 3, RETRY_WAIT_MS = 300

src/main/resources/config/
├── qa.properties                  # base.url, environment, timeout, auth.username, auth.password — default (-Denv=qa)
└── staging.properties             # same keys — selected via -Denv=staging

src/test/resources/
├── testng.xml                     # <suite parallel="classes" thread-count="4"> — see "Parallel execution"
├── allure.properties              # allure.results.directory=target/allure-results
├── createBookingSchema.json, getBookingIdsSchema.json, getBookingbyIdSchema.json   # JSON Schema files,
│                                    # validated via io.restassured.module.jsv.JsonSchemaValidator
└── com/herokuapp/restfulbooker/models/testdata/
    └── auth-data.json, booking-data.json, bookingIds-data.json
```

## Layer responsibilities (top to bottom of a test call)

1. **Test class** — orchestration only: load test data, call the `*Api`, assert on the `Response`. No request-building, no hardcoded URLs.
2. **BaseTest** — `@BeforeMethod` wires up `authApi`/`bookingApi`/`healthApi`; `@AfterMethod` clears the curl `ThreadLocal`. Test classes never call `new XxxApi()` themselves.
3. **Test data** (`*TestData` POJO + JSON file) — loaded via `TestDataUtils.readJsonFile(...)`. See "Typed vs raw test data" for when a field is a real POJO vs a raw `Map`.
4. **`*Api` class** — one method per operation, builds the request via `requestSpec()` from `BaseApi`, hits the endpoint from `ApiEndpoints`, returns the raw `Response`. **Never asserts** — that's the test's job.
5. **`BaseApi`** — the single place `RequestSpecification` is built (baseUri, contentType, curl-logging filter, Allure filter). There is currently no shared "authenticated spec" helper — each `BookingApi` method that needs auth attaches `.cookie("token", token)` itself, with the token passed in as a `String` parameter.
6. **`EnvironmentConfig` / `ConfigReader`** — the only path to environment values. No class outside `config/` should read a `.properties` file directly.
7. **`ApiEndpoints` / `AuthConstants` / `HttpStatusCodes`** — the only place path strings, reusable literals, and HTTP status codes are defined. `HttpStatusCodes` constants carry inline comments documenting cases where the API's actual status code is non-standard for the scenario (e.g. 405 instead of 404) — always use these over a bare int or a generic library's status constants.
8. **`*Request` / `*Response` models** — request and response bodies are POJOs, serialized/deserialized by REST Assured + Jackson via `.body(pojo)` / `.as(Pojo.class)`. XML responses use `response.xmlPath()` instead (see below) — never `.as(...)` on an XML body, it will throw.

## Assertion & verification conventions

The suite deliberately uses **TestNG `Assert`/`SoftAssert`**, not REST Assured's `given/when/then` DSL or Hamcrest matchers — every test class was migrated to this single style for consistency. Pattern for a test that checks more than the status code:

```java
Response response = bookingApi.createBooking(requestBooking);

// GATE — hard assert. If this fails, there's nothing valid to inspect further.
assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
        .matches(response.getBody().asString());
assertTrue(matchesSchema, "response body does not match createBookingSchema.json");

// DETAILS — soft assert. Independent fields, want every mismatch reported together.
CreateBookingResponse created = response.as(CreateBookingResponse.class);
SoftAssert softAssert = new SoftAssert();
softAssert.assertEquals(created.getBooking().getFirstname(), requestBooking.getFirstname(), "firstname mismatch");
softAssert.assertEquals(created.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
softAssert.assertAll();
```

Rules of thumb:
- **Hard-assert** a precondition/gate (status code, schema shape) — if it fails, everything downstream is meaningless.
- **Soft-assert** ≥2 independent field checks on the same response — always with a message, since `assertAll()` reports every failure together and an unlabeled `expected [x] but found [y]` doesn't say which field broke.
- A single check after the gate doesn't need `SoftAssert` — plain `assertEquals(..., "message")` is enough.
- **XML responses** (`Accept: application/xml`): use `response.xmlPath()` (built into REST Assured, same path syntax as `JsonPath`), never `JsonSchemaValidator`/`.as(Pojo.class)` — both assume JSON and throw on `<`.

## Typed vs raw test data

`BookingIdsTestData` intentionally mixes two styles:
- **Typed POJO** (`Booking`, `BookingFilter`, `BookingWithMeta`, `IdCase`, `AuthRequest`) for anything representing a *valid* request — compile-time safety, no stringly-typed key lookups.
- **Raw `Map<String,Object>`** only for fields that must hold a deliberately malformed value for a negative test (e.g. `createWrongPriceDataType`, `filterMalformedDate`, `partialUpdateWithInvalidFieldType`) — forcing these into a typed field would throw at `@BeforeClass` load time instead of at the point the test wants it to fail.

When adding a new test-data field: default to the typed POJO; only fall back to `Map<String,Object>`/`Object` when the test's whole point is an invalid shape or type.

## Auth & parallel-safety

- `TokenManager.getToken()` caches the login token in a **`ThreadLocal<String>`** (not a plain `static` field) — each thread gets its own token, so classes running concurrently under parallel execution don't share/overwrite each other's session.
- `CurlLoggingFilter.LAST_CURL` is also `ThreadLocal`, cleared in `BaseTest`'s `@AfterMethod`.
- **These two are the only non-final static/shared state in the framework** (verified by grepping for `private static` without `final`). Any new shared cache must follow the same `ThreadLocal` pattern — a plain `static` field will silently corrupt data as soon as more than one class runs at once.
- `testng.xml` runs with `<suite parallel="classes" thread-count="4">`. This parallelizes **across classes only** — methods within one class still run sequentially on a single thread, because several test classes (`UpdateBookingRequestTest`, `DeleteBookingRequestTest`, `GetBookingTest`) store per-test state (`bookingId`) in an **instance field** written in `@BeforeMethod` and read in `@Test`. TestNG reuses one instance per class, so `parallel="methods"` would race on that field. Do not raise parallelism above `classes` without first moving that state out of instance fields (e.g. into a local variable returned from an inlined setup call, the pattern already used in `AuthTest`).

## Naming conventions

- Test class: `<Resource><Operation>Test` for booking CRUD (`CreateBookingRequestTest`, `GetBookingTest`, `GetBookingIdsTest`, `UpdateBookingRequestTest`, `DeleteBookingRequestTest`), plain `<Resource>Test` for single-concept flows (`AuthTest`, `HealthCheckTest`).
- Api class: `<Resource>Api` (`AuthApi`, `BookingApi`, `HealthApi`).
- Request/response models: `<Resource>Request` (`models/request/`), `<Resource>Response` (`models/response/`). Domain POJOs shared between request and response bodies (e.g. `Booking`) live directly under `models/`, not under `request/`.
- Test-data models: `<Resource>TestData` (the JSON root object), with per-entry helper POJOs (`BookingWithMeta`, `IdCase`) where entries need more than a plain domain POJO.
- Test-data JSON file: `<resource>-data.json`, at `src/test/resources/<mirrored package path>/`, matching the classpath string passed to `TestDataUtils.readJsonFile`.
- Test-data JSON keys must match the `*TestData` POJO's field names exactly — unknown-property failures are disabled, so drift between JSON and POJO is a silent bug (a misspelled setter with an empty body has caused a real production bug in this codebase before — always verify a new field actually gets assigned).

## Hard rules (no exceptions)

- No hardcoded absolute URLs anywhere. Always `ApiEndpoints` + `BaseApi.requestSpec()`.
- No hand-written JSON strings for request bodies for valid-path tests — always a POJO. Raw strings/maps are reserved for deliberately malformed negative-path data (see "Typed vs raw test data").
- Use `HttpStatusCodes` constants for every status-code assertion — never a bare int literal or another library's status constant (`org.apache.http.HttpStatus`, etc.).
- No commented-out dead code left in a file. Delete it; Git history is the changelog.
- Every test class that should run must be listed in `testng.xml` — an unlisted class is invisible to `mvn test` even if it compiles and passes standalone.
- No new `static` mutable field without `ThreadLocal` (or an equivalent per-thread strategy) — the suite runs `parallel="classes"`.
- Test-data files live under `src/test/resources/`, never `src/main/`.

## Environment selection

```bash
mvn test -DtestGroup=smoke                  # config/qa.properties (default)
mvn test -DtestGroup=smoke -Denv=staging    # config/staging.properties
```
`ConfigReader` loads `config/{env}.properties` from the classpath, where `env` comes from the `-Denv=...` system property (default `"qa"`, set via `pom.xml`'s `<properties><env>qa</env></properties>`). The surefire plugin's `<systemPropertyVariables>` block is what forwards `-Denv=...` into the forked test JVM — `mvn -Denv=staging` alone only sets it for the Maven process itself, not for the tests, without that block. An unknown `env` value fails fast at class-init time with a clear "config/xxx.properties was not found", not a silent fallback. Adding a new environment = add `config/<name>.properties` with the same keys as `qa.properties` (`base.url`, `environment`, `timeout`, `auth.username`, `auth.password`) — no code change needed.

## Reporting (Allure)

```bash
mvn clean test -DtestGroup=smoke     # writes results to target/allure-results
mvn allure:report                    # builds static report to target/site/allure-maven-plugin
allure open target/site/allure-maven-plugin   # serves it over HTTP (required — file:// won't load the JSON data)
```
Or in one step for local viewing: `mvn allure:serve` (builds and serves immediately, holds the terminal until `Ctrl+C`).

`CurlOnFailureListener` attaches the last curl command to Allure on any test failure, and `BaseTest.attachCurlEvidence()`/`attachReturnValue()`/`attachLoadedJson()` let a test explicitly attach evidence (e.g. the booking id created in a fixture) for easier debugging from the report alone.

## Known gaps (accurate as of this writing — not architectural intent)

- `models/BookingId.java` is not referenced anywhere in the codebase — dead code.
- `BaseApi` has no shared "authenticated spec" helper; the token is threaded manually as a `String` parameter into every `BookingApi` method that needs it. A missed call site fails open (silently unauthenticated) rather than failing to compile.
- `TokenManager` never refreshes an expired token (`ThreadLocal` fixes the parallel-safety issue but not staleness) — if the API ever invalidates a session mid-run, callers get a stale token with no automatic re-login.

## Checklist for adding a new flow

1. Add the domain POJO(s) under `models/` (boxed types for fields that can legitimately be missing).
2. Add a `*Request` model under `models/request/` only if the request shape differs from the domain POJO; otherwise reuse the domain POJO directly (see `AuthRequest`, which is used both as the request body and as the test-data field type).
3. Add a `*Response` model under `models/response/` if the response shape needs typed field assertions.
4. Implement `*Api` in `api/`, extending `BaseApi`, one method per operation, using `ApiEndpoints`. Keep the method `Object`-typed only for parameters that a negative test needs to pass an invalid value into.
5. Add a `*TestData` model in `models/testdata/` + JSON file in `src/test/resources/.../testdata/`, typed POJOs for valid-path fields, `Map<String,Object>` only for deliberately malformed ones.
6. Add any new status codes to `HttpStatusCodes` with a comment if the API's behavior for that scenario is non-standard.
7. Write the test class using the hard-assert-gate + soft-assert-details pattern; use `response.xmlPath()` instead of `.as(...)`/schema validation for any XML response.
8. If the flow needs auth, source the token via `BaseTest.getAuthToken()` (backed by the `ThreadLocal`-safe `TokenManager`) — never introduce a new `static` cache.
9. Add the new test class to `testng.xml`.
10. Run `mvn clean test -DtestGroup=smoke` twice in a row before considering it done — the suite runs `parallel="classes"`, and a race condition from new instance-field state may not show up on the first run.
