package com.sgyj.popupmoah.module.popupstore.repository;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PopupStoreRepository extends JpaRepository<PopupStore, Long> {

    /**
     * 모든 팝업스토어의 sourceUrl 목록 반환
     */
    @Query("select p.sourceUrl from PopupStore p where p.sourceUrl is not null")
    List<String> findAllSourceUrls();
}
