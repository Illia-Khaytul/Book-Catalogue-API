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

Integration tests for component interactions and functionality that cannot be tested in isolation and full end to end application tests.
