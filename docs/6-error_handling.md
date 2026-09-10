# Exceptions

Custom exception definition and handling.

## 1. Thrown exceptions

Exceptions thrown by the application.

**Normal:**
- `MethodArgumentNotValidException`: when `@Validated` validation fails
- `HandlerMethodValidationException`: when `@Valid` validation fails
- `HttpMessageNotReadableException`: when the request body is malformed
- `NoResourceFoundException`: when no exposed endpoint matches the request

## 2. Application exception handling

All exceptions return a common `error response` to keep the response consistent.

Fields:
- Instant `timestamp`: when was the response sent
- int `status`: the status code
- String `message`: error message, by default the direct exception message
- Map<String, String> `data`: some exceptions may contain additional data

**MethodArgumentNotValidException handler** -> 400 Bad Request

`message` = Invalid request parameters

`data` = validation errors

**HandlerMethodValidationException handler** -> 400 Bad Request

`message` = Invalid request parameters

`data` = validation errors

**HttpMessageNotReadableException handler** -> 400 Bad Request

`message` = Invalid request body

**NoResourceFoundException handler** -> 404 Not Found

`message` = Resource not found

**Generic exception handler** -> 500 Internal Server Error

Catches any other unexpected exception and logs the stack trace

`message` = Something went wrong
