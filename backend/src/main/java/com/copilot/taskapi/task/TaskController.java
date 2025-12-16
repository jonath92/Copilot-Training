package com.copilot.taskapi.task;

import com.copilot.taskapi.task.dto.TaskRequest;
import com.copilot.taskapi.task.dto.TaskResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Task API endpoints.
 * Provides CRUD operations for tasks as specified in the OpenAPI specification.
 * 
 * All endpoints are mapped under /api/tasks.
 * Request validation is handled via JSR-380 annotations and BindingResult.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    /**
     * Constructor injection for the task service.
     * 
     * @param taskService Service for task business logic
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /api/tasks - Retrieve all tasks.
     * 
     * @return List of all tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        logger.debug("GET /api/tasks - Retrieving all tasks");
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/{id} - Retrieve a specific task by ID.
     * 
     * @param id The task ID (as string, will be parsed to Long)
     * @return The task if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        logger.debug("GET /api/tasks/{} - Retrieving task", id);
        Long taskId = Long.parseLong(id);
        TaskResponse task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    /**
     * POST /api/tasks - Create a new task.
     * 
     * @param request The task creation request (validated)
     * @return The created task with 201 Created status
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        logger.debug("POST /api/tasks - Creating new task");
        TaskResponse createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * PUT /api/tasks/{id} - Update an existing task.
     * 
     * @param id      The task ID to update
     * @param request The task update request (validated)
     * @return The updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        logger.debug("PUT /api/tasks/{} - Updating task", id);
        Long taskId = Long.parseLong(id);
        TaskResponse updatedTask = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * DELETE /api/tasks/{id} - Delete a task.
     * 
     * @param id The task ID to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        logger.debug("DELETE /api/tasks/{} - Deleting task", id);
        Long taskId = Long.parseLong(id);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
