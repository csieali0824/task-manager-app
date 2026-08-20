// [AI assisted 001]
package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

    public Task create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());
        return taskRepository.save(task);
    }

    public Task update(Long id, TaskRequest request) {
        Task task = findById(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());
        return taskRepository.save(task);
    }

    public Task setCompleted(Long id, boolean completed) {
        Task task = findById(id);
        task.setCompleted(completed);
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }
}
