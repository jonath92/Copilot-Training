package models

import (
	"encoding/json"
	"time"

	"github.com/google/uuid"
	"github.com/lib/pq"
)

// TaskStatus represents the status of a task
type TaskStatus string

const (
	TaskStatusPending    TaskStatus = "pending"
	TaskStatusInProgress TaskStatus = "in_progress"
	TaskStatusCompleted  TaskStatus = "completed"
	TaskStatusCancelled  TaskStatus = "cancelled"
	TaskStatusArchived   TaskStatus = "archived"
)

// TaskPriority represents the priority of a task
type TaskPriority string

const (
	TaskPriorityLow    TaskPriority = "low"
	TaskPriorityMedium TaskPriority = "medium"
	TaskPriorityHigh   TaskPriority = "high"
	TaskPriorityUrgent TaskPriority = "urgent"
)

// Task represents a task in the system
type Task struct {
	ID             uuid.UUID        `json:"id" db:"id"`
	Title          string           `json:"title" db:"title" validate:"required,min=3,max=255"`
	Description    *string          `json:"description,omitempty" db:"description"`
	Duration       *int             `json:"duration,omitempty" db:"duration" validate:"omitempty,min=1,max=10080"` // in minutes
	Status         TaskStatus       `json:"status" db:"status"`
	Priority       TaskPriority     `json:"priority" db:"priority"`
	Tags           pq.StringArray   `json:"tags" db:"tags"`
	Metadata       json.RawMessage  `json:"metadata,omitempty" db:"metadata"`
	EstimatedStart *time.Time       `json:"estimated_start,omitempty" db:"estimated_start"`
	EstimatedEnd   *time.Time       `json:"estimated_end,omitempty" db:"estimated_end"`
	ActualStart    *time.Time       `json:"actual_start,omitempty" db:"actual_start"`
	ActualEnd      *time.Time       `json:"actual_end,omitempty" db:"actual_end"`
	CreatedAt      time.Time        `json:"created_at" db:"created_at"`
	UpdatedAt      time.Time        `json:"updated_at" db:"updated_at"`
	CreatedBy      string           `json:"created_by" db:"created_by"`
}

// CreateTaskRequest represents the request to create a task
type CreateTaskRequest struct {
	Title          string           `json:"title" validate:"required,min=3,max=255"`
	Description    *string          `json:"description,omitempty"`
	Duration       *int             `json:"duration,omitempty" validate:"omitempty,min=1,max=10080"`
	Priority       *TaskPriority    `json:"priority,omitempty"`
	Tags           []string         `json:"tags,omitempty"`
	Metadata       json.RawMessage  `json:"metadata,omitempty"`
	EstimatedStart *time.Time       `json:"estimated_start,omitempty"`
	EstimatedEnd   *time.Time       `json:"estimated_end,omitempty"`
}

// UpdateTaskRequest represents the request to update a task
type UpdateTaskRequest struct {
	Title          *string          `json:"title,omitempty" validate:"omitempty,min=3,max=255"`
	Description    *string          `json:"description,omitempty"`
	Duration       *int             `json:"duration,omitempty" validate:"omitempty,min=1,max=10080"`
	Status         *TaskStatus      `json:"status,omitempty"`
	Priority       *TaskPriority    `json:"priority,omitempty"`
	Tags           *[]string        `json:"tags,omitempty"`
	Metadata       json.RawMessage  `json:"metadata,omitempty"`
	EstimatedStart *time.Time       `json:"estimated_start,omitempty"`
	EstimatedEnd   *time.Time       `json:"estimated_end,omitempty"`
	ActualStart    *time.Time       `json:"actual_start,omitempty"`
	ActualEnd      *time.Time       `json:"actual_end,omitempty"`
}

// TasksResponse represents the response for listing tasks
type TasksResponse struct {
	Tasks []Task `json:"tasks"`
	Total int    `json:"total"`
	Page  int    `json:"page"`
	Limit int    `json:"limit"`
}

// ErrorResponse represents an error response
type ErrorResponse struct {
	Error   string      `json:"error"`
	Message string      `json:"message,omitempty"`
	Details interface{} `json:"details,omitempty"`
}

// SearchTasksResponse represents the response for task search
type SearchTasksResponse struct {
	TaskID     uuid.UUID `json:"task_id" db:"task_id"`
	Title      string    `json:"title" db:"title"`
	Description *string  `json:"description,omitempty" db:"description"`
	Similarity float32   `json:"similarity_score" db:"similarity_score"`
}