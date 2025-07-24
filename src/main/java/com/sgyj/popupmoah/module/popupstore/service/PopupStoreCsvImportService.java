package com.sgyj.popupmoah.module.popupstore.service;

import com.opencsv.CSVReader;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * popupstores.csv 파일을 읽어 PopupStore 엔티티로 저장하는 서비스
 */
@Configuration
@EnableScheduling
class PopupStoreCsvImportSchedulerConfig {}

@Service
@RequiredArgsConstructor
public class PopupStoreCsvImportService {

    private final PopupStoreRepository popupStoreRepository;

    /**
     * popupstores.csv 파일을 읽어 신규 팝업스토어 정보를 저장합니다.
     * @param csvFilePath CSV 파일 경로 (예: "popupstores.csv")
     */
    @Transactional
    public void importFromCsv(String csvFilePath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext(); // 헤더 읽기
            if (header == null) return;
            int nameIdx = -1, descIdx = -1, imgIdx = -1, srcIdx = -1, catIdx = -1, startIdx = -1, endIdx = -1;
            // 헤더 인덱스 매핑
            for (int i = 0; i < header.length; i++) {
                switch (header[i]) {
                    case "name": nameIdx = i; break;
                    case "description": descIdx = i; break;
                    case "imageUrl": imgIdx = i; break;
                    case "sourceUrl": srcIdx = i; break;
                    case "category": catIdx = i; break;
                    case "startDate": startIdx = i; break;
                    case "endDate": endIdx = i; break;
                }
            }
            if (nameIdx == -1 || srcIdx == -1) return;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 이미 저장된 sourceUrl 목록 조회 (중복 방지)
            Set<String> existingSources = new HashSet<>(popupStoreRepository.findAllSourceUrls());
            String[] row;
            while ((row = reader.readNext()) != null) {
                String sourceUrl = row[srcIdx];
                if (existingSources.contains(sourceUrl)) continue; // 중복 skip
                LocalDate startDate = null, endDate = null;
                if (startIdx != -1 && row[startIdx] != null && !row[startIdx].isEmpty()) {
                    startDate = LocalDate.parse(row[startIdx], dateFormatter);
                }
                if (endIdx != -1 && row[endIdx] != null && !row[endIdx].isEmpty()) {
                    endDate = LocalDate.parse(row[endIdx], dateFormatter);
                }
                PopupStore popupStore = PopupStore.builder()
                        .name(row[nameIdx])
                        .description(descIdx != -1 ? row[descIdx] : null)
                        .imageUrl(imgIdx != -1 ? row[imgIdx] : null)
                        .sourceUrl(sourceUrl)
                        .category(catIdx != -1 ? row[catIdx] : null)
                        .startDate(startDate != null ? startDate.atStartOfDay() : null)
                        .endDate(endDate != null ? endDate.atStartOfDay() : null)
                        .build();
                popupStoreRepository.save(popupStore);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV import 실패", e);
        }
    }

    /**
     * 매달 말일 23:00에 popupstores.csv 파일을 자동 임포트합니다.
     * (크롤러가 미리 실행되어 최신 CSV가 준비되어 있어야 함)
     */
    @Scheduled(cron = "0 0 23 L * ?")
    public void scheduledImport() {
        importFromCsv("popupstores.csv");
    }
} 