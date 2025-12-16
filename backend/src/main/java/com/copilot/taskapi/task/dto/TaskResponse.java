package com.copilot.taskapi.task.dto;

/**
 * DTO for Task API responses.
 * Uses a record for immutability and concise representation.
 * 
 * The estimatedTime is returned in hours as per the API specification,
 * converted from the internal minutes representation.
 * 
 * @param id            Unique identifier for the task (as String per API spec)
 * @param title         Title of the task
 * @param description   Detailed description of the task
 * @param estimatedTime Estimated time to complete in hours
 */
public record TaskResponse(
    String id,
    String title,
    String description,
    double estimatedTime
) {
}
