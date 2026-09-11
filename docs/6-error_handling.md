# Exceptions

Custom exception definition and handling.

## 1. Thrown exceptions

Exceptions thrown by the application.

**Custom:**
- `duplicate exception`: when a certain element already exists
- `not found exception`: when a given entity does not exist
- `invalid password exception`: when the new password is invalid
- `user not authenticated exception`: when the authentication does not contain the custom user details

**Normal:**
- `MethodArgumentNotValidException`: when `@Validated` validation fails
- `HandlerMethodValidationException`: when `@Valid` validation fails
- `OptimisticLockingFailureException`: when concurrent entity modification is detected
- `HttpMessageNotReadableException`: when the request body is malformed
- `NoResourceFoundException`: when no exposed endpoint matches the request

## 2. Application exception handling

All exceptions return a common `error response` to keep the response consistent.

Fields:
- Instant `timestamp`: when was the response sent
- int `status`: the status code
- String `message`: error message, by default the direct exception message
- Map<String, String> `data`: some exceptions may contain additional data

**Duplicate exception handler** -> 409 Conflict

**Not found exception handler** -> 404 Not Found

**Invalid password exception handler** -> 400 Bad Request

**User not authenticated exception handler** -> 401 Unauthorized

`message` = User is not authenticated

**MethodArgumentNotValidException handler** -> 400 Bad Request

`message` = Invalid request parameters

`data` = validation errors

**HandlerMethodValidationException handler** -> 400 Bad Request

`message` = Invalid request parameters

`data` = validation errors

**OptimisticLockingFailureException handler** -> 409 Conflict

`message` = Concurrent modification error

**HttpMessageNotReadableException handler** -> 400 Bad Request

`message` = Invalid request body

**NoResourceFoundException handler** -> 404 Not Found

`message` = Resource not found

**Generic exception handler** -> 500 Internal Server Error

Catches any other unexpected exception and logs the stack trace

`message` = Something went wrong

## 3. Security exception handling

A custom `AuthenticationEntryPoint` to handle authentication exceptions in the security filter chain.
Returns with status code 401 Unauthorized.
