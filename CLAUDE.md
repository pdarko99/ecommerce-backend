# Ecommerce Backend - Project Status

## Overview
Spring Boot ecommerce backend deployed to cloud.

## Production Environment

| Service | Provider | URL |
|---------|----------|-----|
| **Backend** | Railway | https://ecommerce-backend-production-bab1.up.railway.app |
| **Database** | Neon | PostgreSQL (Serverless) |
| **Image Storage** | Cloudinary | Cloud-based |

### Health Check
```bash
curl https://ecommerce-backend-production-bab1.up.railway.app/actuator/health
```

---

## Deployment Status: Complete ✅

### Completed

1. **Dependencies (pom.xml)**
   - PostgreSQL driver
   - Cloudinary SDK (v1.36.0)
   - Spring Boot Actuator (health checks)

2. **Storage Abstraction**
   - `ImageStorageService` interface
   - `FileStorageService` - local storage implementation
   - `CloudinaryStorageService` - cloud storage implementation
   - `CloudinaryConfig` - Cloudinary bean (conditional)
   - `WebConfig` - static file serving (conditional, local only)

3. **Configuration**
   - `application.properties` - base config with env vars
   - `application-dev.properties` - local PostgreSQL (port 5433) + Cloudinary (gitignored)
   - `application-prod.properties` - production config for Railway

4. **Docker**
   - Multi-stage `Dockerfile`

5. **Deployed to Railway**
   - Connected to GitHub repo (auto-deploys on push)
   - Neon PostgreSQL database
   - Cloudinary for image storage

---

## Railway Environment Variables

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://...` (Neon connection string without credentials) |
| `DATABASE_USERNAME` | Neon database username |
| `DATABASE_PASSWORD` | Neon database password |
| `JWT_SECRET_KEY` | Secure 32+ character string |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |

---

## API Endpoints

### Auth
```bash
# Register
curl -X POST https://ecommerce-backend-production-bab1.up.railway.app/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "Test@123!", "confirmPassword": "Test@123!", "firstName": "John", "lastName": "Doe"}'

# Login
curl -X POST https://ecommerce-backend-production-bab1.up.railway.app/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "Test@123!"}'
```

---

## Local Development

```bash
# Start PostgreSQL Docker
docker start postgres-ecommerce

# Or if container doesn't exist:
docker run -d --name postgres-ecommerce -p 5433:5432 \
  -e POSTGRES_DB=ecommerce \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine

# Connect to PostgreSQL
docker exec -it postgres-ecommerce psql -U postgres -d ecommerce

# Run the app
./mvnw spring-boot:run

# Build Docker image
docker build -t ecommerce .
```

---

## File Structure

```
src/main/java/com/ecommerce/ecommerce/
├── config/
│   ├── CloudinaryConfig.java      # Cloudinary bean (conditional)
│   └── WebConfig.java             # Static file serving (conditional)
├── controllers/
│   ├── AuthController.java        # /auth endpoints
│   └── ProductController.java     # /products endpoints
├── service/
│   ├── ImageStorageService.java   # Interface
│   ├── FileStorageService.java    # Local storage impl
│   ├── CloudinaryStorageService.java  # Cloud storage impl
│   ├── AuthService.java           # Authentication logic
│   └── ProductService.java        # Product logic
└── ...

src/main/resources/
├── application.properties         # Base config with env vars
├── application-dev.properties     # Local dev (gitignored)
└── application-prod.properties    # Production config
```

---

## Notes
- Storage type controlled by `storage.type` property (`local` or `cloudinary`)
- Spring conditionally loads the correct implementation
- Local dev uses port 5433 for PostgreSQL (5432 was occupied)
- Railway auto-deploys on push to main branch
- Password requirements: 8+ chars, uppercase, lowercase, number, special character

---

## AI Assistant
This project was developed with assistance from Claude Opus 4.5 (Anthropic).
