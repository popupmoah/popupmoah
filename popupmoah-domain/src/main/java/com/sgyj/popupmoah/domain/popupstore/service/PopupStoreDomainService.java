package com.sgyj.popupmoah.domain.popupstore.service;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.domain.popupstore.port.PopupStoreRepositoryPort;
import com.sgyj.popupmoah.domain.popupstore.port.PopupStoreServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 도메인 서비스
 * 팝업스토어 도메인의 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class PopupStoreDomainService implements PopupStoreServicePort {
    
    private final PopupStoreRepositoryPort popupStoreRepositoryPort;
    
    @Override
    public PopupStore createPopupStore(PopupStore popupStore) {
        // 도메인 규칙 검증
        validatePopupStore(popupStore);
        
        // 중복 검사
        if (popupStoreRepositoryPort.existsByName(popupStore.getName())) {
            throw new IllegalArgumentException("이미 존재하는 팝업스토어 이름입니다: " + popupStore.getName());
        }
        
        return popupStoreRepositoryPort.save(popupStore);
    }
    
    @Override
    public Optional<PopupStore> getPopupStoreById(Long id) {
        return popupStoreRepositoryPort.findById(id);
    }
    
    @Override
    public List<PopupStore> getAllPopupStores() {
        return popupStoreRepositoryPort.findAll();
    }
    
    @Override
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        PopupStore existingStore = popupStoreRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id));
        
        // 도메인 규칙 검증
        validatePopupStore(popupStore);
        
        // 업데이트 로직
        existingStore.updateInfo(
                popupStore.getName(),
                popupStore.getDescription(),
                popupStore.getStartDate(),
                popupStore.getEndDate(),
                popupStore.getStoreNumber(),
                popupStore.getEmail(),
                popupStore.getWebsite()
        );
        
        if (popupStore.getLatitude() != null && popupStore.getLongitude() != null) {
            existingStore.updateLocation(popupStore.getLatitude(), popupStore.getLongitude());
        }
        
        return popupStoreRepositoryPort.save(existingStore);
    }
    
    @Override
    public void deletePopupStore(Long id) {
        if (!popupStoreRepositoryPort.findById(id).isPresent()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id);
        }
        
        popupStoreRepositoryPort.deleteById(id);
    }
    
    @Override
    public boolean existsByName(String name) {
        return popupStoreRepositoryPort.existsByName(name);
    }
    
    /**
     * 팝업스토어 도메인 규칙 검증
     */
    private void validatePopupStore(PopupStore popupStore) {
        if (popupStore.getName() == null || popupStore.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("팝업스토어 이름은 필수입니다.");
        }
        
        if (popupStore.getName().length() > 200) {
            throw new IllegalArgumentException("팝업스토어 이름은 200자를 초과할 수 없습니다.");
        }
        
        if (popupStore.getStartDate() != null && popupStore.getEndDate() != null) {
            if (popupStore.getStartDate().isAfter(popupStore.getEndDate())) {
                throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
            }
        }
    }
} 