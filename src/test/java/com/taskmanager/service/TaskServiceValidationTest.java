// [C practice] proves the service-layer validation the MCP tools rely on
package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Autowires the Spring-proxied TaskService, which is the exact bean the MCP tools in
 * com.taskmanager.mcp call. If create() rejects a blank title here, it rejects it on the MCP
 * path too, independent of the REST controller's @Valid. Uses the full context (not a Mockito
 * mock) so the @Validated method-validation proxy is in effect.
 */
@SpringBootTest
class TaskServiceValidationTest {

    @Autowired
    private TaskService taskService;

    @Test
    void createRejectsBlankTitleAtTheServiceLayer() {
        assertThatThrownBy(() -> taskService.create(new TaskRequest("   ", "desc", false)))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("must not be blank");
    }
}
