package com.sgyj.popupmoah.module.popupstore.controller.rseponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PopupStoreCreatedResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("모든 필드를 올바르게 맵핑하는지 확인")
    void fromDto_mapsAllFieldsCorrectly() throws JsonProcessingException {
        // given
        String json = """
            {
              "id": 1,
              "name": "Test Popup Store",
              "description": "Description",
              "startDate": "2024-06-01",
              "endDate": "2024-06-10",
              "reservationDate": "2024-05-30"
            }
            """;
        PopupStoreDto dto = objectMapper.readValue(json, PopupStoreDto.class);

        // when
        PopupStoreCreatedResponse response = PopupStoreCreatedResponse.fromDto(dto);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Popup Store");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStartDate()).isEqualTo("2024-06-01");
        assertThat(response.getEndDate()).isEqualTo("2024-06-10");
        assertThat(response.getReservationDate()).isEqualTo("2024-05-30");
    }
}