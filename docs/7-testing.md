# Testing

Overall testing architecture and requirement specification.

Uses Docker to utilize Testcontainers for testing databases.

## 1. Unit tests

Unit tests for services and helper components.

Utilizes Mockito to mock dependencies and stub method calls.

Focus on the main logic flow of each method.

## 2. Slice tests

Slice tests for controllers and controller advice.

Utilizes `@WebMvcTest` and MockMmv to mock the controller layer of the application. Disables security with `addFilter = false`.

Controller advice tests utilize the `BookController` to generate exceptions via stubbing/validation.

Controller tests focus on response assertion and request validation failure message assertion. While controller advice focus on the correct error response generation.

## 3. Integration tests