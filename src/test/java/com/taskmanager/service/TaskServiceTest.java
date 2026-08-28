// [AI assisted 004, 005]
package com.taskmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.exception.InvalidPatchException;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.repository.TaskRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // Constructor injection means the service needs no Spring context to build. The mapper
        // and validator are the real ones: the merge and the constraints are what's under test.
        taskService = new TaskService(taskRepository, MAPPER, VALIDATOR);
    }

    @Test
    void findAllDelegatesToRepository() {
        when(taskRepository.findAll()).thenReturn(List.of(task("A"), task("B")));

        assertThat(taskService.findAll()).hasSize(2);
    }

    @Test
    void findByIdReturnsTaskWhenPresent() {
        Task existing = task("Write README");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThat(taskService.findById(1L)).isSameAs(existing);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createCopiesRequestFieldsOntoNewTask() {
        when(taskRepository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        taskService.create(new TaskRequest("Buy milk", "Low fat", false));

        Task saved = captureSavedTask();
        assertThat(saved.getTitle()).isEqualTo("Buy milk");
        assertThat(saved.getDescription()).isEqualTo("Low fat");
        assertThat(saved.isCompleted()).isFalse();
    }

    @Test
    void updateOverwritesFieldsOfExistingTask() {
        Task existing = task("Old title");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        taskService.update(1L, new TaskRequest("New title", "New description", true));

        Task saved = captureSavedTask();
        assertThat(saved).isSameAs(existing);
        assertThat(saved.getTitle()).isEqualTo("New title");
        assertThat(saved.getDescription()).isEqualTo("New description");
        assertThat(saved.isCompleted()).isTrue();
    }

    @Test
    void updateThrowsWhenTaskMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(999L, new TaskRequest("x", null, false)))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    // ---- patch: JSON Merge Patch semantics -------------------------------------------------

    @Test
    void patchChangesOnlyTheFieldsPresentInTheDocument() {
        Task existing = task("Keep this title");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        taskService.patch(1L, json("{\"completed\": true}"));

        Task saved = captureSavedTask();
        assertThat(saved).isSameAs(existing);
        assertThat(saved.isCompleted()).isTrue();
        assertThat(saved.getTitle()).isEqualTo("Keep this title");
        assertThat(saved.getDescription()).isEqualTo("description");
    }

    @Test
    void patchWithNullClearsANullableField() {
        Task existing = task("Title stays");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        taskService.patch(1L, json("{\"description\": null}"));

        Task saved = captureSavedTask();
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.getTitle()).isEqualTo("Title stays");
    }

    @Test
    void patchRejectsBlankTitleAndDoesNotSave() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task("Was fine")));

        assertThatThrownBy(() -> taskService.patch(1L, json("{\"title\": \"   \"}")))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("must not be blank");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void patchRejectsUnknownFields() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task("x")));

        assertThatThrownBy(() -> taskService.patch(1L, json("{\"id\": 42}")))
                .isInstanceOf(InvalidPatchException.class)
                .hasMessageContaining("id");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void patchRejectsNullForNonNullableField() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task("x")));

        assertThatThrownBy(() -> taskService.patch(1L, json("{\"completed\": null}")))
                .isInstanceOf(InvalidPatchException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void patchRejectsDocumentThatIsNotAnObject() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task("x")));

        assertThatThrownBy(() -> taskService.patch(1L, json("[{\"op\": \"replace\"}]")))
                .isInstanceOf(InvalidPatchException.class)
                .hasMessageContaining("JSON object");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void patchThrowsWhenTaskMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.patch(999L, json("{\"completed\": true}")))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    // ---- delete --------------------------------------------------------------------------

    @Test
    void deleteRemovesTaskFoundById() {
        Task existing = task("Doomed");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        taskService.delete(1L);

        verify(taskRepository).delete(existing);
    }

    @Test
    void deleteThrowsWhenTaskMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(999L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }

    private Task captureSavedTask() {
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        return captor.getValue();
    }

    private Task task(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("description");
        task.setCompleted(false);
        return task;
    }

    private static JsonNode json(String text) {
        try {
            return MAPPER.readTree(text);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad test JSON: " + text, e);
        }
    }
}
