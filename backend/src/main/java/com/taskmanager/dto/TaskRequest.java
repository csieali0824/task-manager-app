package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message = "title must not be blank") String title,
        String description,
        boolean completed
) {
}
