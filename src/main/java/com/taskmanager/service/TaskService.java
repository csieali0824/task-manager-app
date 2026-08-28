// [AI assisted 001, 005]
package com.taskmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.exception.InvalidPatchException;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.repository.TaskRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public TaskService(TaskRepository taskRepository, ObjectMapper objectMapper, Validator validator) {
        this.taskRepository = taskRepository;
        this.objectMapper = objectMapper;
        this.validator = validator;
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
        apply(task, request);
        return taskRepository.save(task);
    }

    public Task update(Long id, TaskRequest request) {
        Task task = findById(id);
        apply(task, request);
        return taskRepository.save(task);
    }

    /**
     * JSON Merge Patch (RFC 7396): fields present in {@code patch} replace the current value,
     * fields absent are left alone, and an explicit {@code null} clears the field. The merged
     * result is validated with the same constraints as a full TaskRequest, so a patch cannot
     * blank out the title.
     */
    public Task patch(Long id, JsonNode patch) {
        Task task = findById(id);
        TaskRequest merged = merge(TaskRequest.from(task), patch);

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(merged);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        apply(task, merged);
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

    private TaskRequest merge(TaskRequest current, JsonNode patch) {
        if (patch == null || !patch.isObject()) {
            throw new InvalidPatchException("Merge patch document must be a JSON object");
        }

        // Start from the current representation and overlay every field the patch mentions.
        // For a flat resource that is the whole merge algorithm; an explicit null survives
        // setAll as NullNode and clears the field on deserialization.
        ObjectNode merged = objectMapper.valueToTree(current);
        merged.setAll((ObjectNode) patch);

        try {
            return objectMapper.readerFor(TaskRequest.class)
                    // Reject fields the resource does not have (id, createdAt, typos) instead
                    // of silently dropping them.
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    // "completed": null would otherwise coerce to false; the field is not
                    // nullable, so clearing it is a client error.
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue(merged);
        } catch (JsonProcessingException e) {
            throw new InvalidPatchException(e.getOriginalMessage());
        } catch (IOException e) {
            throw new InvalidPatchException(e.getMessage());
        }
    }
}
