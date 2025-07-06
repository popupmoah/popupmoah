package com.sgyj.popupmoah.module.popupstore.controller;

import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreCreatedResponse;
import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreSearchedResponse;
import com.sgyj.popupmoah.module.popupstore.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popup-stores")
public class PopupStoreController {

    private final PopupStoreService popupStoreService;

    /**
     * 팝업 스토어 등록
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<PopupStoreCreatedResponse> createPopupStore(CreatePopupStoreRequest request) {
        return ResponseEntity.ok(
            PopupStoreCreatedResponse.fromDto(popupStoreService.createPopupStore(request))
        );
    }

    /**
     * 팝업 스토어 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<PopupStoreSearchedResponse> getPopupStore(Long id) {
        return ResponseEntity.ok(
                PopupStoreSearchedResponse.fromDto(popupStoreService.getPopupStore(id))
        );
    }

    /**
     * 모든 팝업 스토어 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<List<PopupStoreSearchedResponse>> getAllPopupStores() {
        return ResponseEntity.ok(
                popupStoreService.getAllPopupStores().stream().map(PopupStoreSearchedResponse::fromDto).toList()
        );
    }

    /**
     * 팝업 스토어 삭제
     * @param id
     * @return
     */
    public ResponseEntity<Void> deletePopupStore(Long id) {
        popupStoreService.deletePopupStore(id);
        return ResponseEntity.noContent().build();
    }
}
