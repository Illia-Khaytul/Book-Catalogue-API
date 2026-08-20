# Overview

## 1. Main Goal

Create a simple rest api for book management with basic username-password authentication.

## 2. Requirements

- Create, read, update and delete books.
- User registration, password change and deletion.
- Basic authentication with user.
- Filtering and pagination for book read operations.
- Exception handling and custom responses.

## 3. Entities

`Book` entity to persist book data.

`User` entity to persist user data.

## 4. Operations

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

## 5. Security

Basic username-password authentication.

## 6. Exception

Global `RestControllerAdvice` to handle application exceptions and custom `AuthenticationEntryPoint` to handle security exceptions.

Reusable `ErrorResponse` dto to maintain a consistent exception response structure.