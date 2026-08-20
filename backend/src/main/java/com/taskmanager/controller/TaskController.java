// [AI assisted 001]
package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List all tasks")
    public List<TaskResponse> findAll() {
        return taskService.findAll().stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single task by id")
    public TaskResponse findById(@PathVariable Long id) {
        return TaskResponse.from(taskService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        Task created = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return TaskResponse.from(taskService.update(id, request));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a task as completed")
    public TaskResponse markCompleted(@PathVariable Long id) {
        return TaskResponse.from(taskService.setCompleted(id, true));
    }

    @PatchMapping("/{id}/incomplete")
    @Operation(summary = "Mark a task as not completed")
    public TaskResponse markIncomplete(@PathVariable Long id) {
        return TaskResponse.from(taskService.setCompleted(id, false));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}