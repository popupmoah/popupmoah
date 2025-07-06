package com.sgyj.popupmoah.module.popupstore.controller;

import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreCreatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

// TODO 로그인 이후 시큐리티 설정 이후 진행해야 함.
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PopupStoreControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createPopupStore_shouldReturnCreatedResponse() {
        // given
        String json = """
        {
            "name": "Test Popup Store",
            "location": "Test Location"
        }
        """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // when
        ResponseEntity<PopupStoreCreatedResponse> response = restTemplate.postForEntity(
                "/api/popup-stores",
                request,
                PopupStoreCreatedResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Popup Store");
    }
}