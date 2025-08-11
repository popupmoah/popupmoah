package com.sgyj.popupmoah.popupstore.application.service;

import com.sgyj.popupmoah.popupstore.domain.aggregate.PopupStoreAggregate;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 애플리케이션 서비스
 * 유스케이스를 구현하는 애플리케이션 레이어
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PopupStoreApplicationService implements PopupStoreServicePort {
    
    private final PopupStoreRepositoryPort repository;
    private final PopupStoreAggregate aggregate;
    
    @Override
    public PopupStore createPopupStore(PopupStore popupStore) {
        return aggregate.create(popupStore);
    }
    
    @Override
    public Optional<PopupStore> getPopupStore(Long id) {
        return aggregate.findById(id);
    }
    
    @Override
    public List<PopupStore> getAllPopupStores() {
        return aggregate.findAll();
    }
    
    @Override
    public List<PopupStore> getActivePopupStores() {
        return aggregate.findActive();
    }
    
    @Override
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        return aggregate.update(id, popupStore);
    }
    
    @Override
    public void deletePopupStore(Long id) {
        aggregate.delete(id);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        aggregate.incrementViewCount(id);
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        aggregate.incrementLikeCount(id);
    }
    
    @Override
    public void decrementLikeCount(Long id) {
        aggregate.decrementLikeCount(id);
    }
    
    @Override
    public void activatePopupStore(Long id) {
        aggregate.activate(id);
    }
    
    @Override
    public void deactivatePopupStore(Long id) {
        aggregate.deactivate(id);
    }
} 