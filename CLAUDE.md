# Ecommerce Backend - Project Status

## Overview
Spring Boot ecommerce backend migrating from local development to cloud deployment.

## Deployment Target
**Railway** (https://railway.app)

## Current Status: Ready for Deployment

### Completed ✅

1. **Dependencies Updated (pom.xml)**
   - Removed MySQL connector
   - Added PostgreSQL driver
   - Added Cloudinary SDK (v1.36.0)
   - Added Spring Boot Actuator (health checks)

2. **Storage Abstraction**
   - Created `ImageStorageService` interface
   - Updated `FileStorageService` to implement interface (local storage)
   - Created `CloudinaryStorageService` (cloud storage)
   - Created `CloudinaryConfig` for Cloudinary bean
   - Updated `ProductService` to use interface
   - Made `WebConfig` conditional (local storage only)

3. **Configuration Externalized**
   - `application.properties` - uses environment variables
   - `application-dev.properties` - local PostgreSQL (port 5433) + Cloudinary
   - `application-prod.properties` - production config for Railway

4. **Docker**
   - Created multi-stage `Dockerfile`

5. **GitIgnore**
   - Added `.env` files
   - Added `application-dev.properties`
   - Added `application-prod.properties`

6. **Local Testing**
   - PostgreSQL running on Docker (port 5433)
   - Cloudinary integration working

---

## TODO: Deploy to Railway 🚀

### Step 1: Create Railway Account
- Go to https://railway.app
- Sign up with GitHub

### Step 2: Create Neon PostgreSQL Database
- Go to https://neon.tech
- Create free PostgreSQL database
- Copy the connection string

### Step 3: Deploy on Railway
1. Create new project from GitHub repo
2. Add environment variables:

| Variable | Value |
|----------|-------|
| `PORT` | (auto-set by Railway) |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | `jdbc:postgresql://...` (from Neon) |
| `JWT_SECRET_KEY` | (generate secure 32+ char string) |
| `CLOUDINARY_CLOUD_NAME` | `dzgp1fxou` |
| `CLOUDINARY_API_KEY` | (from Cloudinary dashboard) |
| `CLOUDINARY_API_SECRET` | (from Cloudinary dashboard) |

### Step 4: Verify Deployment
- Check health endpoint: `https://your-app.railway.app/actuator/health`
- Test API endpoints

---

## Local Development Commands

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
├── service/
│   ├── ImageStorageService.java   # Interface
│   ├── FileStorageService.java    # Local storage impl
│   ├── CloudinaryStorageService.java  # Cloud storage impl
│   └── ProductService.java        # Uses ImageStorageService
└── ...

src/main/resources/
├── application.properties         # Base config with env vars
├── application-dev.properties     # Local dev (gitignored)
└── application-prod.properties    # Production (gitignored)
```

---

## Notes
- Storage type controlled by `storage.type` property (`local` or `cloudinary`)
- Spring conditionally loads the correct implementation
- Local dev uses port 5433 for PostgreSQL (5432 was occupied)

---

## AI Assistant
This project was developed with assistance from Claude Opus 4.5 (Anthropic).
