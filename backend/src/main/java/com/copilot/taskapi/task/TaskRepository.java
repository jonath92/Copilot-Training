package com.copilot.taskapi.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Task entity.
 * Provides CRUD operations and query methods for tasks.
 * 
 * Using Spring Data JPA ensures parameterized queries to prevent SQL injection.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Spring Data JPA provides standard CRUD operations out of the box.
    // Custom query methods can be added here as needed.
}
