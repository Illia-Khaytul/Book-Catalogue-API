# Book Catalogue API

This is a learning project that demonstrates building a REST API with Spring Boot, including a basic user authentication, layered architecture and realistic testing.

It showcases:
- Spring Data JPA and Specifications for filtering and pagination
- Spring Security with HTTP Basic and BCrypt for password encoding
- Flyway for database migrations
- SpringDoc for API documentation
- Docker with Testcontainers for realistic testing

## 1. Overview 

This API allows to create, read, update and delete books (crud operations) through the exposed endpoints.

It also implements basic username-password security (HTTP Basic) and allows for user registration, password change and deletion.

## 2. Tech Stack

- Spring Boot 4.1
- Spring Data JPA
- Spring Web MVC
- Spring Security
- PostgreSQL 18
- Flyway
- SpringDoc
- Lombok
- Spring Docker Compose
- Docker
- JUnit 5
- Mockito
- Spring Boot Test
- Testcontainers

## 3. Prerequisites

- Java 21+
- Maven 3.9.19 (or higher)
- Docker Engine
- PostgreSQL 18 (for production)

## 4. Architecture

This project utilizes a layered architecture due to its simplicity.

User request -> Controller -> Service -> Repository -> Database

## 5. Project Structure

The project is structured by domain, where each folder contains the classes related by meaning.
1. `book` contains all the classes responsible for operating with books.
2. `user` contains all the classes responsible for operating with users.
3. `security` contains the security configuration and services.
4. `exception` contains the custom exceptions and handling.
5. `common` contains small classes and utilities used in multiple places.
6. `openapi` contains the openapi configuration.

## 6. Getting Started

**Repository:**

First clone the repository to your machine.

```cmd
git clone https://github.com/Illia-Khaytul/Book-Catalogue-API.git
```

or with ssh

```cmd
git clone git@github.com:Illia-Khaytul/Book-Catalogue-API.git
```

### Run for Development

**Docker:**

This project uses Docker containers for development databases and Spring Docker Compose for automatic container management.

First create a `.env` file at the project root with the necessary environment variables for the [compose]{compose.yaml} file.

```text
DEV_DATABASE_NAME=your_db_name
DEV_DATABASE_USERNAME=your_db_username
DEV_DATABASE_PASSWORD=your_db_password
```

Use this [dev example](.env.dev.example) file as reference.

Make sure your machine does not have environment variables with the same name as the ones specified in the `.env` file, or the compose file will use those instead.

Then make sure the port used by docker for the database is actually free.

```cmd
netstat -ano | findstr :5433
```

Port 5433 is the port configured in the [compose]{compose.yaml} file that the database container will use. 

If it's free the command will return nothing. 
If it's occupied the command will return the information of the network connection utilizing it. 
In that case choose a free port and modify the left number (e.g. in `ports: - "5433:5432"` change the 5433) of the `services:postgres:ports:` parameter in the [compose](compose.yaml) file to that port.

Finally make sure Docker Engine is running.

```cmd
docker ps
```

This command shows the list of containers and their status if Docker is running and an ERROR message if not.

The container is automatically created, started and stopped by Spring Docker Compose based on the [compose](compose.yaml) file.

**Run the application:**

Finally start the application.

```cmd
mvn spring-boot:run
```

The default profile is `dev`, so no command line arguments or property changes are required.

Now the application is available at `http://localhost:8080/api/v1`.

### Run for Production

This project does not use docker containers for the production database, so it must be configured separately.

**Environment variables:**

Configure the required environmental variables used by the production database.

```text
PROD_DATABASE_URL
PROD_DATABASE_USERNAME
PROD_DATABASE_PASSWORD
```

Use this [prod example](.env.prod.example) file as reference for the right format.

Restart the IDE so it picks up the variables.

**Run the application:**

Select the `prod` profile in the [properties](src/main/resources/application.yaml) file.

```yaml
spring:
    profiles:
        active: prod
```

Or as a command line argument during startup.

```cmd
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Finally start the application.

```cmd
mvn spring-boot:run
```

Now the application is available at `http://localhost:8080/api/v1`.

## 7. API Documentation

The API documentation is automatically generated with SpringDoc.

It is available at `http://localhost:8080/api/v1/swagger-ui/index.html` for the `dev` profile.

## 8. Security

The application uses a basic username-password authentication method (http basic).
All endpoints for book operation as well as user password change and deletion require it.

To access the endpoints it is required to create a new user with a unique username (see `create` operation in the API documentation).
Then use your username and password to authenticate successfully.

## 9. Run Tests

Features unit tests for service classes and components, slice tests for controllers and repositories, and integration tests for component interaction and end to end testing.

Utilizes Testcontainers for test databases.

To run the tests first make sure the Docker Engine is running. Use the `docker ps` command to verify.

Then run the tests.

```cmd
mvn verify
```

or just the unit tests

```cmd
mvn test
```

## 10. Design Documentation

The design documentation is located at the [docs](/docs/1-overview.md) folder.

## 11. Known Limitations

This is a learning project and thus has several limitations and weaknesses preventing it from being production grade.

The current API allows anyone to create a user and start editing the books. Normally this would be a security risk, but in this case it was an intentional decision. This project was kept intentionally simple for learning purposes while still showcasing the implementation of different technologies such as spring security.

Password validation is also intentionally weak. For the small scope of this application it was not necessary to add complex password validation (Passay) aside from the current bean validation present in the dtos.

Future updates to this project may include book ownership, roles and advanced password validation to enhance security.