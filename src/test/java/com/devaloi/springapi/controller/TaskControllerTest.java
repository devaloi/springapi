package com.devaloi.springapi.controller;

import com.devaloi.springapi.config.SecurityConfig;
import com.devaloi.springapi.dto.CreateTaskRequest;
import com.devaloi.springapi.dto.TaskResponse;
import com.devaloi.springapi.dto.UpdateTaskRequest;
import com.devaloi.springapi.entity.TaskPriority;
import com.devaloi.springapi.entity.TaskStatus;
import com.devaloi.springapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void getAll_returnsPageOfTasks() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Test", "Desc", TaskStatus.TODO, TaskPriority.MEDIUM, null, now, now);
        Page<TaskResponse> page = new PageImpl<>(List.of(task));
        when(taskService.getAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getById_returnsTask() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Found", null, TaskStatus.TODO, TaskPriority.LOW, null, now, now);
        when(taskService.getById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Found"));
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        when(taskService.getById(99L)).thenThrow(new EntityNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    @Test
    @WithMockUser
    void create_returns201WithLocation() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("New task", "Description", null, null, null);
        TaskResponse response = new TaskResponse(1L, "New task", "Description", TaskStatus.TODO, TaskPriority.MEDIUM, null, now, now);
        when(taskService.create(any(CreateTaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.title").value("New task"));
    }

    @Test
    @WithMockUser
    void create_returns400ForBlankTitle() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("", null, null, null, null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void create_returns401WithoutAuth() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("Task", null, null, null, null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void update_returnsUpdatedTask() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest("Updated", null, TaskStatus.DONE, null, null);
        TaskResponse response = new TaskResponse(1L, "Updated", null, TaskStatus.DONE, TaskPriority.MEDIUM, null, now, now);
        when(taskService.update(eq(1L), any(UpdateTaskRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @WithMockUser
    void delete_returns204() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_returns404WhenNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Task not found with id: 99")).when(taskService).delete(99L);

        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }
}
