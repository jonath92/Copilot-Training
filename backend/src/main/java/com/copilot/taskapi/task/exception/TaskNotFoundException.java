package com.copilot.taskapi.task.exception;

/**
 * Exception thrown when a task is not found in the database.
 * This is a runtime exception that will be handled by the global exception handler.
 */
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    /**
     * Creates a new TaskNotFoundException for the given task ID.
     * 
     * @param taskId The ID of the task that was not found
     */
    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
        this.taskId = taskId;
    }

    /**
     * Gets the ID of the task that was not found.
     * 
     * @return The task ID
     */
    public Long getTaskId() {
        return taskId;
    }
}
