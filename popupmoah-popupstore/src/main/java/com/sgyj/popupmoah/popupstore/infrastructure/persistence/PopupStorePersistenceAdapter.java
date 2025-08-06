package com.sgyj.popupmoah.popupstore.infrastructure.persistence;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 영속성 어댑터 (Outbound Adapter)
 * PopupStoreRepositoryPort 인터페이스를 구현하여 JPA Repository와 연결합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PopupStorePersistenceAdapter implements PopupStoreRepositoryPort {

    private final PopupStoreJpaRepository popupStoreJpaRepository;

    @Override
    public PopupStore save(PopupStore popupStore) {
        log.info("팝업스토어 저장: id={}, name={}", popupStore.getId(), popupStore.getName());
        return popupStoreJpaRepository.save(popupStore);
    }

    @Override
    public Optional<PopupStore> findById(Long id) {
        log.info("팝업스토어 조회: id={}", id);
        return popupStoreJpaRepository.findById(id);
    }

    @Override
    public List<PopupStore> findAllActive() {
        log.info("활성화된 팝업스토어 목록 조회");
        return popupStoreJpaRepository.findByActiveTrue();
    }

    @Override
    public List<PopupStore> findCurrentlyActive() {
        log.info("현재 진행 중인 팝업스토어 목록 조회");
        return popupStoreJpaRepository.findCurrentlyActive(LocalDateTime.now());
    }

    @Override
    public List<PopupStore> findByCategory(String category) {
        log.info("카테고리별 팝업스토어 조회: category={}", category);
        return popupStoreJpaRepository.findByCategoryAndActiveTrue(category);
    }

    @Override
    public List<PopupStore> findByLocation(String location) {
        log.info("위치별 팝업스토어 조회: location={}", location);
        return popupStoreJpaRepository.findByLocationAndActiveTrue(location);
    }

    @Override
    public List<PopupStore> findByNameContaining(String name) {
        log.info("이름으로 팝업스토어 검색: name={}", name);
        return popupStoreJpaRepository.findByNameContainingAndActiveTrue(name);
    }

    @Override
    public void deleteById(Long id) {
        log.info("팝업스토어 삭제: id={}", id);
        popupStoreJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsBySourceUrl(String sourceUrl) {
        log.info("소스 URL 존재 확인: sourceUrl={}", sourceUrl);
        return popupStoreJpaRepository.existsBySourceUrl(sourceUrl);
    }

    @Override
    public List<String> findAllSourceUrls() {
        log.info("모든 소스 URL 조회");
        return popupStoreJpaRepository.findAllSourceUrls();
    }
} 