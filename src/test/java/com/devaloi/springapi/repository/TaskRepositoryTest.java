package com.devaloi.springapi.repository;

import com.devaloi.springapi.entity.Task;
import com.devaloi.springapi.entity.TaskPriority;
import com.devaloi.springapi.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        Task task1 = new Task();
        task1.setTitle("Write unit tests");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setPriority(TaskPriority.HIGH);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Deploy application");
        task2.setStatus(TaskStatus.TODO);
        task2.setPriority(TaskPriority.MEDIUM);
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setTitle("Write documentation");
        task3.setStatus(TaskStatus.TODO);
        task3.setPriority(TaskPriority.LOW);
        taskRepository.save(task3);
    }

    @Test
    void findByStatus_returnsMatchingTasks() {
        Page<Task> result = taskRepository.findByStatus(TaskStatus.TODO, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByPriority_returnsMatchingTasks() {
        Page<Task> result = taskRepository.findByPriority(TaskPriority.HIGH, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Write unit tests");
    }

    @Test
    void findWithFilters_byStatusAndPriority() {
        Page<Task> result = taskRepository.findWithFilters(
                TaskStatus.TODO, TaskPriority.MEDIUM, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Deploy application");
    }

    @Test
    void findWithFilters_bySearch() {
        Page<Task> result = taskRepository.findWithFilters(
                null, null, "write", PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findWithFilters_noFilters_returnsAll() {
        Page<Task> result = taskRepository.findWithFilters(
                null, null, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void save_setsTimestamps() {
        Task task = new Task();
        task.setTitle("New task");
        Task saved = taskRepository.save(task);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
