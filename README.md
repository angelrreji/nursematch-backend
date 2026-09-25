# NurseMatch Backend

REST API for **NurseMatch**, a platform that matches nurse practitioner (NP) students with clinical providers (preceptors) for their clinical rotations.

Students describe the rotation they need (specialty, location, dates, hours). NurseMatch finds providers who practise that specialty, are accepting students, and have availability covering those dates, and ranks them by distance. Students then create a match, pay a deposit to unlock provider details, and an administrator confirms and completes the placement.

---

## Key Features

- **Role-based accounts**: NP students, providers, and administrators, with stateless JWT authentication.
- **Student profiles and rotation requests**: each request is geocoded from its address.
- **Provider profiles**: specialties, license state, geocoded practice location, availability slots, and an accepting/not-accepting toggle.
- **Match discovery**: filters by specialty, state, and availability, then sorts by great-circle (Haversine) distance, with pagination.
- **Match lifecycle**: `PENDING → DEPOSIT_PAID → CONFIRMED → COMPLETED` (or `CANCELLED`).
- **Paywalled match details**: provider details are returned only after the deposit is paid.
- **Admin console API**: list all matches and confirm, complete, or cancel them.
- **Consistent JSON error responses** from a global exception handler.

## Architecture

A layered Spring Boot application organised **by feature**. Each feature package contains its own `controller → service → repository → model` layers plus request/response DTOs.

```
HTTP request
   │
   ▼
JwtAuthFilter ──► SecurityConfig (route → role rules)
   │
   ▼
Controller  (REST, validation via @Valid, DTOs in/out)
   │
   ▼
Service     (business rules, orchestration)
   │         └──► GeocodingClient ──► OpenStreetMap Nominatim
   ▼
Repository  (Spring Data MongoDB)
   │
   ▼
MongoDB
```

Rotation requests are embedded in the student profile document, and availability slots are embedded in the provider profile document.

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web, Validation, Security) |
| Persistence | MongoDB via Spring Data MongoDB |
| Auth | Spring Security, JWT (jjwt 0.11.5, HS256), BCrypt password hashing |
| Geocoding | OpenStreetMap Nominatim via Spring `RestClient` |
| Boilerplate | Lombok |
| Build | Maven (wrapper included) |
| Testing | JUnit 5, Spring Boot Test, Spring Security Test |

## Main Modules

| Package | Responsibility |
|---|---|
| `auth` | Registration, login, `UserDetailsService` implementation |
| `security` | JWT creation/validation (`JwtUtil`) and the request filter (`JwtAuthFilter`) |
| `config` | Security filter chain, route authorization, CORS, password encoder |
| `user` | `User` document, `Role` and `PaymentStatus` enums |
| `student` | Student profile management |
| `rotation` | Submitting and listing rotation requests (with geocoding) |
| `provider` | Provider profile, availability slots, accepting-students toggle |
| `match` | Discovery algorithm and match creation/listing/details |
| `payment` | Deposit payment that unlocks match details |
| `admin` | Administrative match management |
| `geo` | Nominatim geocoding client and Haversine distance utility |
| `exception` | Custom exceptions and global error handler |

## Authentication & Authorization

- `POST /api/auth/register` creates a user with a BCrypt-hashed password and a role (`NP_STUDENT`, `PROVIDER`, or `ADMIN`).
- `POST /api/auth/login` returns a signed JWT (HS256) containing the user's email (subject) and role. The default lifetime is 24 hours.
- Clients send the token on every request as `Authorization: Bearer <token>`.
- The API is stateless (no HTTP session) and CSRF protection is disabled, as is usual for token-based APIs.
- Route access by role:

| Path | Access |
|---|---|
| `/api/auth/**`, `/api/test/public` | Public |
| `/api/student/**`, `/api/rotation/**`, `/api/match/**`, `/api/payment/**` | `NP_STUDENT` |
| `/api/provider/**` | `PROVIDER` |
| `/api/admin/**` | `ADMIN` |
| Everything else | Any authenticated user |

CORS currently allows the local frontend origin `http://localhost:5173`.

## Database

MongoDB, with these collections:

| Collection | Document | Notes |
|---|---|---|
| `users` | `User` | name, unique email, password hash, role, profile/payment flags |
| `student_profiles` | `StudentProfile` | one per student user; embeds `rotationRequests[]` |
| `provider_profiles` | `ProviderProfile` | one per provider user; specialties, location (lat/lng), embeds `availability[]` |
| `matches` | `Match` | student ↔ provider link, dates, status, deposit and paperwork flags |

Indexes are declared on the entities: unique `email` and `userId`, a compound index on `{specialties, state, acceptingStudents}`, and indexes on `{studentId, status}` and `{providerId, status}`. Spring Data MongoDB does **not** create declared indexes automatically by default, so create them in the database, or set `spring.data.mongodb.auto-index-creation=true` for local development.

> The default database name is `matchnp`, kept from before the project was renamed so existing local data remains accessible. Set `MONGODB_URI` / `MONGODB_DATABASE` to use a different database.

## API Overview

All endpoints are prefixed with `/api`. Request and response bodies are JSON.

**Auth**
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a user (`name`, `email`, `password`, `role`) |
| POST | `/auth/login` | Log in, returns `{ token, role, email }` |

**Student** (`NP_STUDENT`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/student/profile` | Create student profile |
| GET | `/student/profile` | Get own profile |
| POST | `/rotation/request` | Submit a rotation request (geocoded) |
| GET | `/rotation/requests` | List own rotation requests |
| GET | `/match/discover?requestId=&page=0&size=10` | Find matching providers ranked by distance |
| POST | `/match/create` | Create a match with a provider |
| GET | `/match/my-matches` | List own matches |
| GET | `/match/{matchId}/details` | Match details (returns `402 Payment Required` until the deposit is paid) |
| POST | `/payment/deposit` | Pay the deposit for a match |

**Provider** (`PROVIDER`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/provider/profile` | Create provider profile (geocoded) |
| GET | `/provider/profile` | Get own profile |
| POST | `/provider/availability` | Add an availability slot |
| PATCH | `/provider/accepting?accepting=true` | Toggle whether accepting students |

**Admin** (`ADMIN`)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/matches` | List all matches |
| PATCH | `/admin/matches/{id}/confirm` | Confirm a match (deposit must be paid) |
| PATCH | `/admin/matches/{id}/complete` | Complete a confirmed match |
| PATCH | `/admin/matches/{id}/cancel` | Cancel a match |

**Utility**
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/geo/geocode?address=&city=&state=` | Authenticated | Geocode an address |
| GET | `/test/public` | Public | Connectivity check |
| GET | `/test/secure` | Authenticated | Token check |

Errors use a common shape:

```json
{ "timestamp": "2026-01-01T12:00:00", "status": 404, "message": "Match not found" }
```

## Project Structure

```
.
├── .env.example                  # Environment variable template (copy to .env)
├── .mvn/wrapper/                 # Maven wrapper config
├── mvnw, mvnw.cmd                # Maven wrapper scripts
├── pom.xml
└── src
    ├── main
    │   ├── java/com/nursematch
    │   │   ├── NurseMatchBackendApplication.java
    │   │   ├── admin/            # controller, dto, service
    │   │   ├── auth/             # controller, dto, service
    │   │   ├── config/           # SecurityConfig
    │   │   ├── exception/        # GlobalExceptionHandler, custom exceptions
    │   │   ├── geo/              # GeocodingClient, GeoUtils, controller, dto
    │   │   ├── match/            # controller, dto, model, repository, service
    │   │   ├── payment/          # controller, dto, service
    │   │   ├── provider/         # controller, dto, model, repository, service
    │   │   ├── rotation/         # controller, dto, model, service
    │   │   ├── security/         # JwtUtil, JwtAuthFilter
    │   │   ├── student/          # controller, dto, model, repository, service
    │   │   └── user/             # model, repository
    │   └── resources
    │       └── application.properties
    └── test
        ├── java/com/nursematch
        │   └── NurseMatchBackendApplicationTests.java
        └── resources/config
            └── application.properties   # test-only overrides (non-secret JWT key)
```

## Environment Configuration

Configuration is read from environment variables. For local development, copy `.env.example` to `.env`. The application loads `.env` from the working directory automatically, and it is git-ignored.

| Variable | Required | Default | Description |
|---|---|---|---|
| `JWT_SECRET` | **Yes** | none | Hex-encoded HMAC key of at least 32 bytes (64 hex chars). Generate with `openssl rand -hex 32`. |
| `JWT_EXPIRATION_MS` | No | `86400000` | Token lifetime in milliseconds (24 h) |
| `MONGODB_URI` | No | `mongodb://localhost:27017/matchnp` | MongoDB connection string |
| `MONGODB_DATABASE` | No | `matchnp` | Database name |
| `SERVER_PORT` | No | `8080` | HTTP port |
| `GEOCODING_BASE_URL` | No | `https://nominatim.openstreetmap.org/search` | Nominatim search endpoint |
| `GEOCODING_USER_AGENT` | No | `NurseMatch-App/1.0` | User-Agent sent to Nominatim. Include a contact email, as the [usage policy](https://operations.osmfoundation.org/policies/nominatim/) requires. |

The application will not start without `JWT_SECRET`. Never commit `.env` or real credentials.

## Local Setup

**Prerequisites**
- JDK 21
- MongoDB 6+ running locally (or a connection string to a remote instance)
- Internet access for Maven dependencies and Nominatim geocoding

**Steps**
```bash
git clone <repository-url>
cd nursematch-backend

cp .env.example .env
# Edit .env: set JWT_SECRET (openssl rand -hex 32) and your contact email in GEOCODING_USER_AGENT
```

To start MongoDB quickly with Docker:
```bash
docker run -d --name nursematch-mongo -p 27017:27017 mongo:7
```

## Running the Application

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`. To check that it is running:
```bash
curl http://localhost:8080/api/test/public
```

To build a runnable JAR:
```bash
./mvnw clean package
java -jar target/nursematch-backend-0.0.1-SNAPSHOT.jar
```

## Testing

```bash
./mvnw test
```

The current suite is a Spring context-load smoke test. It uses a non-secret JWT key from `src/test/resources/config/application.properties`, and it does not need a running MongoDB instance.

## Project Status & Known Limitations

This is an early-stage project. Before production use, note that:

- **Payments are simulated.** `POST /api/payment/deposit` marks a deposit as paid without integrating a payment provider.
- **Role is chosen at registration.** Any caller can currently register with any role, including `ADMIN`.
- **Index creation.** Declared MongoDB indexes, including unique email, must be created explicitly (see [Database](#database)).
- **CORS** is hard-coded to `http://localhost:5173`.
- **Test coverage** is limited to a context-load test.
