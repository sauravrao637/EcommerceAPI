# Kotlin eCommerce API

This project is an eCommerce API built in Kotlin using the Ktor framework. It provides various endpoints and features to support an eCommerce platform.

## Features

- User authentication and authorization using JWT (JSON Web Tokens)
- CORS (Cross-Origin Resource Sharing) enabled to allow cross-origin requests
- Serialization and deserialization of JSON using Gson
- Routing and request handling for different API endpoints
- Integration with various controllers for handling business logic

## Getting Started

To get started with the project, follow these steps:

1. Clone the repository: `git clone <repository-url>`
2. Install Kotlin and its dependencies (if not already installed)
3. Build the project: `gradle clean build`
4. Run the project: `gradle run`

Make sure to configure any necessary environment variables, database connections, or other settings as per your requirements.

### Database Configuration

- Create a MySQL database with the following credentials:
  - Host: `localhost`
  - Port: `3306`
  - Database Name: `fcs`
  - Username: `root`
  - Password: `12345678`

- Run the `initial.sql` script provided in the project to set up the initial database schema and data.

## API Endpoints

The API provides various endpoints for different functionalities. You can access the API documentation and test the endpoints using Postman by clicking the button below:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://god.postman.co/run-collection/336b8dcabc8263753f74?action=collection%2Fimport)

Please refer to the source code for detailed request/response structures and additional endpoints.

## Dependencies

The project uses the following dependencies:

- Ktor: Core framework for building web applications in Kotlin
- JWT: Library for handling JSON Web Tokens for user authentication
- Gson: Library for serializing and deserializing JSON objects

Make sure to check the `build.gradle` file for the complete list of dependencies and their versions.
