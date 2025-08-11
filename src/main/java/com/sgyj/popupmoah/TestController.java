package com.sgyj.popupmoah;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend is running successfully!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/popupstores")
    public Map<String, Object> getPopupStores() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "팝업스토어 목록 조회 성공");
        response.put("data", new Object[]{
            Map.of(
                "id", 1,
                "name", "테스트 팝업스토어 1",
                "description", "첫 번째 테스트 팝업스토어입니다.",
                "location", "서울시 강남구",
                "startDate", "2024-01-01",
                "endDate", "2024-01-31",
                "latitude", 37.5665,
                "longitude", 126.9780,
                "categoryId", 1,
                "isActive", true
            ),
            Map.of(
                "id", 2,
                "name", "테스트 팝업스토어 2",
                "description", "두 번째 테스트 팝업스토어입니다.",
                "location", "서울시 서초구",
                "startDate", "2024-02-01",
                "endDate", "2024-02-28",
                "latitude", 37.4837,
                "longitude", 127.0324,
                "categoryId", 2,
                "isActive", true
            )
        });
        return response;
    }

    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "카테고리 목록 조회 성공");
        response.put("data", new Object[]{
            Map.of(
                "id", 1,
                "name", "패션",
                "description", "의류, 신발, 액세서리 등",
                "parentId", null,
                "level", 1,
                "orderIndex", 1,
                "isActive", true,
                "children", new Object[]{
                    Map.of(
                        "id", 5,
                        "name", "여성의류",
                        "description", "여성 의류 전용",
                        "parentId", 1,
                        "level", 2,
                        "orderIndex", 1,
                        "isActive", true
                    )
                }
            ),
            Map.of(
                "id", 2,
                "name", "식품",
                "description", "음식, 음료, 간식 등",
                "parentId", null,
                "level", 1,
                "orderIndex", 2,
                "isActive", true,
                "children", new Object[0]
            )
        });
        return response;
    }
}
