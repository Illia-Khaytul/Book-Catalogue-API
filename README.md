# Book Catalogue API

Basic book cataloguing rest api that allows for book management and implements basic security measures.

## 1. Overview

This api allows to create, read, update and delete books (crud operations) through the exposed endpoints.
It also implements basic username-password security (http basic) and allows for user registration, password change and deletion.

## 2. Tech Stack

- Spring Boot 4.1
- Spring Data JPA
- Spring Web MVC
- Spring Security
- SpringDoc
- Spring DotEnv
- Lombok
- PostgreSQL 18
- JUnit 5
- Mockito
- Spring Boot Test
- Testcontainers

## 3. Prerequisites

- Java 21+
- Maven 3.9.19 (or higher)
- PostgreSQL database (e.g. pgAdmin 4)
- Docker Engine (for testcontainers)

## 4. Getting Started

First clone the repository to your machine.

```bash
git clone https://github.com/Illia-Khaytul/Book-Catalogue-API.git
```

or

```bash
git clone git@github.com:Illia-Khaytul/Book-Catalogue-API.git
```

Then configure the required environmental variables used in the application. 
This application uses the Spring DotEnv library to load the environment variable from a `.env` file located at the project root.

```text
DEV_DATABASE_USERNAME=db_username
DEV_DATABASE_PASSWORD=db_password
DEV_DATABASE_URL=//db_ip:db_port/db_name

PROD_DATABASE_USERNAME=db_username
PROD_DATABASE_PASSWORD=db_password
PROD_DATABASE_URL=//db_ip:db_port/db_name
```

The `datasource.url` in the `application.properties` file already contains the `jdbc:postgresql:` prefix and expects just the address of the database itself.

Select your desired profile (dev/prod) in the `application.properties` file.

```text
spring.profiles.active=dev
```

Or as a command line argument during startup.

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

By default the `dev` profile is selected.

Finally start the application.

```bash
mvn spring-boot:run
```

Now the application is available at `http://localhost:8080/api/v1`

## 5. API Documentation

The API documentation is automatically generated with SpringDoc.

It is available at `http://localhost:8080/api/v1/swagger-ui/index.html` on the `dev` profile.

## 6. Security

The application uses a basic username-password authentication method (http basic).
All endpoints for book operation as well as user password change and deletion require it.

To access the endpoints it is required to create a new user with a unique username (see `create` operation in the api documentation).
Then use your username and password to authenticate successfully.

## 7. Run Tests

Features unit tests for service classes and components, slice tests for controllers and repositories, and integration tests for full application testing.

Utilizes Testcontainers for test databases.

To run the tests first make sure the Docker Engine is running. Run the following command to test it:

```bash
docker ps
```

This command shows the list of containers and their status if Docker is running and and ERROR message if not.

Then execute this command to run the tests:

```bash
mvn test
```

## 8. Project Structure

The project is structured by domain, where each folder contains the classes related by meaning.
1. `book` contains all the classes responsible for operating with books.
2. `user` contains all the classes responsible for operating with users.
3. `security` contains the security configuration and services.
4. `exception` contains the custom exceptions and handling.
5. `common` contains small classes and utilities used in multiple places.
6. `openapi` contains the openapi configuration.

## 9. Design Documentation

The design documentation is located at the [/docs](/docs/1-overview.md) folder.

## 10. Known Limitations

The current api allows anyone to create a user and start editing the books. Normally this would be a security risk, but in this case it was an intentional decision. This project was kept intentionally simple for learning purposes while still showcasing the implementation of different technologies such as spring security.

Password validation is also intentionally weak. For the small scope of this application it was not necessary to add complex password validation (Passay) aside from the current bean validation present in the dtos.

Future updates to this project may include book ownership, roles and advanced password validation to enhance security.