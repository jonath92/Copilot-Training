package com.copilot.taskapi.task.dto;

/**
 * DTO for error responses as defined in the API specification.
 * Provides consistent error response format across all endpoints.
 * 
 * @param message Error message describing what went wrong
 * @param code    Optional error code for programmatic handling
 */
public record ErrorResponse(
    String message,
    String code
) {
    /**
     * Creates an error response with just a message (no code).
     * 
     * @param message Error message
     */
    public ErrorResponse(String message) {
        this(message, null);
    }
}
