# Testing Guide

Comprehensive guide to testing strategies, tools, and practices for the Task Management Application.

## Testing Philosophy

The application uses a multi-layered testing approach:

1. **Unit Tests** - Test individual functions and components
2. **Integration Tests** - Test component interactions
3. **End-to-End Tests** - Test complete user workflows
4. **Manual Testing** - Exploratory and acceptance testing

## Testing Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Backend | Go `testing` | Unit and integration tests |
| Frontend | Vitest (planned) | Component and unit tests |
| E2E | Playwright | Full application workflows |
| Database | SQL scripts | Schema and data validation |

## Backend Testing (Go)

### Running Tests

```bash
cd backend

# Run all tests
go test ./...

# Run tests with verbose output
go test -v ./...

# Run tests with coverage
go test -cover ./...

# Generate coverage report
go test -coverprofile=coverage.out ./...
go tool cover -html=coverage.out
```

### Writing Unit Tests

**Example: Testing HTTP Handler**

```go
package main

import (
    "net/http"
    "net/http/httptest"
    "testing"
)

func TestHealthHandler(t *testing.T) {
    // Arrange
    req := httptest.NewRequest(http.MethodGet, "/health", nil)
    w := httptest.NewRecorder()
    
    // Act
    healthHandler(w, req)
    
    // Assert
    if w.Code != http.StatusOK {
        t.Errorf("Expected status 200, got %d", w.Code)
    }
    
    contentType := w.Header().Get("Content-Type")
    if contentType != "application/json" {
        t.Errorf("Expected Content-Type application/json, got %s", contentType)
    }
}

func TestHealthHandler_MethodNotAllowed(t *testing.T) {
    req := httptest.NewRequest(http.MethodPost, "/health", nil)
    w := httptest.NewRecorder()
    
    healthHandler(w, req)
    
    if w.Code != http.StatusMethodNotAllowed {
        t.Errorf("Expected status 405, got %d", w.Code)
    }
}
```

### Table-Driven Tests

```go
func TestValidateTask(t *testing.T) {
    tests := []struct {
        name      string
        task      Task
        wantError bool
        errorMsg  string
    }{
        {
            name: "valid task",
            task: Task{
                Title:    "Valid Task",
                Duration: 60,
                Priority: "medium",
            },
            wantError: false,
        },
        {
            name: "title too short",
            task: Task{
                Title:    "Ab",
                Duration: 60,
                Priority: "medium",
            },
            wantError: true,
            errorMsg:  "title must be between 3 and 255 characters",
        },
        {
            name: "invalid duration",
            task: Task{
                Title:    "Valid Title",
                Duration: 0,
                Priority: "medium",
            },
            wantError: true,
            errorMsg:  "duration must be between 1 and 10080 minutes",
        },
    }
    
    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            err := validateTask(tt.task)
            
            if tt.wantError && err == nil {
                t.Errorf("Expected error but got none")
            }
            
            if !tt.wantError && err != nil {
                t.Errorf("Expected no error but got: %v", err)
            }
            
            if tt.wantError && err != nil && err.Error() != tt.errorMsg {
                t.Errorf("Expected error message '%s', got '%s'", tt.errorMsg, err.Error())
            }
        })
    }
}
```

### Integration Tests

**Testing Database Operations**:

```go
func TestCreateTask_Database(t *testing.T) {
    // Setup test database
    db := setupTestDB(t)
    defer cleanupTestDB(t, db)
    
    // Create task
    task := Task{
        Title:    "Integration Test Task",
        Duration: 120,
        Priority: "high",
    }
    
    id, err := createTask(db, task)
    if err != nil {
        t.Fatalf("Failed to create task: %v", err)
    }
    
    // Verify task was created
    retrieved, err := getTask(db, id)
    if err != nil {
        t.Fatalf("Failed to retrieve task: %v", err)
    }
    
    if retrieved.Title != task.Title {
        t.Errorf("Expected title '%s', got '%s'", task.Title, retrieved.Title)
    }
}

func setupTestDB(t *testing.T) *sql.DB {
    // Create test database connection
    db, err := sql.Open("postgres", "postgres://testuser:testpass@localhost/testdb")
    if err != nil {
        t.Fatalf("Failed to connect to test database: %v", err)
    }
    
    // Run migrations
    runMigrations(db)
    
    return db
}

func cleanupTestDB(t *testing.T, db *sql.DB) {
    // Clean up test data
    _, err := db.Exec("TRUNCATE tasks CASCADE")
    if err != nil {
        t.Logf("Warning: Failed to clean up test database: %v", err)
    }
    db.Close()
}
```

## Frontend Testing

### Setting Up Vitest

```bash
cd frontend

# Install Vitest and testing utilities
npm install -D vitest @vitest/ui jsdom
npm install -D @testing-library/dom @testing-library/user-event
```

**vite.config.ts**:
```typescript
import { defineConfig } from 'vite'

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
})
```

### Unit Tests

**Example: Testing Utility Function**

```typescript
import { describe, it, expect } from 'vitest'
import { formatDuration } from './utils'

describe('formatDuration', () => {
  it('formats minutes correctly', () => {
    expect(formatDuration(30)).toBe('30 minutes')
    expect(formatDuration(1)).toBe('1 minute')
  })
  
  it('formats hours correctly', () => {
    expect(formatDuration(60)).toBe('1 hour')
    expect(formatDuration(120)).toBe('2 hours')
  })
  
  it('formats hours and minutes', () => {
    expect(formatDuration(90)).toBe('1 hour 30 minutes')
    expect(formatDuration(150)).toBe('2 hours 30 minutes')
  })
})
```

### API Client Tests

```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { getTasks, createTask } from './api/tasks'

// Mock fetch
global.fetch = vi.fn()

describe('Task API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })
  
  it('getTasks fetches tasks from API', async () => {
    const mockTasks = [
      { id: '1', title: 'Task 1', duration: 30 },
      { id: '2', title: 'Task 2', duration: 60 },
    ]
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ tasks: mockTasks, total: 2 }),
    })
    
    const tasks = await getTasks(10, 0)
    
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/v1/tasks?limit=10&offset=0'
    )
    expect(tasks).toEqual(mockTasks)
  })
  
  it('createTask sends POST request', async () => {
    const newTask = {
      title: 'New Task',
      duration: 45,
      priority: 'high',
    }
    
    const createdTask = { id: '123', ...newTask }
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => createdTask,
    })
    
    const result = await createTask(newTask)
    
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/v1/tasks',
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newTask),
      })
    )
    expect(result).toEqual(createdTask)
  })
})
```

## End-to-End Testing (Playwright)

### Running E2E Tests

```bash
cd e2e

# Run all tests
npm test

# Run in headed mode (see browser)
npm run test:headed

# Interactive UI mode
npm run test:ui

# Run specific test file
npx playwright test tasks.spec.ts

# Debug mode
npx playwright test --debug
```

### Writing E2E Tests

**Example: Task Creation Workflow**

```typescript
import { test, expect } from '@playwright/test'

test.describe('Task Management', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to app and wait for it to load
    await page.goto('http://localhost:5173')
    await page.waitForLoadState('networkidle')
  })
  
  test('should create a new task', async ({ page }) => {
    // Fill in task form
    await page.fill('[data-testid="task-title"]', 'Implement feature X')
    await page.fill('[data-testid="task-description"]', 'Add new feature to the application')
    await page.fill('[data-testid="task-duration"]', '120')
    await page.selectOption('[data-testid="task-priority"]', 'high')
    
    // Submit form
    await page.click('[data-testid="create-task-button"]')
    
    // Verify task appears in list
    await expect(page.locator('[data-testid="task-list"]')).toContainText('Implement feature X')
    
    // Verify task details
    const taskCard = page.locator('[data-testid="task-card"]').first()
    await expect(taskCard).toContainText('120 minutes')
    await expect(taskCard).toContainText('high')
  })
  
  test('should update task status', async ({ page }) => {
    // Create a task first
    await createTask(page, 'Test Task', 60)
    
    // Click on task to open details
    await page.click('text=Test Task')
    
    // Change status
    await page.selectOption('[data-testid="task-status"]', 'in_progress')
    await page.click('[data-testid="save-task"]')
    
    // Verify status updated
    await expect(page.locator('[data-testid="task-status-badge"]')).toHaveText('In Progress')
  })
  
  test('should delete a task', async ({ page }) => {
    // Create a task
    await createTask(page, 'Task to Delete', 30)
    
    // Delete task
    await page.click('[data-testid="task-options"]')
    await page.click('[data-testid="delete-task"]')
    
    // Confirm deletion
    await page.click('[data-testid="confirm-delete"]')
    
    // Verify task removed
    await expect(page.locator('text=Task to Delete')).not.toBeVisible()
  })
})

// Helper function
async function createTask(page, title: string, duration: number) {
  await page.fill('[data-testid="task-title"]', title)
  await page.fill('[data-testid="task-duration"]', duration.toString())
  await page.click('[data-testid="create-task-button"]')
  await page.waitForSelector(`text=${title}`)
}
```

### Page Object Pattern

**tasks.page.ts**:
```typescript
import { Page, Locator } from '@playwright/test'

export class TasksPage {
  readonly page: Page
  readonly titleInput: Locator
  readonly durationInput: Locator
  readonly prioritySelect: Locator
  readonly createButton: Locator
  readonly taskList: Locator
  
  constructor(page: Page) {
    this.page = page
    this.titleInput = page.locator('[data-testid="task-title"]')
    this.durationInput = page.locator('[data-testid="task-duration"]')
    this.prioritySelect = page.locator('[data-testid="task-priority"]')
    this.createButton = page.locator('[data-testid="create-task-button"]')
    this.taskList = page.locator('[data-testid="task-list"]')
  }
  
  async goto() {
    await this.page.goto('http://localhost:5173')
    await this.page.waitForLoadState('networkidle')
  }
  
  async createTask(title: string, duration: number, priority = 'medium') {
    await this.titleInput.fill(title)
    await this.durationInput.fill(duration.toString())
    await this.prioritySelect.selectOption(priority)
    await this.createButton.click()
  }
  
  async getTaskByTitle(title: string) {
    return this.taskList.locator(`text=${title}`)
  }
}
```

**Using Page Object**:
```typescript
import { test, expect } from '@playwright/test'
import { TasksPage } from './pages/tasks.page'

test('create task using page object', async ({ page }) => {
  const tasksPage = new TasksPage(page)
  
  await tasksPage.goto()
  await tasksPage.createTask('Feature Implementation', 180, 'high')
  
  const task = await tasksPage.getTaskByTitle('Feature Implementation')
  await expect(task).toBeVisible()
})
```

## Database Testing

### Schema Validation

```sql
-- Test script: test_schema.sql

-- Verify tables exist
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_name = 'tasks';

-- Verify columns
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'tasks'
ORDER BY ordinal_position;

-- Verify constraints
SELECT constraint_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'tasks';

-- Verify indexes
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename = 'tasks';
```

### Data Validation

```sql
-- Test data integrity

-- Test: Title validation
INSERT INTO tasks (title, duration, priority) 
VALUES ('AB', 60, 'medium');  -- Should fail (title too short)

-- Test: Duration validation
INSERT INTO tasks (title, duration, priority) 
VALUES ('Valid Title', 0, 'medium');  -- Should fail (duration invalid)

-- Test: Enum validation
INSERT INTO tasks (title, duration, priority) 
VALUES ('Valid Title', 60, 'invalid');  -- Should fail (invalid priority)

-- Test: Time range validation
INSERT INTO tasks (title, duration, estimated_start, estimated_end) 
VALUES ('Valid Title', 60, '2024-12-05 14:00:00', '2024-12-05 13:00:00');  
-- Should fail (end before start)
```

## Test Coverage

### Measuring Coverage

**Backend**:
```bash
cd backend
go test -coverprofile=coverage.out ./...
go tool cover -html=coverage.out -o coverage.html
open coverage.html
```

**Frontend**:
```bash
cd frontend
npm test -- --coverage
```

### Coverage Goals

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: Critical paths covered
- **E2E Tests**: All user workflows covered

## Continuous Integration

Tests run automatically on every pull request via GitHub Actions:

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-go@v4
        with:
          go-version: '1.21'
      - run: cd backend && go test -v ./...
  
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: cd e2e && npm ci
      - run: npx playwright install --with-deps
      - run: npm test
```

## Best Practices

### General
- Write tests before or alongside code (TDD/BDD)
- Keep tests simple and focused
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)
- Mock external dependencies
- Clean up test data

### Backend
- Use table-driven tests for multiple scenarios
- Test error cases and edge cases
- Use `t.Helper()` for test helper functions
- Isolate database tests with transactions or cleanup

### Frontend
- Test user interactions, not implementation details
- Use data-testid attributes for selectors
- Mock API calls to avoid backend dependency
- Test accessibility (ARIA labels, keyboard navigation)

### E2E
- Test critical user journeys first
- Keep tests independent (no shared state)
- Use realistic test data
- Add explicit waits for async operations
- Take screenshots on failures

## Troubleshooting

### Tests Fail Locally

1. **Check dependencies**: `go mod download`, `npm install`
2. **Verify database**: Ensure PostgreSQL is running
3. **Check environment**: Set required environment variables
4. **Clear cache**: Delete build artifacts and caches

### Flaky E2E Tests

1. **Add explicit waits**: Use `waitForSelector` instead of fixed delays
2. **Check for race conditions**: Ensure proper sequencing
3. **Increase timeouts**: For slow CI environments
4. **Run in headed mode**: Debug visually

### Database Tests Fail

1. **Reset database**: `docker-compose down -v && docker-compose up -d`
2. **Check schema**: Verify migrations ran successfully
3. **Isolate tests**: Use transactions or separate test database

## Related Documentation

- [Backend Documentation](../../backend/README.md) - Backend testing specifics
- [E2E Documentation](../../e2e/README.md) - Playwright configuration
- [Contributing Guidelines](contributing.md) - Testing requirements for contributions
- [CI/CD Pipeline](../../.github/workflows/) - Automated testing workflows
