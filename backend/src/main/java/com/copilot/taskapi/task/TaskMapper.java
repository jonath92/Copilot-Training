package com.copilot.taskapi.task;

import com.copilot.taskapi.task.dto.TaskRequest;
import com.copilot.taskapi.task.dto.TaskResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between Task entities and DTOs.
 * Handles the conversion between internal minutes representation
 * and external hours representation for estimated time.
 */
@Component
public class TaskMapper {

    private static final double MINUTES_PER_HOUR = 60.0;

    /**
     * Converts a Task entity to a TaskResponse DTO.
     * Duration is converted from minutes to hours.
     * 
     * @param task The task entity to convert
     * @return The corresponding TaskResponse DTO
     */
    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId().toString(),
            task.getTitle(),
            task.getDescription(),
            task.getDuration() / MINUTES_PER_HOUR  // Convert minutes to hours
        );
    }

    /**
     * Creates a new Task entity from a TaskRequest DTO.
     * Estimated time is converted from hours to minutes.
     * 
     * @param request The task request DTO
     * @return A new Task entity
     */
    public Task toEntity(TaskRequest request) {
        return new Task(
            request.title(),
            request.description(),
            convertHoursToMinutes(request.estimatedTime())
        );
    }

    /**
     * Updates an existing Task entity with data from a TaskRequest DTO.
     * 
     * @param task    The existing task entity to update
     * @param request The task request DTO with new data
     */
    public void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDuration(convertHoursToMinutes(request.estimatedTime()));
    }

    /**
     * Converts hours to minutes, rounding to nearest integer.
     * 
     * @param hours Time in hours
     * @return Time in minutes
     */
    private Integer convertHoursToMinutes(Double hours) {
        return (int) Math.round(hours * MINUTES_PER_HOUR);
    }
}
