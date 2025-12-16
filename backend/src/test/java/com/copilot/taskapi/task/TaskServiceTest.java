package com.copilot.taskapi.task;

import com.copilot.taskapi.task.dto.TaskRequest;
import com.copilot.taskapi.task.dto.TaskResponse;
import com.copilot.taskapi.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TaskService.
 * Uses Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskMapper taskMapper;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
        taskService = new TaskService(taskRepository, taskMapper);
    }

    @Test
    @DisplayName("getAllTasks returns all tasks")
    void getAllTasks_ReturnsAllTasks() {
        // Given
        Task task1 = createTask(1L, "Task 1", "Description 1", 60);
        Task task2 = createTask(2L, "Task 2", "Description 2", 120);
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        // When
        List<TaskResponse> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).title()).isEqualTo("Task 1");
        assertThat(result.get(0).estimatedTime()).isEqualTo(1.0); // 60 min = 1 hour
        assertThat(result.get(1).id()).isEqualTo("2");
        assertThat(result.get(1).estimatedTime()).isEqualTo(2.0); // 120 min = 2 hours
    }

    @Test
    @DisplayName("getTaskById returns task when found")
    void getTaskById_WhenTaskExists_ReturnsTask() {
        // Given
        Task task = createTask(1L, "Task 1", "Description 1", 90);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        TaskResponse result = taskService.getTaskById(1L);

        // Then
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("Task 1");
        assertThat(result.estimatedTime()).isEqualTo(1.5); // 90 min = 1.5 hours
    }

    @Test
    @DisplayName("getTaskById throws exception when not found")
    void getTaskById_WhenTaskNotExists_ThrowsException() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.getTaskById(1L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("Task not found with id: 1");
    }

    @Test
    @DisplayName("createTask creates and returns new task")
    void createTask_CreatesAndReturnsTask() {
        // Given
        TaskRequest request = new TaskRequest("New Task", "New Description", 2.5);
        Task savedTask = createTask(1L, "New Task", "New Description", 150);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskResponse result = taskService.createTask(request);

        // Then
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("New Task");
        assertThat(result.estimatedTime()).isEqualTo(2.5); // 150 min = 2.5 hours
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask updates existing task")
    void updateTask_WhenTaskExists_UpdatesAndReturnsTask() {
        // Given
        Task existingTask = createTask(1L, "Old Title", "Old Desc", 60);
        Task updatedTask = createTask(1L, "Updated Title", "Updated Desc", 120);
        TaskRequest request = new TaskRequest("Updated Title", "Updated Desc", 2.0);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        TaskResponse result = taskService.updateTask(1L, request);

        // Then
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("Updated Title");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask throws exception when not found")
    void updateTask_WhenTaskNotExists_ThrowsException() {
        // Given
        TaskRequest request = new TaskRequest("Title", "Desc", 1.0);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.updateTask(1L, request))
            .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteTask deletes existing task")
    void deleteTask_WhenTaskExists_DeletesTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTask throws exception when not found")
    void deleteTask_WhenTaskNotExists_ThrowsException() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> taskService.deleteTask(1L))
            .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).deleteById(any());
    }

    /**
     * Helper method to create a Task for testing.
     */
    private Task createTask(Long id, String title, String description, int durationMinutes) {
        Task task = new Task(title, description, durationMinutes);
        task.setId(id);
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return task;
    }
}
