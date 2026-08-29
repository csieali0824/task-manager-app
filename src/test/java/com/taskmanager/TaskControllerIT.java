// [AI assisted 001, 004]
package com.taskmanager;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndFetchTask() {
        TaskRequest request = new TaskRequest("Write README", "Explain OpenAPI access", false);

        ResponseEntity<TaskResponse> createResponse = restTemplate.postForEntity(
                url("/api/tasks"), request, TaskResponse.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> listResponse = restTemplate.getForEntity(url("/api/tasks"), String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Write README");
    }

    /** The UI's checkbox is a PUT of the whole task with completed flipped; this is that call. */
    @Test
    void putReplacesTheTask() {
        ResponseEntity<TaskResponse> created = restTemplate.postForEntity(
                url("/api/tasks"), new TaskRequest("Toggle me", "unchanged", false), TaskResponse.class);
        Long id = created.getBody().id();

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
                url("/api/tasks/" + id), HttpMethod.PUT,
                new HttpEntity<>(new TaskRequest("Toggle me", "unchanged", true)), TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.completed()).isTrue();
        assertThat(body.title()).isEqualTo("Toggle me");
        assertThat(body.description()).isEqualTo("unchanged");
    }

    /**
     * TaskNotFoundException carries no @ResponseStatus, so without ApiExceptionHandler this
     * would surface as 500 with Spring Boot's default error body (which always includes "path"
     * and omits "message"). The 404 plus the custom body shape is what proves the handler ran.
     */
    @Test
    void returns404WhenTaskNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/tasks/999999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .contains("\"status\":404")
                .contains("Task not found with id: 999999")
                .doesNotContain("\"path\"");
    }

    /**
     * Spring maps MethodArgumentNotValidException to 400 on its own, so only the
     * "field: message" body built by ApiExceptionHandler proves the handler ran.
     */
    @Test
    void returns400WhenTitleIsBlank() {
        TaskRequest request = new TaskRequest("   ", "title is only whitespace", false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/tasks"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .contains("\"status\":400")
                .contains("title:")
                .contains("must not be blank")
                .doesNotContain("\"path\"");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
