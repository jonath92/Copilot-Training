# API Documentation

Complete reference for the Task Management REST API.

## Base Information

- **Base URL**: `http://localhost:8080/api/v1`
- **Content Type**: `application/json`
- **Authentication**: None (currently)

## Quick Start

### Creating Your First Task

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My first task",
    "description": "Learn the API",
    "duration": 30,
    "priority": "medium"
  }'
```

### Listing Tasks

```bash
curl http://localhost:8080/api/v1/tasks?limit=10
```

### Getting a Specific Task

```bash
curl http://localhost:8080/api/v1/tasks/{task-id}
```

## Complete API Reference

For detailed endpoint specifications, see [API Design Documentation](../architecture/api-design.md).

## OpenAPI Specification

The API is fully documented using OpenAPI 3.0:
- [OpenAPI YAML File](../../api-spec/api.yaml)

### Using the OpenAPI Spec

#### Generate TypeScript Client

```bash
npx @openapitools/openapi-generator-cli generate \
  -i api-spec/api.yaml \
  -g typescript-fetch \
  -o frontend/src/generated
```

#### Generate Go Server Stub

```bash
oapi-codegen -package api api-spec/api.yaml > backend/api/generated.go
```

#### View in Swagger UI

```bash
docker run -p 8081:8080 \
  -e SWAGGER_JSON=/api.yaml \
  -v $(pwd)/api-spec/api.yaml:/api.yaml \
  swaggerapi/swagger-ui
```

Then open `http://localhost:8081`

## Code Examples

### JavaScript/TypeScript

#### Fetch Tasks

```typescript
interface Task {
  id: string;
  title: string;
  description?: string;
  duration: number;
  status: string;
  priority: string;
  created_at: string;
  updated_at: string;
}

async function getTasks(limit = 20, offset = 0): Promise<Task[]> {
  const response = await fetch(
    `http://localhost:8080/api/v1/tasks?limit=${limit}&offset=${offset}`
  );
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  const data = await response.json();
  return data.tasks;
}

// Usage
const tasks = await getTasks(10, 0);
console.log(tasks);
```

#### Create Task

```typescript
interface CreateTaskRequest {
  title: string;
  description?: string;
  duration: number;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  tags?: string[];
  estimated_start?: string;
  estimated_end?: string;
}

async function createTask(task: CreateTaskRequest): Promise<Task> {
  const response = await fetch('http://localhost:8080/api/v1/tasks', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(task),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return response.json();
}

// Usage
const newTask = await createTask({
  title: 'Implement feature X',
  description: 'Add support for feature X',
  duration: 120,
  priority: 'high',
  tags: ['backend', 'api'],
});
```

#### Update Task

```typescript
async function updateTask(id: string, updates: Partial<Task>): Promise<Task> {
  const response = await fetch(`http://localhost:8080/api/v1/tasks/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(updates),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return response.json();
}

// Usage
const updated = await updateTask('123e4567-e89b-12d3-a456-426614174000', {
  status: 'completed',
  actual_end: new Date().toISOString(),
});
```

#### Delete Task

```typescript
async function deleteTask(id: string): Promise<void> {
  const response = await fetch(`http://localhost:8080/api/v1/tasks/${id}`, {
    method: 'DELETE',
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
}

// Usage
await deleteTask('123e4567-e89b-12d3-a456-426614174000');
```

### Go

#### Fetch Tasks

```go
package main

import (
    "encoding/json"
    "fmt"
    "net/http"
    "net/url"
)

type Task struct {
    ID          string   `json:"id"`
    Title       string   `json:"title"`
    Description string   `json:"description,omitempty"`
    Duration    int      `json:"duration"`
    Status      string   `json:"status"`
    Priority    string   `json:"priority"`
    Tags        []string `json:"tags,omitempty"`
    CreatedAt   string   `json:"created_at"`
    UpdatedAt   string   `json:"updated_at"`
}

type TasksResponse struct {
    Tasks  []Task `json:"tasks"`
    Total  int    `json:"total"`
    Limit  int    `json:"limit"`
    Offset int    `json:"offset"`
}

func getTasks(limit, offset int) (*TasksResponse, error) {
    baseURL := "http://localhost:8080/api/v1/tasks"
    params := url.Values{}
    params.Add("limit", fmt.Sprintf("%d", limit))
    params.Add("offset", fmt.Sprintf("%d", offset))
    
    resp, err := http.Get(baseURL + "?" + params.Encode())
    if err != nil {
        return nil, fmt.Errorf("request failed: %w", err)
    }
    defer resp.Body.Close()
    
    if resp.StatusCode != http.StatusOK {
        return nil, fmt.Errorf("unexpected status: %d", resp.StatusCode)
    }
    
    var result TasksResponse
    if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
        return nil, fmt.Errorf("decode failed: %w", err)
    }
    
    return &result, nil
}

// Usage
tasks, err := getTasks(10, 0)
if err != nil {
    log.Fatal(err)
}
fmt.Printf("Found %d tasks\n", len(tasks.Tasks))
```

#### Create Task

```go
type CreateTaskRequest struct {
    Title          string   `json:"title"`
    Description    string   `json:"description,omitempty"`
    Duration       int      `json:"duration"`
    Priority       string   `json:"priority"`
    Tags           []string `json:"tags,omitempty"`
    EstimatedStart string   `json:"estimated_start,omitempty"`
    EstimatedEnd   string   `json:"estimated_end,omitempty"`
}

func createTask(req CreateTaskRequest) (*Task, error) {
    body, err := json.Marshal(req)
    if err != nil {
        return nil, fmt.Errorf("marshal failed: %w", err)
    }
    
    resp, err := http.Post(
        "http://localhost:8080/api/v1/tasks",
        "application/json",
        bytes.NewReader(body),
    )
    if err != nil {
        return nil, fmt.Errorf("request failed: %w", err)
    }
    defer resp.Body.Close()
    
    if resp.StatusCode != http.StatusCreated {
        return nil, fmt.Errorf("unexpected status: %d", resp.StatusCode)
    }
    
    var task Task
    if err := json.NewDecoder(resp.Body).Decode(&task); err != nil {
        return nil, fmt.Errorf("decode failed: %w", err)
    }
    
    return &task, nil
}

// Usage
task, err := createTask(CreateTaskRequest{
    Title:    "Implement feature Y",
    Duration: 180,
    Priority: "high",
    Tags:     []string{"backend", "database"},
})
```

### Python

#### Fetch Tasks

```python
import requests
from typing import List, Optional
from dataclasses import dataclass

@dataclass
class Task:
    id: str
    title: str
    description: Optional[str]
    duration: int
    status: str
    priority: str
    tags: List[str]
    created_at: str
    updated_at: str

def get_tasks(limit: int = 20, offset: int = 0) -> List[Task]:
    response = requests.get(
        'http://localhost:8080/api/v1/tasks',
        params={'limit': limit, 'offset': offset}
    )
    response.raise_for_status()
    
    data = response.json()
    return [Task(**task) for task in data['tasks']]

# Usage
tasks = get_tasks(limit=10)
for task in tasks:
    print(f"{task.title} - {task.status}")
```

#### Create Task

```python
from datetime import datetime
from typing import Optional, List

def create_task(
    title: str,
    duration: int,
    priority: str = 'medium',
    description: Optional[str] = None,
    tags: Optional[List[str]] = None,
) -> Task:
    payload = {
        'title': title,
        'duration': duration,
        'priority': priority,
    }
    
    if description:
        payload['description'] = description
    if tags:
        payload['tags'] = tags
    
    response = requests.post(
        'http://localhost:8080/api/v1/tasks',
        json=payload
    )
    response.raise_for_status()
    
    return Task(**response.json())

# Usage
task = create_task(
    title='Fix bug #123',
    duration=45,
    priority='urgent',
    tags=['bugfix', 'frontend']
)
```

## Error Handling

### Common Errors

#### 400 Bad Request

```json
{
  "error": "Invalid request",
  "code": "BAD_REQUEST",
  "message": "Missing required field: title"
}
```

**Solution**: Check request payload matches expected format.

#### 404 Not Found

```json
{
  "error": "Task not found",
  "code": "TASK_NOT_FOUND",
  "message": "No task exists with ID: 123e4567-e89b-12d3-a456-426614174000"
}
```

**Solution**: Verify the task ID exists in the database.

#### 422 Unprocessable Entity

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

**Solution**: Fix validation errors listed in `details`.

#### 500 Internal Server Error

```json
{
  "error": "Internal server error",
  "code": "INTERNAL_ERROR",
  "message": "An unexpected error occurred"
}
```

**Solution**: Check server logs and report if persistent.

## Best Practices

### Rate Limiting

While not currently enforced, design clients to:
- Cache responses when appropriate
- Implement exponential backoff on errors
- Batch operations where possible

### Pagination

Always use pagination for large datasets:

```typescript
async function getAllTasks(): Promise<Task[]> {
  const allTasks: Task[] = [];
  const limit = 100;
  let offset = 0;
  let hasMore = true;
  
  while (hasMore) {
    const response = await fetch(
      `http://localhost:8080/api/v1/tasks?limit=${limit}&offset=${offset}`
    );
    const data = await response.json();
    
    allTasks.push(...data.tasks);
    offset += limit;
    hasMore = data.tasks.length === limit;
  }
  
  return allTasks;
}
```

### Error Handling

Always handle errors gracefully:

```typescript
try {
  const task = await createTask(taskData);
  console.log('Task created:', task.id);
} catch (error) {
  if (error.response) {
    // Server responded with error
    const apiError = await error.response.json();
    console.error('API Error:', apiError.message);
    
    if (apiError.details) {
      // Handle validation errors
      Object.entries(apiError.details).forEach(([field, message]) => {
        console.error(`${field}: ${message}`);
      });
    }
  } else {
    // Network or other error
    console.error('Network error:', error.message);
  }
}
```

## Postman Collection

Import the OpenAPI spec into Postman:
1. Open Postman
2. Click Import
3. Select `api-spec/api.yaml`
4. Collection will be auto-generated

## Related Documentation

- [API Design](../architecture/api-design.md) - Detailed API architecture
- [OpenAPI Specification](../../api-spec/api.yaml) - Machine-readable API definition
- [Backend Documentation](../../backend/README.md) - Server implementation
- [Frontend Integration](../../frontend/README.md) - Client-side integration
