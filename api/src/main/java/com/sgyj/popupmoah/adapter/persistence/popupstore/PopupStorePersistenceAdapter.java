package com.sgyj.popupmoah.adapter.persistence.popupstore;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 영속성 어댑터 (Outbound Adapter)
 * 도메인의 PopupStoreRepositoryPort를 구현하여 JPA Repository와 연결합니다.
 */
@Component
@RequiredArgsConstructor
public class PopupStorePersistenceAdapter implements PopupStoreRepositoryPort {
    
    private final PopupStoreJpaRepository popupStoreJpaRepository;
    
    @Override
    public PopupStore save(PopupStore popupStore) {
        return popupStoreJpaRepository.save(popupStore);
    }
    
    @Override
    public Optional<PopupStore> findById(Long id) {
        return popupStoreJpaRepository.findById(id);
    }
    
    @Override
    public List<PopupStore> findAll() {
        return popupStoreJpaRepository.findAll();
    }
    
    @Override
    public boolean existsByName(String name) {
        return popupStoreJpaRepository.existsByName(name);
    }
    
    @Override
    public Optional<PopupStore> findByName(String name) {
        return popupStoreJpaRepository.findByName(name);
    }
    
    @Override
    public void delete(PopupStore popupStore) {
        popupStoreJpaRepository.delete(popupStore);
    }
    
    @Override
    public void deleteById(Long id) {
        popupStoreJpaRepository.deleteById(id);
    }
} 