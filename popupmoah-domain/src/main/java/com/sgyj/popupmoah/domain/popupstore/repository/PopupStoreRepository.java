package com.sgyj.popupmoah.domain.popupstore.repository;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PopupStoreRepository extends JpaRepository<PopupStore, Long> {

    /**
     * 이름으로 팝업스토어가 존재하는지 확인
     */
    boolean existsByName(String name);

    /**
     * 이름으로 팝업스토어 검색
     */
    Optional<PopupStore> findByName(String name);
} 