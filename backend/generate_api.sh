#!/bin/bash

# Script to generate API interface code for the Go backend using oapi-codegen
# This script uses the OpenAPI specification to generate Go server code

set -e

BACKEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
API_SPEC="../api-spec/api.yaml"

cd "$BACKEND_DIR"

echo "Generating API interface using oapi-codegen..."

# Check if oapi-codegen is installed
if ! command -v oapi-codegen &> /dev/null; then
    echo "Installing oapi-codegen..."
    go install github.com/oapi-codegen/oapi-codegen/v2/cmd/oapi-codegen@latest
fi

# Generate types (models)
echo "Generating types from OpenAPI spec..."
oapi-codegen -package main -generate types "$API_SPEC" > models_gen.go

# Generate server interface and handlers
echo "Generating server code from OpenAPI spec..."
oapi-codegen -package main -generate chi-server "$API_SPEC" > server_gen.go

echo "✓ Generated models_gen.go (types from OpenAPI spec)"
echo "✓ Generated server_gen.go (server interface and routing)"

# Create implementation of the server interface
cat > handlers.go << 'EOF'
package main

import (
	"encoding/json"
	"net/http"
)

// ServerImpl implements the ServerInterface from generated code
type ServerImpl struct{}

// NewServerImpl creates a new server implementation
func NewServerImpl() *ServerImpl {
	return &ServerImpl{}
}

// GetTasks handles GET /api/tasks
func (s *ServerImpl) GetTasks(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	
	tasks := GetAllTasks()
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(tasks)
}

// CreateTask handles POST /api/tasks
func (s *ServerImpl) PostTasks(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	
	var input TaskInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(Error{
			Message: "Invalid request body",
		})
		return
	}
	
	// Validate input
	if input.Title == "" || input.Description == "" || input.Duration < 1 {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(Error{
			Message: "Missing or invalid required fields",
		})
		return
	}
	
	task := CreateTask(input)
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(task)
}

// GetTaskById handles GET /api/tasks/{id}
func (s *ServerImpl) GetTasksId(w http.ResponseWriter, r *http.Request, id int64) {
	w.Header().Set("Content-Type", "application/json")
	
	task, found := GetTaskByID(id)
	if !found {
		w.WriteHeader(http.StatusNotFound)
		json.NewEncoder(w).Encode(Error{
			Message: "Task not found",
		})
		return
	}
	
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(task)
}

// UpdateTask handles PUT /api/tasks/{id}
func (s *ServerImpl) PutTasksId(w http.ResponseWriter, r *http.Request, id int64) {
	w.Header().Set("Content-Type", "application/json")
	
	var input TaskInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(Error{
			Message: "Invalid request body",
		})
		return
	}
	
	// Validate input
	if input.Title == "" || input.Description == "" || input.Duration < 1 {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(Error{
			Message: "Missing or invalid required fields",
		})
		return
	}
	
	task, found := UpdateTask(id, input)
	if !found {
		w.WriteHeader(http.StatusNotFound)
		json.NewEncoder(w).Encode(Error{
			Message: "Task not found",
		})
		return
	}
	
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(task)
}

// DeleteTask handles DELETE /api/tasks/{id}
func (s *ServerImpl) DeleteTasksId(w http.ResponseWriter, r *http.Request, id int64) {
	w.Header().Set("Content-Type", "application/json")
	
	found := DeleteTask(id)
	if !found {
		w.WriteHeader(http.StatusNotFound)
		json.NewEncoder(w).Encode(Error{
			Message: "Task not found",
		})
		return
	}
	
	w.WriteHeader(http.StatusNoContent)
}
EOF

echo "✓ Created handlers.go (implementation of ServerInterface)"

# Create store.go with in-memory data store
cat > store.go << 'EOF'
package main

import (
	"sync"
)

var (
	tasks   = make(map[int64]Task)
	nextID  int64 = 1
	tasksMu sync.RWMutex
)

// GetAllTasks returns all tasks
func GetAllTasks() []Task {
	tasksMu.RLock()
	defer tasksMu.RUnlock()
	
	result := make([]Task, 0, len(tasks))
	for _, task := range tasks {
		result = append(result, task)
	}
	return result
}

// CreateTask creates a new task
func CreateTask(input TaskInput) Task {
	tasksMu.Lock()
	defer tasksMu.Unlock()
	
	task := Task{
		Id:          nextID,
		Title:       input.Title,
		Description: input.Description,
		Duration:    input.Duration,
	}
	
	tasks[nextID] = task
	nextID++
	
	return task
}

// GetTaskByID returns a task by ID
func GetTaskByID(id int64) (Task, bool) {
	tasksMu.RLock()
	defer tasksMu.RUnlock()
	
	task, found := tasks[id]
	return task, found
}

// UpdateTask updates an existing task
func UpdateTask(id int64, input TaskInput) (Task, bool) {
	tasksMu.Lock()
	defer tasksMu.Unlock()
	
	task, found := tasks[id]
	if !found {
		return Task{}, false
	}
	
	task.Title = input.Title
	task.Description = input.Description
	task.Duration = input.Duration
	
	tasks[id] = task
	return task, true
}

// DeleteTask deletes a task by ID
func DeleteTask(id int64) bool {
	tasksMu.Lock()
	defer tasksMu.Unlock()
	
	_, found := tasks[id]
	if !found {
		return false
	}
	
	delete(tasks, id)
	return true
}
EOF

echo "✓ Created store.go (in-memory data store)"

# Update main.go to use oapi-codegen generated server
cat > main.go << 'EOF'
package main

import (
	"log"
	"net/http"
	
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func main() {
	// Create chi router
	r := chi.NewRouter()
	
	// Add middleware
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)
	
	// Health check endpoint
	r.Get("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"healthy"}`))
	})
	
	// Create server implementation
	serverImpl := NewServerImpl()
	
	// Register generated handlers
	HandlerFromMux(serverImpl, r)
	
	port := ":8080"
	log.Printf("Server starting on port %s", port)
	log.Printf("API available at http://localhost:8080/api")
	
	if err := http.ListenAndServe(port, r); err != nil {
		log.Fatal(err)
	}
}
EOF

echo "✓ Updated main.go (using oapi-codegen server)"

# Update go.mod to include dependencies
if [ ! -f go.mod ]; then
    go mod init backend
fi

echo "Installing dependencies..."
go get github.com/oapi-codegen/runtime
go get github.com/go-chi/chi/v5
go mod tidy

echo ""
echo "✅ API interface generation complete!"
echo ""
echo "Generated files:"
echo "  - models_gen.go  (Generated types from OpenAPI spec)"
echo "  - server_gen.go  (Generated server interface and routing)"
echo "  - handlers.go    (Implementation of ServerInterface)"
echo "  - store.go       (In-memory data store)"
echo "  - main.go        (Application entry point with chi router)"
echo ""
echo "To run the server:"
echo "  go run ."
echo ""
echo "Available endpoints:"
echo "  GET    /health           - Health check"
echo "  GET    /api/tasks        - Get all tasks"
echo "  POST   /api/tasks        - Create a task"
echo "  GET    /api/tasks/{id}   - Get a task by ID"
echo "  PUT    /api/tasks/{id}   - Update a task"
echo "  DELETE /api/tasks/{id}   - Delete a task"
echo ""

