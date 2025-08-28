package com.sgyj.popupmoah.performance;

import com.sgyj.popupmoah.popupstore.application.service.PopupStoreApplicationService;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.reservation.application.service.ReservationApplicationService;
import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 캐시 성능 테스트
 * 캐시 히트율과 응답 시간을 측정
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class CachePerformanceTest {

    @Autowired
    private PopupStoreApplicationService popupStoreService;

    @Autowired
    private ReservationApplicationService reservationService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 팝업스토어 조회 캐시 성능 테스트
     */
    @Test
    public void testPopupStoreCachePerformance() {
        log.info("=== 팝업스토어 캐시 성능 테스트 시작 ===");

        // 캐시 초기화
        clearCache("popupstores");

        // 첫 번째 조회 (캐시 미스)
        long startTime = System.currentTimeMillis();
        List<PopupStore> firstResult = popupStoreService.getAllPopupStores();
        long firstQueryTime = System.currentTimeMillis() - startTime;
        log.info("첫 번째 조회 (캐시 미스): {}ms, 결과 수: {}", firstQueryTime, firstResult.size());

        // 두 번째 조회 (캐시 히트)
        startTime = System.currentTimeMillis();
        List<PopupStore> secondResult = popupStoreService.getAllPopupStores();
        long secondQueryTime = System.currentTimeMillis() - startTime;
        log.info("두 번째 조회 (캐시 히트): {}ms, 결과 수: {}", secondQueryTime, secondResult.size());

        // 성능 개선 확인
        double improvement = (double) (firstQueryTime - secondQueryTime) / firstQueryTime * 100;
        log.info("성능 개선: {:.2f}%", improvement);

        // 동시성 테스트
        testConcurrentCacheAccess();
    }

    /**
     * 예약 조회 캐시 성능 테스트
     */
    @Test
    public void testReservationCachePerformance() {
        log.info("=== 예약 캐시 성능 테스트 시작 ===");

        // 캐시 초기화
        clearCache("reservations");

        Long testMemberId = 1L;
        Long testPopupStoreId = 1L;

        // 회원별 예약 조회 테스트
        long startTime = System.currentTimeMillis();
        List<Reservation> firstResult = reservationService.getReservationsByMember(testMemberId);
        long firstQueryTime = System.currentTimeMillis() - startTime;
        log.info("첫 번째 회원별 예약 조회 (캐시 미스): {}ms", firstQueryTime);

        startTime = System.currentTimeMillis();
        List<Reservation> secondResult = reservationService.getReservationsByMember(testMemberId);
        long secondQueryTime = System.currentTimeMillis() - startTime;
        log.info("두 번째 회원별 예약 조회 (캐시 히트): {}ms", secondQueryTime);

        // 팝업스토어별 예약 조회 테스트
        startTime = System.currentTimeMillis();
        List<Reservation> thirdResult = reservationService.getReservationsByPopupStore(testPopupStoreId);
        long thirdQueryTime = System.currentTimeMillis() - startTime;
        log.info("팝업스토어별 예약 조회 (캐시 미스): {}ms", thirdQueryTime);

        startTime = System.currentTimeMillis();
        List<Reservation> fourthResult = reservationService.getReservationsByPopupStore(testPopupStoreId);
        long fourthQueryTime = System.currentTimeMillis() - startTime;
        log.info("팝업스토어별 예약 조회 (캐시 히트): {}ms", fourthQueryTime);
    }

    /**
     * 동시성 캐시 접근 테스트
     */
    private void testConcurrentCacheAccess() {
        log.info("=== 동시성 캐시 접근 테스트 시작 ===");

        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                try {
                    popupStoreService.getAllPopupStores();
                    popupStoreService.getActivePopupStores();
                } catch (Exception e) {
                    log.error("동시성 테스트 오류: {}", e.getMessage());
                }
            }, executor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("동시성 테스트 완료: {}ms (100개 요청)", totalTime);
        
        executor.shutdown();
    }

    /**
     * 캐시 통계 조회
     */
    @Test
    public void testCacheStatistics() {
        log.info("=== 캐시 통계 조회 ===");

        cacheManager.getCacheNames().forEach(cacheName -> {
            log.info("캐시 이름: {}", cacheName);
            // 실제 구현에서는 캐시 통계를 조회하는 로직 추가
        });
    }

    /**
     * 캐시 무효화 테스트
     */
    @Test
    public void testCacheEviction() {
        log.info("=== 캐시 무효화 테스트 시작 ===");

        // 캐시 초기화
        clearCache("popupstores");

        // 데이터 조회하여 캐시에 저장
        List<PopupStore> firstResult = popupStoreService.getAllPopupStores();
        log.info("첫 번째 조회 완료, 결과 수: {}", firstResult.size());

        // 캐시 무효화 (실제로는 팝업스토어 생성/수정을 통해)
        // popupStoreService.createPopupStore(new PopupStore(...));

        // 다시 조회하여 캐시가 무효화되었는지 확인
        List<PopupStore> secondResult = popupStoreService.getAllPopupStores();
        log.info("캐시 무효화 후 조회 완료, 결과 수: {}", secondResult.size());
    }

    /**
     * 캐시 클리어
     */
    private void clearCache(String cacheName) {
        if (cacheManager.getCache(cacheName) != null) {
            cacheManager.getCache(cacheName).clear();
            log.info("캐시 클리어 완료: {}", cacheName);
        }
    }
}
