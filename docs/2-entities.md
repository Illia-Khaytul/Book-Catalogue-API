# Entities

Entity design.

All unique constraints are prefixed with "unique_"

## 1. Book

**Fields:**
- Long `id`: identity
- String `title`: required
- String `description`: lob, optional
- String `author`: required
- Integer `pages`: optional
- LocalDate `releaseDate`: optional
- Integer `version`: versioning

Notes:
- Constraint for only one title per author.
- Versioning field to catch concurrent modification.

## 2. User

**Fields:**
- Long `id`: identity
- String `username`: required, unique
- String `password`: required

Notes:
- Username must be unique.
- Password is hashed.
- No versioning since users are barely modified (password change can be performed sequentially for security).
