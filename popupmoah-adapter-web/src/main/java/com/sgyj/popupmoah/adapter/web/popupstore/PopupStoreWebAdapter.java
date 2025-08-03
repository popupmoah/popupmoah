package com.sgyj.popupmoah.adapter.web.popupstore;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.domain.popupstore.port.PopupStoreServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팝업스토어 웹 어댑터 (Inbound Adapter)
 * HTTP 요청을 도메인 서비스로 전달하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/popupstores")
@RequiredArgsConstructor
public class PopupStoreWebAdapter {
    
    private final PopupStoreServicePort popupStoreServicePort;
    
    @PostMapping
    public ResponseEntity<PopupStore> createPopupStore(@RequestBody PopupStore popupStore) {
        PopupStore createdStore = popupStoreServicePort.createPopupStore(popupStore);
        return ResponseEntity.ok(createdStore);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PopupStore> getPopupStore(@PathVariable Long id) {
        return popupStoreServicePort.getPopupStoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<PopupStore>> getAllPopupStores() {
        List<PopupStore> stores = popupStoreServicePort.getAllPopupStores();
        return ResponseEntity.ok(stores);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PopupStore> updatePopupStore(@PathVariable Long id, @RequestBody PopupStore popupStore) {
        try {
            PopupStore updatedStore = popupStoreServicePort.updatePopupStore(id, popupStore);
            return ResponseEntity.ok(updatedStore);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePopupStore(@PathVariable Long id) {
        try {
            popupStoreServicePort.deletePopupStore(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkNameExists(@RequestParam String name) {
        boolean exists = popupStoreServicePort.existsByName(name);
        return ResponseEntity.ok(exists);
    }
} 