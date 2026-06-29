# Item Manager

## Quick Start

### Backend (Spring Boot)
```bash
./mvnw clean install
./mvnw spring-boot:run
```
Starts on `http://localhost:8080`.

### Frontend (Angular)
```bash
npm install
npm start
```
Starts on `http://localhost:4200`.

## Access Points

- **Web UI**: http://localhost:4200
- **Swagger Docs**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console
  - URL: `jdbc:h2:mem:itemdb`
  - User: `sa`
  - Password: *(Leave blank)*

## Tech Stack

**Backend:**
- Java 21
- Spring Boot 4.1.0
- Springdoc OpenAPI (Swagger UI) 2.6.0
- H2 Database (In-Memory)
- JUnit 6.0.3
- Maven Wrapper (mvnw)

**Frontend:**
- Angular 22.0.0
- TypeScript 6.0.2
- Build System: Angular CLI (esbuild + Vite)
- UI: Custom CSS Glassmorphism

## Key Architecture

- **Pagination & Search**: Delegated to the backend to efficiently handle 1,000+ records.
- **Data Seeding**: Automated startup script reads `items.csv` to populate the database.
- **Frontend**: Uses Angular Standalone Components.

## AI Transcripts

Full history of AI usage and decisions is located in:
[docs/plans/ai-session-log.md](./docs/plans/ai-session-log.md)