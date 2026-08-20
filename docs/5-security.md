# Security

Security configuration and component definition.

## 1. Configuration

- Turn off `csrf`.
- Disable `sessions`.
- Disable `form login`.
- Enable `http basic` authentication.
- Authorize http requests:
    - No auth for `/users` and `open api` endpoints.
    - Require authentication for everything else.

## 2. Components

- `BCryptPasswordEncoder` for password encoding.
- Custom `UserDetails` to hold the user username and id (for easier user access from the database).
- Custom `UserDetailsService` to fetch the user from the database.
- `DaoAuthenticationProvider` with the configured password encoder and user details service.
