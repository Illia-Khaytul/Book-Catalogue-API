# Testing

Overall testing architecture and requirement specification.

Uses Docker to utilize Testcontainers for testing databases.

Configures Testcontainers in 2 ways. 
- A configuration bean to load the testcontainer instance into the application context for full application initialization.
- An abstract parent class with a static instance that allows reuse across test classes for tests that load only part of the context (e.g. `DataJpaTest` tests).
Both configurations are located under the `/config` folder in the test folder.

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

Same configuration as controller tests (with `@WebMvcTest`). Utilizes the `BookController` to generate exceptions via stubbing/validation.

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

Loads only the database layer of the application and the required services `BookService`and `BookSpecificationBuilder`. For that reason it requires the abstract testcontainer parent configuration.

Focus on testing if the generated specifications produce the required results.

**End to end IT**

Loads the full application with a random port (`webEnvironment = RANDOM_PORT`) to test how the application all together.

Utilizes `TestRestClient` to test the api.

Focus on the correct function (happy path) of each endpoint and the return of the appropriate error responses in case of failure (error path).

End to end tests have a `PER_CLASS` test lifecycle because it is necessary for successful authentication. Since some api endpoints require authentication it is necessary to have a user present in the databse. To register said user it is necessary to persist it before each test (because of database cleanup) with a password hash, which is generated once inside of the `@BeforeAll` method with a `PasswordEncoder` dependency injected after test class instantiation.