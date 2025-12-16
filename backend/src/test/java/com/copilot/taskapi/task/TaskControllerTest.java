package com.copilot.taskapi.task;

import com.copilot.taskapi.task.dto.TaskRequest;
import com.copilot.taskapi.task.dto.TaskResponse;
import com.copilot.taskapi.task.exception.GlobalExceptionHandler;
import com.copilot.taskapi.task.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for TaskController.
 * Tests REST endpoints using MockMvc.
 */
@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("GET /api/tasks returns all tasks")
    void getAllTasks_ReturnsAllTasks() throws Exception {
        // Given
        TaskResponse task1 = new TaskResponse("1", "Task 1", "Desc 1", 1.0);
        TaskResponse task2 = new TaskResponse("2", "Task 2", "Desc 2", 2.0);
        when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));

        // When/Then
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns task when found")
    void getTaskById_WhenExists_ReturnsTask() throws Exception {
        // Given
        TaskResponse task = new TaskResponse("1", "Task 1", "Description", 1.5);
        when(taskService.getTaskById(1L)).thenReturn(task);

        // When/Then
        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.title").value("Task 1"))
            .andExpect(jsonPath("$.estimatedTime").value(1.5));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns 404 when not found")
    void getTaskById_WhenNotExists_Returns404() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenThrow(new TaskNotFoundException(1L));

        // When/Then
        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /api/tasks creates task")
    void createTask_WithValidInput_ReturnsCreated() throws Exception {
        // Given
        TaskRequest request = new TaskRequest("New Task", "Description", 2.0);
        TaskResponse response = new TaskResponse("1", "New Task", "Description", 2.0);
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @DisplayName("POST /api/tasks returns 400 for invalid input")
    void createTask_WithInvalidInput_Returns400() throws Exception {
        // Given - empty title
        String invalidRequest = "{\"title\":\"\",\"description\":\"Desc\",\"estimatedTime\":1.0}";

        // When/Then
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} updates task")
    void updateTask_WithValidInput_ReturnsUpdated() throws Exception {
        // Given
        TaskRequest request = new TaskRequest("Updated", "Updated Desc", 3.0);
        TaskResponse response = new TaskResponse("1", "Updated", "Updated Desc", 3.0);
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} returns 404 when not found")
    void updateTask_WhenNotExists_Returns404() throws Exception {
        // Given
        TaskRequest request = new TaskRequest("Title", "Desc", 1.0);
        when(taskService.updateTask(eq(1L), any(TaskRequest.class)))
            .thenThrow(new TaskNotFoundException(1L));

        // When/Then
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} deletes task")
    void deleteTask_WhenExists_ReturnsNoContent() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} returns 404 when not found")
    void deleteTask_WhenNotExists_Returns404() throws Exception {
        // Given
        doThrow(new TaskNotFoundException(1L)).when(taskService).deleteTask(1L);

        // When/Then
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns 400 for invalid ID format")
    void getTaskById_WithInvalidIdFormat_Returns400() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/tasks/invalid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_ID_FORMAT"));
    }
}
