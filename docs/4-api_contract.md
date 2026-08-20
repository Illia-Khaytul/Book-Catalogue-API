# API Contract

Endpoint and response status definition.

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
    - String `title`: required, size 1 to 50
    - String `description`: optional, size 0 tp 1000
    - String `author`: required, size 1 to 50
    - Integer `pages`: optional, positive or 0
    - LocalDate `releaseDate`: optional, past or present

**Returns:**
- Status: 201 Created
- Header: Location: `/books/{userId}`
- Body: `book response`
    - Long `bookId`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`


### Update book

**PATCH** `/books/{bookId}`

**Receives:** 
- Path variable: long `bookId`
- Body: `update request`: all fields optional
    - String `title`: size 1 to 50
    - String `description`: size 0 tp 1000
    - String `author`: size 1 to 50
    - Integer `pages`: positive or 0
    - LocalDate `releaseDate`: past or present

**Returns:**
- Status: 200 OK
- Body: `book response`
    - Long `bookId`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`


### Getks book

**GET** `/books/{bookId}`

**Receives:** 
- Path variable: long `bookId`

**Returns:**
- Status: 200 OK
- Body: `book response`
    - Long `bookId`
    - String `title`
    - String `description`
    - String `author`
    - Integer `pages`
    - LocalDate `releaseDate`


### Get books

**GET** `/books`

**Receives:**
- Query parameters: Pageable `pagination`: default page 0, size 20, sorted by id, descending
- Query parameters: `filtering`: all fields optional
    - String `titleContains`: size 1 to 50
    - String `authorName`: size 1 to 50
    - Integer `minPages`: positive or 0
    - Integer `maxPages`: positive or 0
    - LocalDate `releasedBefore`
    - LocalDate `releasedAfter`: past or present

**Returns:**
- Status: 200 OK
- Body: `paginated response`
    - int `page`
    - int `totalPages`
    - int `pageSize`
    - long `totalElements`
    - List<> `content`: `book response`


### Delete book

**DELETE** `/books/{bookId}`

**Receives:** 
- Path Variable: long `bookId`

**Returns:**
- Status: 204 No Content


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


### Change password

**PATCH** `/users/password`

**Receives:**
- Body: `password change request`
    - String `newPassword`: required, size 6 to 50

**Returns:**
- Status: 200 OK


### Delete user

**DELETE** `/users`

**Receives:** nothing

**Returns:**
- Status: 204 No Content
