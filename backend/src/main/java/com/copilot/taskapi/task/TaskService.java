package com.copilot.taskapi.task;

import com.copilot.taskapi.task.dto.TaskRequest;
import com.copilot.taskapi.task.dto.TaskResponse;
import com.copilot.taskapi.task.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Task business logic.
 * Handles CRUD operations for tasks and encapsulates business rules.
 * 
 * This service is stateless and uses constructor injection for dependencies.
 * All methods work with DTOs to avoid exposing repository entities directly.
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Constructor injection for required dependencies.
     * 
     * @param taskRepository Repository for task persistence
     * @param taskMapper     Mapper for DTO conversions
     */
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Retrieves all tasks from the database.
     * 
     * @return List of all tasks as TaskResponse DTOs
     */
    public List<TaskResponse> getAllTasks() {
        logger.debug("Retrieving all tasks");
        List<Task> tasks = taskRepository.findAll();
        logger.info("Found {} tasks", tasks.size());
        return tasks.stream()
            .map(taskMapper::toResponse)
            .toList();
    }

    /**
     * Retrieves a specific task by its ID.
     * 
     * @param id The task ID
     * @return The task as a TaskResponse DTO
     * @throws TaskNotFoundException if the task is not found
     */
    public TaskResponse getTaskById(Long id) {
        logger.debug("Retrieving task with id: {}", id);
        Task task = findTaskOrThrow(id);
        return taskMapper.toResponse(task);
    }

    /**
     * Creates a new task.
     * 
     * @param request The task creation request
     * @return The created task as a TaskResponse DTO
     */
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        logger.debug("Creating new task with title: {}", request.title());
        Task task = taskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);
        logger.info("Created task with id: {}", savedTask.getId());
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Updates an existing task.
     * 
     * @param id      The task ID to update
     * @param request The task update request
     * @return The updated task as a TaskResponse DTO
     * @throws TaskNotFoundException if the task is not found
     */
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        logger.debug("Updating task with id: {}", id);
        Task existingTask = findTaskOrThrow(id);
        taskMapper.updateEntity(existingTask, request);
        Task savedTask = taskRepository.save(existingTask);
        logger.info("Updated task with id: {}", savedTask.getId());
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Deletes a task by its ID.
     * 
     * @param id The task ID to delete
     * @throws TaskNotFoundException if the task is not found
     */
    @Transactional
    public void deleteTask(Long id) {
        logger.debug("Deleting task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        logger.info("Deleted task with id: {}", id);
    }

    /**
     * Helper method to find a task or throw an exception if not found.
     * 
     * @param id The task ID
     * @return The found Task entity
     * @throws TaskNotFoundException if the task is not found
     */
    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
