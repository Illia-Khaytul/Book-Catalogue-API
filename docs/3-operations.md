# Operations

Operation definition and received and returned data design.

## 1. Book operations

1. Create book
2. Update book
3. Get book
4. Get books
5. Delete book

### 1.1. Create book

Creates a new book with provided data and persists it to the database.

**Receives:** `create request`

**Steps:**
1. Checks if a book with this title and author already exists. Throws `duplicate exception`.
2. Creates a new book with the provided data.

**Returns:** `book response`

Notes:
- Requires a transaction so that the duplicate check reads only committed data.


### 1.2. Update book

Fetches an existing book by provided id and updates its fields with the provided data.

**Receives:** long `bookId` and `update request`

**Steps:**
1. Fetch book by id. If not found throws `not found exception`.
2. Check if a book with the new title and author already exists. Throws `duplicate exception`.
3. Updates book with provided data.

**Returns:** `book response`

Notes:
- Partial update, if the provided field is null, do not update.
- Transaction required to catch concurrent modification.
- Allows 3 retries in case of concurrent modification. The update is partial, the provided data is not invalid on optimistic locking. Used to load fresh data from the database for a correct partial update instead of failing.


### 1.3. Get book

Returns an existing book by provided id.

**Receives:** long `bookId`

**Steps:**
1. Fetch book by id. If not found throws `not found exception`.

**Returns:** `book response`

Notes:
- Read only transaction.


### 1.4. Get books

Returns a page of existing books by provided pagination and filters.

**Receives:** Pageable `pagination` and `filtering`

**Steps:**
1. Build specification with provided filtering.
2. Fetch books with pagination and specification.

**Returns:** `paginated response` for `book response`

Notes:
- Read only transaction.


### 1.5. Delete book

Deletes an existing book by provided id.

**Receives:** long `bookId`

**Steps:**
1. Check if the book exists. Throws `not found exception`.
2. Delete book by id.

**Returns:** nothing

Notes:
- Deletes the book directly without loading it into the persistence context. Requires transaction.
- No need to check if the book exists (in case it gets deleted mid operation) because the operation deletes it anyways.


## 2. User operations

1. Create user
2. Change user password
3. Delete user

### 2.1. Create user

Creates a new user with the provided data and persists it to the database.

**Receives:** `create request`

**Steps:**
1. Check if user with provided username already exists. Throws `duplicate exception`.
2. Encode password.
3. Create new user with provided data.

**Returns:** nothing

Notes:
- Requires transaction to read only committed data from the database.


### 2.2. Change user password

Changes the password of the accessing user.

**Receives:** `password change request`

**Steps:**
1. Fetches authenticated user. If not present (somehow), throws `not found exception`.
3. Check if new password is different from old password. Throws `invalid password exception`.
2. Check if provided old password matches the current user password. Throws `invalid password exception`.
4. Encodes new password.
5. Updates user password to new one.

**Returns:** nothing

Notes:
- Requires transaction to read only committed data from the database.


### 2.3. Delete user

Deletes the accessing user.

**Receives:** nothing

**Steps:**
1. Fetches authenticated user id.
2. Deletes user by id.

**Returns:** nothing

Notes:
- Deletes user directly without loading it into the persistence context. Requires transaction.
- No need to check if the user exists (in case the user gets deleted mid operation) because the operation deletes it anyways.