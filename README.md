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
