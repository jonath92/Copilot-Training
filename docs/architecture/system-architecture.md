# System Architecture

The Task Management Application follows a three-tier architecture pattern, separating concerns across presentation, business logic, and data persistence layers.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         Frontend Layer                       │
│                    (Vite + TypeScript)                       │
│                   Port: 5173 (dev mode)                      │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
                         │
┌────────────────────────▼────────────────────────────────────┐
│                      Backend Layer                           │
│                     (Go HTTP Server)                         │
│                        Port: 8080                            │
└────────────────────────┬────────────────────────────────────┘
                         │ SQL
                         │
┌────────────────────────▼────────────────────────────────────┐
│                     Database Layer                           │
│                   (PostgreSQL 15+)                           │
│                        Port: 5432                            │
└─────────────────────────────────────────────────────────────┘
```

## Components

### Frontend (Presentation Layer)

**Technology**: Vite + TypeScript

**Responsibilities**:
- User interface rendering
- Client-side validation
- API consumption
- State management
- User interaction handling

**Key Files**:
- `frontend/src/main.ts` - Application entry point
- `frontend/index.html` - HTML template
- `frontend/package.json` - Dependencies and build scripts

**Build Process**:
```bash
npm run build  # TypeScript compilation + Vite bundling
```

### Backend (Business Logic Layer)

**Technology**: Go 1.21+

**Responsibilities**:
- RESTful API endpoints
- Business logic execution
- Request validation
- Database interaction
- Error handling and logging

**Architecture Pattern**: HTTP handlers with standard library `net/http`

**Current Endpoints**:
- `GET /health` - Health check endpoint

**Planned Endpoints** (see [API Specification](../../api-spec/api.yaml)):
- `GET /api/v1/tasks` - List all tasks
- `POST /api/v1/tasks` - Create new task
- `GET /api/v1/tasks/{id}` - Get task by ID
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task

**Key Files**:
- `backend/main.go` - HTTP server setup and handlers
- `backend/go.mod` - Go module dependencies

**Server Configuration**:
```go
ReadTimeout:  10 * time.Second
WriteTimeout: 10 * time.Second
IdleTimeout:  120 * time.Second
```

### Database (Data Persistence Layer)

**Technology**: PostgreSQL 15+ with extensions

**Responsibilities**:
- Data storage and retrieval
- Data integrity enforcement
- Query optimization
- Backup and recovery

**Extensions Enabled**:
- `uuid-ossp` - UUID generation
- `pgcrypto` - Cryptographic functions
- `pg_trgm` - Trigram matching for fuzzy search
- `btree_gin` - GIN indexes for btree types

**Schema Highlights**:

#### Tasks Table
Primary entity for task management with:
- UUID primary keys
- Custom domain types for validation
- ENUM types for status and priority
- JSONB for flexible metadata
- Array fields for tags
- Comprehensive indexing strategy

For detailed schema information, see [Database Design](database-design.md).

## Data Flow

### Creating a Task

```
User Input (Frontend)
    │
    ├─> Client-side validation
    │
    ├─> POST /api/v1/tasks (Backend)
    │       │
    │       ├─> Request validation
    │       ├─> Business logic
    │       └─> INSERT INTO tasks (Database)
    │
    └─> Response with created task
```

### Querying Tasks

```
User Request (Frontend)
    │
    ├─> GET /api/v1/tasks?limit=20 (Backend)
    │       │
    │       ├─> Parameter validation
    │       ├─> SELECT with optimized indexes (Database)
    │       └─> Result mapping
    │
    └─> JSON response with tasks array
```

## Design Patterns

### Backend Patterns

1. **Handler Pattern**: HTTP handlers for route processing
2. **Struct-based Responses**: Typed JSON responses
3. **Error Wrapping**: Contextual error propagation
4. **Timeout Configuration**: Prevents resource exhaustion

### Database Patterns

1. **Domain-Driven Design**: Custom domains for business rules
2. **Trigger-based Automation**: Auto-update timestamps
3. **View-based Analytics**: Precomputed statistics
4. **Index Optimization**: Multi-strategy indexing

## Scalability Considerations

### Current State
- Single-instance deployment
- Direct database connections
- In-process session management

### Future Enhancements
- Connection pooling (recommended: `pgx` for Go)
- Redis for session/cache layer
- Load balancer for multiple backend instances
- Database replication for read scaling
- CDN for frontend static assets

## Security Architecture

### Current Implementation
- Database-level validation through custom domains
- SQL injection prevention through parameterized queries
- CORS configuration for API access

### Planned Enhancements
- Authentication middleware (JWT recommended)
- Authorization rules per endpoint
- Rate limiting
- HTTPS/TLS encryption
- Input sanitization layer
- Audit logging

## Monitoring and Observability

### Current State
- Basic health check endpoint
- Server-side logging with Go's `log` package

### Recommended Additions
- Structured logging (consider `zap` or `zerolog`)
- Metrics collection (Prometheus-compatible)
- Distributed tracing (OpenTelemetry)
- Error tracking (Sentry or similar)
- Database query performance monitoring

## Development vs. Production

### Development
- Database runs in Docker
- Frontend dev server with hot reload
- Backend runs with `go run`
- Verbose logging enabled

### Production (Recommended)
- Managed PostgreSQL service
- Frontend served as static assets
- Backend compiled binary
- Minimal logging, metrics-based monitoring
- Environment-based configuration
- Health checks and liveness probes

## Related Documentation

- [Database Design](database-design.md) - Detailed database schema
- [API Design](api-design.md) - API conventions and standards
- [Development Setup](../guides/development-setup.md) - Local environment configuration
- [Testing Strategy](../guides/testing.md) - Testing approach across layers
