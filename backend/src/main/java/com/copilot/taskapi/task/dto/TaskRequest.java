package com.copilot.taskapi.task.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for Task creation and update requests.
 * Uses JSR-380 validation annotations for input validation.
 * 
 * The estimatedTime is provided in hours as per the API specification
 * and will be converted to minutes for internal storage.
 * 
 * @param title         Title of the task (required, 1-255 characters)
 * @param description   Detailed description of the task (required)
 * @param estimatedTime Estimated time to complete in hours (required, >= 0)
 */
public record TaskRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title,
    
    @NotNull(message = "Description is required")
    String description,
    
    @NotNull(message = "Estimated time is required")
    @Min(value = 0, message = "Estimated time must be non-negative")
    Double estimatedTime
) {
}
