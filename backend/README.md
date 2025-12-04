# Task Management Backend

A simple Go backend service for the task management application.

## Features

- Health check endpoint at `/health`
- JSON response format
- Proper HTTP status codes and error handling
- Configurable timeouts

## Running the Service

```bash
cd backend
go run main.go
```

The service will start on port 8080. You can check the health endpoint:

```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2024-12-04T10:30:00Z",
  "service": "task-management-backend"
}
```

## Development

### Prerequisites

- Go 1.21 or higher

### Building

```bash
go build -o task-backend main.go
```

### Running the binary

```bash
./task-backend
```