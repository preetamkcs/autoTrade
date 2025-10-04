# Zerodha Trading Application

This is a Spring Boot application for automating trading using the Zerodha Kite Connect API.

## Building the Application

To build the application, you will need Java 21 and Maven. Run the following command from the root of the project:

```bash
./mvnw clean package
```

This will create a standalone executable JAR file in the `target/` directory.

## Running the Application

The application can be run in two modes: with a PostgreSQL database (default) or with an in-memory H2 database (for local development and testing).

### With PostgreSQL (Default Profile)

By default, the application is configured to connect to a PostgreSQL database. Before running, make sure you have a PostgreSQL instance running and have configured the following properties in `src/main/resources/application.properties`, or have set them as environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `KITE_API_KEY`
- `KITE_API_SECRET`
- `KITE_REQUEST_TOKEN`

To run the application, use the following command:

```bash
java -jar target/trading-app-0.0.1-SNAPSHOT.jar
```

### With In-Memory H2 Database (`in-memory` profile)

For local development and testing, you can run the application with an in-memory H2 database. This requires no external database setup.

To run the application with the `in-memory` profile, use the following command:

```bash
java -jar -Dspring.profiles.active=in-memory target/trading-app-0.0.1-SNAPSHOT.jar
```

#### Accessing the H2 Console

When the `in-memory` profile is active, you can access the H2 database console in your browser at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use the following settings to connect:
- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:mem:trading_db`
- **User Name:** `sa`
- **Password:** `password`

## Docker

A `Dockerfile` is included for containerizing the application. You can build the Docker image with the following command:

```bash
docker build -t trading-app .
```

To run the Docker container, you will need to provide the necessary environment variables for the database and Kite Connect API credentials.