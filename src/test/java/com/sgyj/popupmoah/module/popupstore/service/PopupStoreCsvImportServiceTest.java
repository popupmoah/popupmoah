package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PopupStoreCsvImportService의 CSV 임포트 기능 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class PopupStoreCsvImportServiceTest {

    @Autowired
    PopupStoreCsvImportService importService;

    @Autowired
    PopupStoreRepository popupStoreRepository;

    @Test
    @DisplayName("CSV 임포트: 정상적으로 팝업스토어가 저장된다")
    void importFromCsv_success() {
        // given
        String csvPath = "src/test/resources/test_popupstores.csv";

        // when
        importService.importFromCsv(csvPath);

        // then
        List<PopupStore> all = popupStoreRepository.findAll();
        assertThat(all).isNotEmpty();
        PopupStore store = all.get(0);
        assertThat(store.getName()).isEqualTo("테스트 팝업스토어");
        assertThat(store.getSourceUrl()).isEqualTo("https://test.com/1");
        assertThat(store.getStartDate()).isNotNull();
        assertThat(store.getEndDate()).isNotNull();
    }

    @Test
    @DisplayName("CSV 임포트: 중복 sourceUrl은 저장하지 않는다")
    void importFromCsv_duplicate() {
        // given
        String csvPath = "src/test/resources/test_popupstores.csv";
        importService.importFromCsv(csvPath); // 1차 임포트
        long count1 = popupStoreRepository.count();

        // when
        importService.importFromCsv(csvPath); // 2차 임포트(중복)

        // then
        long count2 = popupStoreRepository.count();
        assertThat(count2).isEqualTo(count1); // 중복 저장 없음
    }
} 