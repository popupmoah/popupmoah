package com.sgyj.popupmoah.module.popupstore.controller.request;

import com.sgyj.popupmoah.module.popupstore.controller.rseponse.PopupStoreCreatedResponse;
import com.sgyj.popupmoah.module.popupstore.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popup-stores")
public class PopupStoreController {

    private final PopupStoreService popupStoreService;

    @PostMapping
    public ResponseEntity<PopupStoreCreatedResponse> createPopupStore(CreatePopupStoreRequest request) {
        return ResponseEntity.ok(
            PopupStoreCreatedResponse.fromDto(popupStoreService.createPopupStore(request))
        );
    }
}
