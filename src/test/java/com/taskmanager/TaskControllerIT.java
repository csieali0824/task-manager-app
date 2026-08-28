// [AI assisted 001, 004, 005]
package com.taskmanager;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIT {

    private static final MediaType MERGE_PATCH_JSON = MediaType.valueOf("application/merge-patch+json");

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

    // ---- PATCH /api/tasks/{id} as JSON Merge Patch -----------------------------------------

    @Test
    void mergePatchChangesOnlyTheGivenField() {
        TaskResponse created = create("Patch me", "keep this description");

        ResponseEntity<TaskResponse> response = mergePatch(created.id(), "{\"completed\": true}", TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.completed()).isTrue();
        assertThat(body.title()).isEqualTo("Patch me");
        assertThat(body.description()).isEqualTo("keep this description");
    }

    /**
     * The merged result goes through the same constraints as a full request, but via the
     * Validator inside the service rather than @Valid, so it surfaces as
     * ConstraintViolationException and exercises that handler instead.
     */
    @Test
    void mergePatchReturns400WhenTitleWouldBecomeBlank() {
        TaskResponse created = create("Still valid", null);

        ResponseEntity<String> response = mergePatch(created.id(), "{\"title\": \"  \"}", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .contains("\"status\":400")
                .contains("title:")
                .contains("must not be blank")
                .doesNotContain("\"path\"");
    }

    @Test
    void mergePatchReturns400ForUnknownField() {
        TaskResponse created = create("No such field", null);

        ResponseEntity<String> response = mergePatch(created.id(), "{\"owner\": \"me\"}", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("owner");
    }

    /**
     * RFC 5789: the patch format is identified by the request's media type. A plain
     * application/json body is not declared as a merge patch, so the endpoint does not accept it.
     */
    @Test
    void mergePatchReturns415ForPlainJsonContentType() {
        TaskResponse created = create("Wrong media type", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/tasks/" + created.id()), HttpMethod.PATCH,
                new HttpEntity<>("{\"completed\": true}", headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private TaskResponse create(String title, String description) {
        ResponseEntity<TaskResponse> response = restTemplate.postForEntity(
                url("/api/tasks"), new TaskRequest(title, description, false), TaskResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TaskResponse body = response.getBody();
        assertThat(body).isNotNull();
        return body;
    }

    private <T> ResponseEntity<T> mergePatch(Long id, String document, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MERGE_PATCH_JSON);
        return restTemplate.exchange(
                url("/api/tasks/" + id), HttpMethod.PATCH, new HttpEntity<>(document, headers), responseType);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
