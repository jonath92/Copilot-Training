# API Design

The Task Management API follows RESTful principles with a focus on simplicity, consistency, and developer experience.

## API Overview

**Base URL**: `/api/v1`
**Format**: JSON
**Authentication**: None (planned for future versions)

## Design Principles

### REST Standards
- Resource-based URLs
- HTTP methods map to CRUD operations
- Proper HTTP status codes
- Stateless request handling

### Consistency
- Predictable URL patterns
- Uniform response structures
- Standard error formats
- Consistent naming conventions

### Developer Experience
- Clear, descriptive endpoints
- Comprehensive error messages
- Pagination for list endpoints
- Filtering and sorting capabilities

## Endpoints

### Health Check

#### GET /health

Health check endpoint for monitoring and load balancers.

**Response** (200 OK):
```json
{
  "status": "healthy",
  "timestamp": "2024-12-04T10:30:00Z",
  "service": "task-management-backend"
}
```

### Tasks Resource

#### GET /api/v1/tasks

Retrieve a paginated list of tasks.

**Query Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | integer | 20 | Maximum tasks to return (1-100) |
| `offset` | integer | 0 | Number of tasks to skip |
| `status` | string | - | Filter by status (pending, in_progress, etc.) |
| `priority` | string | - | Filter by priority (low, medium, high, urgent) |
| `tags` | string | - | Comma-separated tags to filter by |

**Response** (200 OK):
```json
{
  "tasks": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "title": "Implement user authentication",
      "description": "Add JWT-based authentication",
      "duration": 180,
      "status": "in_progress",
      "priority": "high",
      "tags": ["backend", "security"],
      "metadata": {
        "assigned_to": "john@example.com"
      },
      "estimated_start": "2024-12-05T09:00:00Z",
      "estimated_end": "2024-12-05T12:00:00Z",
      "actual_start": "2024-12-05T09:15:00Z",
      "actual_end": null,
      "created_at": "2024-12-04T14:30:00Z",
      "updated_at": "2024-12-05T09:15:00Z",
      "created_by": "admin"
    }
  ],
  "total": 42,
  "limit": 20,
  "offset": 0
}
```

#### POST /api/v1/tasks

Create a new task.

**Request Body**:
```json
{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "duration": 180,
  "priority": "high",
  "tags": ["backend", "security"],
  "metadata": {
    "assigned_to": "john@example.com"
  },
  "estimated_start": "2024-12-05T09:00:00Z",
  "estimated_end": "2024-12-05T12:00:00Z"
}
```

**Response** (201 Created):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "duration": 180,
  "status": "pending",
  "priority": "high",
  "tags": ["backend", "security"],
  "metadata": {
    "assigned_to": "john@example.com"
  },
  "estimated_start": "2024-12-05T09:00:00Z",
  "estimated_end": "2024-12-05T12:00:00Z",
  "actual_start": null,
  "actual_end": null,
  "created_at": "2024-12-04T14:30:00Z",
  "updated_at": "2024-12-04T14:30:00Z",
  "created_by": null
}
```

#### GET /api/v1/tasks/{id}

Retrieve a specific task by ID.

**Path Parameters**:
- `id` (UUID) - Task identifier

**Response** (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "duration": 180,
  "status": "in_progress",
  "priority": "high",
  "tags": ["backend", "security"],
  "metadata": {
    "assigned_to": "john@example.com"
  },
  "estimated_start": "2024-12-05T09:00:00Z",
  "estimated_end": "2024-12-05T12:00:00Z",
  "actual_start": "2024-12-05T09:15:00Z",
  "actual_end": null,
  "created_at": "2024-12-04T14:30:00Z",
  "updated_at": "2024-12-05T09:15:00Z",
  "created_by": "admin"
}
```

**Response** (404 Not Found):
```json
{
  "error": "Task not found",
  "code": "TASK_NOT_FOUND",
  "message": "No task exists with ID: 123e4567-e89b-12d3-a456-426614174000"
}
```

#### PUT /api/v1/tasks/{id}

Update an existing task.

**Path Parameters**:
- `id` (UUID) - Task identifier

**Request Body**:
```json
{
  "title": "Implement OAuth2 authentication",
  "description": "Add OAuth2 support with Google and GitHub providers",
  "duration": 240,
  "status": "in_progress",
  "priority": "urgent",
  "tags": ["backend", "security", "oauth"],
  "metadata": {
    "assigned_to": "john@example.com",
    "reviewed_by": "jane@example.com"
  },
  "actual_start": "2024-12-05T09:15:00Z"
}
```

**Response** (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Implement OAuth2 authentication",
  "description": "Add OAuth2 support with Google and GitHub providers",
  "duration": 240,
  "status": "in_progress",
  "priority": "urgent",
  "tags": ["backend", "security", "oauth"],
  "metadata": {
    "assigned_to": "john@example.com",
    "reviewed_by": "jane@example.com"
  },
  "estimated_start": "2024-12-05T09:00:00Z",
  "estimated_end": "2024-12-05T12:00:00Z",
  "actual_start": "2024-12-05T09:15:00Z",
  "actual_end": null,
  "created_at": "2024-12-04T14:30:00Z",
  "updated_at": "2024-12-05T10:20:00Z",
  "created_by": "admin"
}
```

#### DELETE /api/v1/tasks/{id}

Delete a task.

**Path Parameters**:
- `id` (UUID) - Task identifier

**Response** (204 No Content)

**Response** (404 Not Found):
```json
{
  "error": "Task not found",
  "code": "TASK_NOT_FOUND",
  "message": "No task exists with ID: 123e4567-e89b-12d3-a456-426614174000"
}
```

### Search and Analytics

#### GET /api/v1/tasks/search

Fuzzy search for tasks by title.

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `q` | string | Yes | Search query |
| `limit` | integer | No | Maximum results (default: 20) |

**Response** (200 OK):
```json
{
  "results": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "title": "Implement user authentication",
      "similarity_score": 0.85
    },
    {
      "id": "987fcdeb-51a2-43b1-9876-543210fedcba",
      "title": "Implement authorization",
      "similarity_score": 0.62
    }
  ],
  "total": 2
}
```

#### GET /api/v1/analytics

Get task analytics and statistics.

**Response** (200 OK):
```json
{
  "by_status": [
    {
      "status": "pending",
      "total_tasks": 15,
      "completed_count": 0,
      "avg_actual_duration": null,
      "avg_estimated_duration": 120
    },
    {
      "status": "in_progress",
      "total_tasks": 8,
      "completed_count": 0,
      "avg_actual_duration": null,
      "avg_estimated_duration": 180
    },
    {
      "status": "completed",
      "total_tasks": 42,
      "completed_count": 42,
      "avg_actual_duration": 195,
      "avg_estimated_duration": 150
    }
  ]
}
```

## HTTP Status Codes

The API uses standard HTTP status codes:

### Success Codes
- `200 OK` - Request succeeded
- `201 Created` - Resource created successfully
- `204 No Content` - Request succeeded with no response body

### Client Error Codes
- `400 Bad Request` - Invalid request format or parameters
- `404 Not Found` - Resource doesn't exist
- `405 Method Not Allowed` - HTTP method not supported for endpoint
- `422 Unprocessable Entity` - Validation error

### Server Error Codes
- `500 Internal Server Error` - Unexpected server error
- `503 Service Unavailable` - Server temporarily unavailable

## Error Response Format

All errors follow a consistent structure:

```json
{
  "error": "Brief error description",
  "code": "ERROR_CODE_CONSTANT",
  "message": "Detailed error message with context",
  "details": {
    "field": "Additional error details"
  }
}
```

### Example: Validation Error

**Response** (422 Unprocessable Entity):
```json
{
  "error": "Validation failed",
  "code": "VALIDATION_ERROR",
  "message": "One or more fields failed validation",
  "details": {
    "title": "Title must be between 3 and 255 characters",
    "duration": "Duration must be between 1 and 10080 minutes"
  }
}
```

## Request Validation

### Title Validation
- Length: 3-255 characters
- Cannot start or end with whitespace
- Must contain meaningful content

### Duration Validation
- Minimum: 1 minute
- Maximum: 10,080 minutes (1 week)
- Must be a positive integer

### Status Validation
Valid values: `pending`, `in_progress`, `completed`, `cancelled`, `archived`

### Priority Validation
Valid values: `low`, `medium`, `high`, `urgent`

### Timestamp Validation
- Must be in ISO 8601 format with timezone
- `estimated_end` must be after `estimated_start`
- `actual_end` must be after `actual_start`

## Pagination

List endpoints support pagination via `limit` and `offset` parameters:

**Request**:
```
GET /api/v1/tasks?limit=20&offset=40
```

**Response**:
```json
{
  "tasks": [...],
  "total": 150,
  "limit": 20,
  "offset": 40
}
```

**Pagination Calculation**:
- Page 1: `offset=0, limit=20`
- Page 2: `offset=20, limit=20`
- Page 3: `offset=40, limit=20`

## Rate Limiting

*Currently not implemented. Planned for future versions.*

Recommended limits:
- 100 requests per minute per IP
- 1000 requests per hour per API key

## Versioning

The API uses URL versioning (`/api/v1/`). Breaking changes will result in a new version (`/api/v2/`).

### Backward Compatibility
- Adding new optional fields: Non-breaking
- Adding new endpoints: Non-breaking
- Removing fields: Breaking (requires new version)
- Changing field types: Breaking (requires new version)
- Changing validation rules: Potentially breaking

## OpenAPI Specification

The complete API specification is available in OpenAPI 3.0 format:
- [OpenAPI YAML](../../api-spec/api.yaml)

Generate client SDKs:
```bash
./scripts/generate-api.sh
```

## Future Enhancements

### Planned Features
- Authentication and authorization
- Webhook support for task events
- Batch operations
- GraphQL endpoint
- Real-time updates via WebSocket
- File attachments for tasks
- Task dependencies and subtasks

### API Evolution
- `POST /api/v1/auth/login` - User authentication
- `GET /api/v1/tasks/{id}/dependencies` - Task dependencies
- `GET /api/v1/tasks/{id}/subtasks` - Nested tasks
- `POST /api/v1/tasks/{id}/attachments` - File uploads

## Related Documentation

- [OpenAPI Specification](../../api-spec/api.yaml) - Machine-readable API definition
- [Backend Documentation](../../backend/README.md) - Implementation details
- [System Architecture](system-architecture.md) - Overall system design
- [Database Design](database-design.md) - Data model and schema
