// [AI assisted 001, 005]
package com.taskmanager.controller;

import com.fasterxml.jackson.databind.JsonNode;
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

    /** Media type registered by RFC 7396 for JSON Merge Patch documents. */
    public static final String MERGE_PATCH_JSON = "application/merge-patch+json";

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
    @Operation(summary = "Replace an existing task (all fields required)")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return TaskResponse.from(taskService.update(id, request));
    }

    @PatchMapping(path = "/{id}", consumes = MERGE_PATCH_JSON)
    @Operation(
            summary = "Partially update a task",
            description = "JSON Merge Patch (RFC 7396). Send only the fields to change, e.g. "
                    + "{\"completed\": true}. Omitted fields keep their current value; "
                    + "\"description\": null clears the description.")
    public TaskResponse patch(@PathVariable Long id, @RequestBody JsonNode patch) {
        return TaskResponse.from(taskService.patch(id, patch));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
