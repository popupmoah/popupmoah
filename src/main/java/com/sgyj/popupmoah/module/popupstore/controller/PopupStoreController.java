package com.sgyj.popupmoah.module.popupstore.controller;

import com.sgyj.popupmoah.config.ApiResponse; // 표준 응답 DTO
import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreCreatedResponse;
import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreDefaultResponse;
import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreSearchedResponse;
import com.sgyj.popupmoah.module.popupstore.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popup-stores")
public class PopupStoreController {

    private final PopupStoreService popupStoreService;

    /**
     * 팝업스토어 등록 (표준 응답 포맷 적용)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PopupStoreCreatedResponse>> createPopupStore(@RequestBody CreatePopupStoreRequest request) {
        PopupStoreDto dto = popupStoreService.createPopupStore(request);
        return ResponseEntity.ok(ApiResponse.success(PopupStoreCreatedResponse.fromDto(dto)));
    }

    /**
     * 팝업스토어 단건 조회 (표준 응답 포맷 적용)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStoreDefaultResponse>> getPopupStore(@PathVariable Long id) {
        PopupStoreDto dto = popupStoreService.getPopupStore(id);
        return ResponseEntity.ok(ApiResponse.success(PopupStoreDefaultResponse.getPopupStoreDefaultResponse(dto, new PopupStoreDefaultResponse())));
    }

    /**
     * 팝업스토어 전체 조회 (표준 응답 포맷 적용)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PopupStoreSearchedResponse>>> getAllPopupStores() {
        List<PopupStoreDto> dtos = popupStoreService.getAllPopupStores();
        List<PopupStoreSearchedResponse> responses = dtos.stream().map(PopupStoreSearchedResponse::fromDto).toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
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
