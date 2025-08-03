package com.sgyj.popupmoah.module.crawling.service;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팝업스토어 정보를 자동으로 크롤링하는 서비스
 * 정각마다 실행되어 새로운 팝업 정보를 수집합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PopupStoreRepository popupStoreRepository;
    private final WebClient webClient;

    /**
     * 정각마다 팝업스토어 정보를 크롤링합니다.
     * cron 표현식: 매시 정각 (0분 0초)
     */
    @Scheduled(cron = "0 0 * * * *") // 매시 정각
    public void crawlPopupStores() {
        log.info("팝업스토어 크롤링 시작: {}", LocalDateTime.now());
        
        try {
            // 크롤링 대상 사이트들
            List<String> targetSites = List.of(
                "https://example-popup-site1.com",
                "https://example-popup-site2.com"
            );

            for (String site : targetSites) {
                crawlSite(site);
            }
            
            log.info("팝업스토어 크롤링 완료: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("팝업스토어 크롤링 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 사이트에서 팝업스토어 정보를 크롤링합니다.
     * 
     * @param siteUrl 크롤링할 사이트 URL
     */
    private void crawlSite(String siteUrl) {
        log.info("사이트 크롤링 시작: {}", siteUrl);
        
        webClient.get()
                .uri(siteUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parsePopupData)
                .doOnSuccess(popupStores -> {
                    log.info("크롤링된 팝업스토어 수: {}", popupStores.size());
                    savePopupStores(popupStores);
                })
                .doOnError(error -> {
                    log.error("사이트 크롤링 실패: {} - {}", siteUrl, error.getMessage());
                })
                .subscribe();
    }

    /**
     * HTML 콘텐츠에서 팝업스토어 데이터를 파싱합니다.
     * 
     * @param htmlContent 크롤링된 HTML 콘텐츠
     * @return 파싱된 팝업스토어 목록
     */
    private Mono<List<PopupStore>> parsePopupData(String htmlContent) {
        // TODO: 실제 HTML 파싱 로직 구현
        // Jsoup을 사용하여 HTML 파싱
        // 예시: 팝업스토어 정보 추출 및 PopupStore 엔티티로 변환
        
        log.info("HTML 콘텐츠 파싱 시작 (길이: {} bytes)", htmlContent.length());
        
        // 임시 구현: 실제 파싱 로직으로 교체 필요
        return Mono.just(List.of());
    }

    /**
     * 크롤링된 팝업스토어 정보를 데이터베이스에 저장합니다.
     * 
     * @param popupStores 저장할 팝업스토어 목록
     */
    private void savePopupStores(List<PopupStore> popupStores) {
        if (popupStores.isEmpty()) {
            log.info("저장할 팝업스토어가 없습니다.");
            return;
        }

        try {
            for (PopupStore popupStore : popupStores) {
                // 중복 체크 (이름과 주소로 판단)
                if (!isDuplicate(popupStore)) {
                    popupStoreRepository.save(popupStore);
                    log.info("새로운 팝업스토어 저장: {}", popupStore.getName());
                } else {
                    log.info("중복 팝업스토어 건너뜀: {}", popupStore.getName());
                }
            }
        } catch (Exception e) {
            log.error("팝업스토어 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 팝업스토어가 중복인지 확인합니다.
     * 
     * @param popupStore 확인할 팝업스토어
     * @return 중복 여부
     */
    private boolean isDuplicate(PopupStore popupStore) {
        // 이름으로만 중복 체크 (임시 구현)
        // TODO: 위치 정보를 포함한 더 정확한 중복 체크 로직 구현 필요
        return popupStoreRepository.findByName(popupStore.getName()).isPresent();
    }

    /**
     * 수동으로 크롤링을 실행합니다.
     * 개발/테스트 목적으로 사용
     */
    public void manualCrawl() {
        log.info("수동 크롤링 시작");
        crawlPopupStores();
    }
} 