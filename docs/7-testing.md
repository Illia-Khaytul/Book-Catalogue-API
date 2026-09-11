# Testing

Overall testing architecture and requirement specification.

Uses Docker to utilize Testcontainers for testing databases.

Disables the base servlet path for testing with the `test` profile for consistent test behavior.

## 1. Unit tests

Unit tests for services and helper components.

Utilizes Mockito to mock dependencies and stub method calls.

Focus on the main logic flow of each method.

## 2. Slice tests

Slice tests for controllers, controller advice and repositories.

**Controller slice tests**

Utilizes `@WebMvcTest` and MockMvc to mock the web layer of the application and disables security with `addFilter = false`.

Controller tests focus on response assertion and request validation failure message assertion.

**Controller advice slice tests**

Same configuration as controller tests (with `@WebMvcTest`). Utilizes a dummy REST controller to generate exceptions via stubbing/bean validation.

Focus on the correct error response generation.

**Repository slice tests**

Utilizes `DataJpaTest` with Testcontainers to test on real database interactions.

Focus on the correct operation of custom and derived repository methods.

## 3. Integration tests

Integration tests for retriable `updateBook` and BookSpecificationsBuilder for `getBooks` (cannot be properly verified with unit tests), as well as end to end tests for the full api.

**Retriable book update IT**

Loads the full application with no web layer (`webEnvironment = NONE`) to allow Spring AOP to successfully create the retry proxy.

Focus on the correct retry operation of the method and not on the correct transactional rollback, load new entity, try again behavior since it is not possible to accurately simulate the necessary concurrent modification conditions to trigger the retry requirements.

**BookSpecificationBuilder IT**

Loads only the database layer of the application and the required services `BookService`and `BookSpecificationBuilder`. 
The tested behavior is located very close to the persistence layer, so there is no need to initialize the full context.

Focus on testing if the generated specifications produce the required results.

**End to end IT**

Loads the full application with a random port (`webEnvironment = RANDOM_PORT`) to test how the application works all together.

Utilizes `TestRestClient` to test the api.

Focus on the correct function (happy path) of each endpoint and the return of the appropriate error responses in case of failure (error path).

Since most endpoints require authentication it is necessary to have a user present in the database.
Persisting a user requires password encoding with an injected `PasswordEncoder`.
To avoid unnecessary overhead of password encoding before each test, the test's lifecycle is set to `PER_CLASS` and the password is encoded once before all tests.