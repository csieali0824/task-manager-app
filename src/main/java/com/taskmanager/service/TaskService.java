// [AI assisted 001]
package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * {@code @Validated} plus {@code @Valid} on the write methods enforces the TaskRequest
 * constraints here in the service, so every caller is validated, not only the REST controller.
 * Without this, non-HTTP entry points (the MCP tools in {@code com.taskmanager.mcp}) would skip
 * the controller's {@code @Valid} and could persist a blank title.
 */
@Service
@Validated
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(@Valid TaskRequest request) {
        Task task = new Task();
        apply(task, request);
        return taskRepository.save(task);
    }

    /** Full replacement: every field of the task is overwritten from the request. */
    public Task update(Long id, @Valid TaskRequest request) {
        Task task = findById(id);
        apply(task, request);
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    private static void apply(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());
    }
}
