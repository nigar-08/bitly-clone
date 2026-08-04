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
