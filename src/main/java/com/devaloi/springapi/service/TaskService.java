package com.devaloi.springapi.service;

import com.devaloi.springapi.dto.CreateTaskRequest;
import com.devaloi.springapi.dto.TaskResponse;
import com.devaloi.springapi.dto.UpdateTaskRequest;
import com.devaloi.springapi.entity.TaskPriority;
import com.devaloi.springapi.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse create(CreateTaskRequest request);

    TaskResponse getById(Long id);

    Page<TaskResponse> getAll(TaskStatus status, TaskPriority priority, String search, Pageable pageable);

    TaskResponse update(Long id, UpdateTaskRequest request);

    void delete(Long id);
}
