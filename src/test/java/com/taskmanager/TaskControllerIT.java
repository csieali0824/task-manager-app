package com.taskmanager;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
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

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
