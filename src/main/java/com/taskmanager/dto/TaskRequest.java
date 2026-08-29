package com.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message = "task must not be blank")
        @Schema(description = "任務名稱，不可為空白", example = "寫週報")
        String title,
        String description,
        boolean completed
) {
}
