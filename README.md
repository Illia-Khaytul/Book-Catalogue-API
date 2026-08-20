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
- Spring OpenApi
- Spring DotEnv
- PostgreSQL
- Testcontainers

## 3. Prerequisites

- Java 21+
- Maven 3.9.19 (or higher)
- PostgreSQL database (e.g. pgAdmin 4)

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
DEV_DATABASE_USERNAME=your_username
DEV_DATABASE_PASSWORD=your_password
DEV_DATABASE_URL=//your_ip:your_port/your_database

PROD_DATABASE_USERNAME=your_username
PROD_DATABASE_PASSWORD=your_password
PROD_DATABASE_URL=//your_ip:your_port/your_database
```

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

Now the application is available at `http://localhost:8080`

## 5. API Documentation

The API documentation is automatically generated with Spring OpenApi.

It is available at `http://localhost:8080/swagger-ui/index.html` on the `dev` profile.

## 6. Security

The application uses a basic username-password authentication method (http basic).
All endpoints for book operation as well as user password change and deletion require it.

To access the endpoints it is required to create a new user with a unique username (see `create` operation in the api documentation).
Then use your username and password to authenticate successfully.

## 7. Project Structure

The project is structured by domain, where each folder contains the classes related by meaning.
1. `book` contains all the classes responsible for operating with books.
2. `user` contains all the classes responsible for operating with users.
3. `security` contains the security configuration and services.
4. `exception` contains the custom exceptions and handling.
5. `common` contains small classes and utilities used in multiple places.
6. `openapi` contains the openapi configuration.
