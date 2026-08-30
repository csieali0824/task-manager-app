// [C practice — Spring AI MCP server]
package com.taskmanager.mcp;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the task operations as MCP tools. Each @Tool method becomes a tool an MCP client
 * (such as Claude) can call by name. Reuses the same TaskService the REST controller uses, so
 * validation and not-found handling behave identically.
 */
@Service
public class TaskTools {

    private final TaskService taskService;

    public TaskTools(TaskService taskService) {
        this.taskService = taskService;
    }

    /** Trimmed view without the Instant timestamps, which keeps tool results simple to serialize. */
    public record TaskView(Long id, String title, String description, boolean completed) {
        static TaskView from(Task task) {
            return new TaskView(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
        }
    }

    @Tool(description = "List every task with its id, title, description and completion state")
    public List<TaskView> listTasks() {
        return taskService.findAll().stream().map(TaskView::from).toList();
    }

    @Tool(description = "Create a new task and return it")
    public TaskView createTask(
            @ToolParam(description = "The task title, must not be blank") String title,
            @ToolParam(description = "Optional longer description", required = false) String description) {
        return TaskView.from(taskService.create(new TaskRequest(title, description, false)));
    }

    @Tool(description = "Mark a task complete or incomplete by id")
    public TaskView setTaskCompleted(
            @ToolParam(description = "The id of the task to change") Long id,
            @ToolParam(description = "true to mark complete, false to mark incomplete") boolean completed) {
        Task current = taskService.findById(id);
        Task updated = taskService.update(id, new TaskRequest(current.getTitle(), current.getDescription(), completed));
        return TaskView.from(updated);
    }

    @Tool(description = "Delete a task by id")
    public String deleteTask(@ToolParam(description = "The id of the task to delete") Long id) {
        taskService.delete(id);
        return "Deleted task " + id;
    }
}
