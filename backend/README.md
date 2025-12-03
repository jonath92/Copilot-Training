# Backend Service

A simple Go backend service that provides health check functionality.

## Endpoints

- `GET /health` - Returns the health status of the service

## Running the Service

```bash
go run main.go
```

The service will start on port 8080.

## Testing

```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "healthy"
}
```
