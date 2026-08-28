package com.taskmanager.dto;

import com.taskmanager.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message = "task must not be blank")
        @Schema(description = "任務名稱，不可為空白", example = "寫週報")
        String title,
        String description,
        boolean completed
) {
    /** The current state of a task in request shape; the base a merge patch is applied onto. */
    public static TaskRequest from(Task task) {
        return new TaskRequest(task.getTitle(), task.getDescription(), task.isCompleted());
    }
}
