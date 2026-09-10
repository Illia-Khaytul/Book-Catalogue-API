# API Contract

Endpoint and response status definition.

Every endpoint has a base path `/api/v1` and may throw with an error response `500 Internal Server Error` in case of an unexpected exception.

## User endpoints

1. Create user
2. Change password
3. Delete user

### Create user

**POST** `/users`

**Receives:**
- Body: `create request`
    - String `username`: required, size 5 to 50
    - String `password`: required, size 6 to 50

**Returns:**
- Status: 201 Created

**Error Responses:**
- 400 Bad Request: request validation failed
- 409 Conflict: `duplicate exception`


### Change password

**PATCH** `/users/password`

**Receives:**
- Body: `password change request`
    - String `oldPassword`: required, size 6 to 50
    - String `newPassword`: required, size 6 to 50

**Returns:**
- Status: 200 OK

**Error Responses:**
- 400 Bad Request: request validation failed or invalid password
- 401 Unauthorized: invalid authentication
- 404 Not Found: `not found exception` (user was authenticated but doesn't exist in database)


### Delete user

**DELETE** `/users`

**Receives:** nothing

**Returns:**
- Status: 204 No Content

**Error Responses:**
- 401 Unauthorized: invalid authentication
