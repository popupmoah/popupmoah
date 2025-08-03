package com.sgyj.popupmoah.adapter.persistence.popupstore;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 팝업스토어 JPA Repository
 * Spring Data JPA를 사용한 데이터 접근 계층
 */
@Repository
public interface PopupStoreJpaRepository extends JpaRepository<PopupStore, Long> {

    /**
     * 이름으로 팝업스토어가 존재하는지 확인
     */
    boolean existsByName(String name);

    /**
     * 이름으로 팝업스토어 검색
     */
    Optional<PopupStore> findByName(String name);
} 