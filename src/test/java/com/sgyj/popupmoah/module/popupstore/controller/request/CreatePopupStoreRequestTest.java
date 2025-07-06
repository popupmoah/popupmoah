package com.sgyj.popupmoah.module.popupstore.controller.request;

import com.sgyj.popupmoah.infra.jpa.Location;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePopupStoreRequestTest {

    @Test
    @DisplayName("mapToEntity maps all fields correctly")
    void mapToEntity_mapsAllFieldsCorrectly() {
        // given
        CreatePopupStoreRequest request = new CreatePopupStoreRequestTestHelper().create();

        // when
        PopupStore entity = request.mapToEntity();

        // then
        assertThat(entity.getName()).isEqualTo("Test Store");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getStartDate()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
        assertThat(entity.getEndDate()).isEqualTo(LocalDateTime.of(2024, 6, 10, 20, 0));
        assertThat(entity.getReservationDate()).isEqualTo(LocalDateTime.of(2024, 5, 30, 9, 0));
        assertThat(entity.getLocation()).isEqualTo(Location.of(37.1234, 127.5678));
        assertThat(entity.getStoreNumber()).isEqualTo("010-1234-5678");
        assertThat(entity.getEmail()).isEqualTo("test@popup.com");
        assertThat(entity.getWebsite()).isEqualTo("https://popup.com");
    }

    // 테스트용 request 객체 생성을 위한 헬퍼 클래스
    static class CreatePopupStoreRequestTestHelper extends CreatePopupStoreRequest {
        CreatePopupStoreRequest create() {
            CreatePopupStoreRequest req = new CreatePopupStoreRequest();
            setField(req, "popupStoreName", "Test Store");
            setField(req, "popupStoreDescription", "Test Description");
            setField(req, "startDate", LocalDateTime.of(2024, 6, 1, 10, 0));
            setField(req, "endDate", LocalDateTime.of(2024, 6, 10, 20, 0));
            setField(req, "reservationDate", LocalDateTime.of(2024, 5, 30, 9, 0));
            setField(req, "latitude", 37.1234);
            setField(req, "longitude", 127.5678);
            setField(req, "storeNumber", "010-1234-5678");
            setField(req, "email", "test@popup.com");
            setField(req, "website", "https://popup.com");
            return req;
        }
        private void setField(Object target, String fieldName, Object value) {
            try {
                var field = CreatePopupStoreRequest.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}