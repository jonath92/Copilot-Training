package com.copilot.taskapi.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Custom health controller providing additional health check endpoints.
 * This supplements the Spring Actuator health endpoints with application-specific checks.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    /**
     * Simple liveness check endpoint.
     * Returns 200 OK if the application is running.
     *
     * @return health status response
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        logger.debug("Health check requested");
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", Instant.now().toString()
        ));
    }

    /**
     * Readiness check endpoint.
     * Indicates whether the application is ready to accept traffic.
     *
     * @return readiness status response
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        logger.debug("Readiness check requested");
        // Add additional readiness checks here (e.g., database connectivity)
        return ResponseEntity.ok(Map.of(
            "status", "READY",
            "timestamp", Instant.now().toString()
        ));
    }

    /**
     * Liveness check endpoint.
     * Indicates whether the application is alive and should not be restarted.
     *
     * @return liveness status response
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> live() {
        logger.debug("Liveness check requested");
        return ResponseEntity.ok(Map.of(
            "status", "ALIVE",
            "timestamp", Instant.now().toString()
        ));
    }
}
