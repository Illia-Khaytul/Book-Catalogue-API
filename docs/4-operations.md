# Operations

Operation definition and received and returned data design.

## 1. User operations

1. Create user
2. Change user password
3. Delete user

### 1.1. Create user

Creates a new user with the provided data and persists it to the database.

**Receives:** `create request`

**Steps:**
1. Check if user with provided username already exists. Throws `duplicate exception`.
2. Encode password.
3. Create new user with provided data.

**Returns:** nothing


### 1.2. Change user password

Changes the password of the accessing user.

**Receives:** `password change request`

**Steps:**
1. Fetches authenticated user. If not present (somehow), throws `not found exception`.
2. Check if new password is different from old password. Throws `invalid password exception`.
3. Check if provided old password matches the current user password. Throws `invalid password exception`.
4. Encodes new password.
5. Updates user password to new one.

**Returns:** nothing


### 1.3. Delete user

Deletes the accessing user.

**Receives:** nothing

**Steps:**
1. Fetches authenticated user id.
2. Deletes user by id.

**Returns:** nothing

Notes:
- If the user gets deleted between authentication and deletion (user details has the id but user does not exist in the database) the application will throw. 
A normal deleteById method fetches the user before deletion to execute all entity lifecycle hooks (none present in this case). 
Use a modifying delete query to avoid loading the entity or performing additional existence checks.
