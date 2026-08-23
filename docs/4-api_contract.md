# API Contract

Endpoint and response status definition.

Every endpoint has a base path `/api/v1` and may throw with an error response `500 Internal Server Error` in case of an unexpected exception.

## Book endpoints

1. Create book
2. Update book
3. Get book
4. Get books
5. Delete book

### Create book

**POST** `/books`

**Receives:**
- Body: `create request`
    - String `title`: required, size 1 to 100
    - String `description`: optional, size 0 to 1000
    - String `author`: required, size 1 to 50
    - Integer `pages`: optional, positive or 0
    - LocalDate `releaseDate`: optional, past or present

**Returns:**
- Status: 201 Created
- Header: Location: `/api/v1/books/{bookId}`
- Body: `book response`
    - Long `id`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`

**Error Responses:**
- 400 Bad Request: request validation failed
- 401 Unauthorized: invalid authentication
- 409 Conflict: `duplicate exception`


### Update book

**PATCH** `/books/{bookId}`

**Receives:** 
- Path variable: long `bookId`
- Body: `update request`: all fields optional
    - String `title`: size 1 to 100
    - String `description`: size 0 to 1000
    - String `author`: size 1 to 50
    - Integer `pages`: positive or 0
    - LocalDate `releaseDate`: past or present

**Returns:**
- Status: 200 OK
- Body: `book response`
    - Long `id`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`

**Error Responses:**
- 400 Bad Request: request validation failed
- 401 Unauthorized: invalid authentication
- 404 Not Found: `not found exception`
- 409 Conflict: `duplicate exception` or concurrent modification


### Get book

**GET** `/books/{bookId}`

**Receives:** 
- Path variable: long `bookId`

**Returns:**
- Status: 200 OK
- Body: `book response`
    - Long `id`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`

**Error Responses:**
- 400 Bad Request: request validation failed
- 401 Unauthorized: invalid authentication
- 404 Not Found: `not found exception`


### Get books

**GET** `/books`

**Receives:**
- Query parameters: Pageable `pagination`: default page 0, size 20, sorted by id, descending
- Query parameters: `filtering`: all fields optional
    - String `titleContains`: size 1 to 100
    - String `authorName`: size 1 to 50
    - Integer `minPages`: positive or 0
    - Integer `maxPages`: positive or 0
    - LocalDate `releasedBefore`: any date
    - LocalDate `releasedAfter`: past or present

**Returns:**
- Status: 200 OK
- Body: `paginated response`
    - int `page`
    - int `totalPages`
    - int `pageSize`
    - long `totalElements`
    - List<> `content`: `book response`

**Error Responses:**
- 400 Bad Request: request validation failed
- 401 Unauthorized: invalid authentication


### Delete book

**DELETE** `/books/{bookId}`

**Receives:** 
- Path Variable: long `bookId`

**Returns:**
- Status: 204 No Content

**Error Responses:**
- 400 Bad Request: request validation failed
- 401 Unauthorized: invalid authentication
- 404 Not Found: `not found exception`


## User endpoints

1. Create
2. Change password
3. Delete user

### Create

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
- 404 Not Found: `not found exception` (user was authenticated but doesn't exist in database, rare)


### Delete user

**DELETE** `/users`

**Receives:** nothing

**Returns:**
- Status: 204 No Content

**Error Responses:**
- 401 Unauthorized: invalid authentication
