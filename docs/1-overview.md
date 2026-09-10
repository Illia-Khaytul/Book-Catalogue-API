# Overview

## 1. Index
1. [Overview](/docs/1-overview.md) (this)
2. [Entities](/docs/2-entities.md)
3. [Security](/docs/3-security.md)
4. [Operations](/docs/4-operations.md)
5. [Api Contract](/docs/5-api_contract.md)
6. [Error Handling](/docs/6-error_handling.md)
7. [Testing](/docs/7-testing.md)

## 2. Goals

Create a simple rest api for book management with basic username-password authentication.

**Requirements:**
- Create, read, update and delete books.
- User registration, password change and deletion.
- Basic authentication with user.
- Filtering and pagination for book read operations.
- Exception handling and custom responses.

## 3. Entities

`Book` entity to persist book data.

`User` entity to persist user data.

## 4. Security

Basic username-password authentication using the `User` entity.

## 5. Operations

**Book:**
- Create book
- Update book
- Get book
- Get books
- Delete book

**User:**
- Create user
- Change user password
- Delete user

## 6. Exception

Global `RestControllerAdvice` to handle application exceptions and custom `AuthenticationEntryPoint` to handle security exceptions.

Reusable `ErrorResponse` dto to maintain a consistent exception response structure.
