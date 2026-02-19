package com.devaloi.springapi.service;

import com.devaloi.springapi.dto.CreateTaskRequest;
import com.devaloi.springapi.dto.TaskResponse;
import com.devaloi.springapi.dto.UpdateTaskRequest;
import com.devaloi.springapi.entity.Task;
import com.devaloi.springapi.entity.TaskPriority;
import com.devaloi.springapi.entity.TaskStatus;
import com.devaloi.springapi.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void create_savesAndReturnsTask() {
        CreateTaskRequest request = new CreateTaskRequest("Test task", "Description", null, null, null);
        Task saved = createTask(1L, "Test task", TaskStatus.TODO, TaskPriority.MEDIUM);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse response = taskService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Test task");
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void create_withExplicitStatusAndPriority() {
        CreateTaskRequest request = new CreateTaskRequest("Urgent", null, TaskStatus.IN_PROGRESS, TaskPriority.HIGH, null);
        Task saved = createTask(1L, "Urgent", TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse response = taskService.create(request);

        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.priority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    void getById_returnsTask() {
        Task task = createTask(1L, "Found task", TaskStatus.TODO, TaskPriority.LOW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Found task");
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_returnsPaginatedResults() {
        Task task = createTask(1L, "Task", TaskStatus.TODO, TaskPriority.MEDIUM);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Task> page = new PageImpl<>(List.of(task), pageable, 1);
        when(taskRepository.findWithFilters(null, null, null, pageable)).thenReturn(page);

        Page<TaskResponse> result = taskService.getAll(null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Task");
    }

    @Test
    void update_modifiesAndReturnsTask() {
        Task existing = createTask(1L, "Old title", TaskStatus.TODO, TaskPriority.LOW);
        Task updated = createTask(1L, "New title", TaskStatus.DONE, TaskPriority.LOW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        UpdateTaskRequest request = new UpdateTaskRequest("New title", null, TaskStatus.DONE, null, null);
        TaskResponse response = taskService.update(1L, request);

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        UpdateTaskRequest request = new UpdateTaskRequest("Title", null, null, null, null);

        assertThatThrownBy(() -> taskService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_removesTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Task createTask(Long id, String title, TaskStatus status, TaskPriority priority) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setStatus(status);
        task.setPriority(priority);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }
}
