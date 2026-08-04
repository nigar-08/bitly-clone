# Scalable URL Shortener

A full-stack URL-shortening service built with Java 21, Spring Boot, React, and MySQL. The backend focuses on the correctness concerns behind a redirect-heavy system: collision-safe key allocation, atomic counters, indexed analytics, ownership boundaries, and reproducible schema changes.

## Engineering Highlights

- Cryptographically strong Base62 short-code generation with collision detection and retry
- Database uniqueness constraint as the final concurrency-safe collision guard
- Atomic click-count increments to avoid lost updates during concurrent redirects
- Transactional click-event recording for analytics consistency
- Owner-scoped analytics so authenticated users cannot inspect another user's links
- HTTP/HTTPS-only URL validation and structured API errors
- Indexed redirect, user-history, and time-range analytics access paths
- Flyway-managed MySQL schema migrations
- JWT authentication with BCrypt password hashing and duplicate-account protection
- Hermetic H2 tests plus GitHub Actions verification
- Java 21 multi-stage, non-root Docker image

## Run Locally

Start MySQL and the backend:

```bash
docker compose up --build
```

The API is available at `http://localhost:8080` and MySQL is exposed at `localhost:3307` for local inspection.

Start the frontend separately:

```bash
cd frontend
npm ci
npm run dev
```

## Verify

```bash
cd backend
./mvnw verify

cd ../frontend
npm ci
npm run build
```

Backend tests use an isolated in-memory database and require neither MySQL nor deployment secrets.

## Core API Flow

1. A user registers and authenticates to receive a JWT.
2. `POST /api/urls/shorten` validates the destination and allocates a unique Base62 code.
3. `GET /{shortUrl}` resolves the destination, atomically increments the counter, records the click, and returns an HTTP 302 response.
4. Authenticated analytics endpoints aggregate click events within user-owned links and requested time ranges.

## Scale-Out Direction

For substantially higher redirect volume, the next step would be a distributed Redis cache for code-to-destination resolution and asynchronous analytics ingestion through a durable event stream. Those components are intentionally not claimed in the current implementation.
# Bitly Clone

A full-stack URL shortener built with Spring Boot, MySQL, JWT authentication, and a React/Vite frontend.

## Tech Stack

- Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA
- Database: MySQL
- Frontend: React, Vite, Tailwind CSS, Material UI
- Auth: JWT

## Project Structure

```text
backend/             Spring Boot REST API
frontend/            React/Vite client
url-shortener-sb/    Older standalone Spring Boot copy
```

For normal development, use `backend/` and `frontend/`.

## Features

- Register and log in users
- Generate short URLs
- Redirect short URLs to original links
- View user-specific shortened URLs
- Track click counts and date-based analytics

## Backend Setup

Create a MySQL database:

```sql
CREATE DATABASE urlshortenerdb;
```

Set these environment variables, or create them in your IntelliJ run configuration:

```bash
DATABASE_URL=jdbc:mysql://localhost:3306/urlshortenerdb
DATABASE_USERNAME=root
DATABASE_PASSWORD=your_mysql_password
DATABASE_DIALECT=org.hibernate.dialect.MySQL8Dialect
JWT_SECRET=replace_with_a_long_secure_secret
JWT_EXPIRATION=86400000
FRONTEND_URL=http://localhost:5173
```

Run the backend:

```bash
cd backend
./mvnw spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

## Frontend Setup

Create or update `frontend/.env`:

```bash
VITE_BACKEND_URL=http://localhost:8080
VITE_REACT_FRONT_END_URL=http://localhost:5173
VITE_REACT_SUBDOMAIN=http://url.localhost:5173
```

Install dependencies and run the frontend:

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on:

```text
http://localhost:5173
```

## API Endpoints

Public endpoints:

```text
POST /api/auth/public/register
POST /api/auth/public/login
GET  /{shortUrl}
```

Authenticated endpoints:

```text
POST /api/urls/shorten
GET  /api/urls/myurls
GET  /api/urls/analytics/{shortUrl}
GET  /api/urls/totalClicks
```

Authenticated requests must include the JWT token returned from login.

## IntelliJ Run Notes

Open the `backend` folder as the Spring Boot project, or select `UrlShortenerApplication` and create a run configuration.

Add the backend environment variables in:

```text
Run > Edit Configurations > Environment variables
```

Then start the backend from IntelliJ and run the frontend separately with `npm run dev`.
