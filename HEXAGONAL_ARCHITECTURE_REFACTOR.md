# 🏗️ 진정한 핵사고날 아키텍처 리팩토링

## 📋 현재 문제점

### ❌ 잘못된 구조

```
popupstore/
├── build.gradle (Spring 의존성 포함)
└── src/main/java/
    └── domain/
        └── entity/
            └── PopupStore.java (@Entity, @Table 어노테이션 포함)
```

### ✅ 올바른 구조

```
popupstore/
├── domain/ (순수 Java)
│   ├── entity/
│   │   └── PopupStore.java (순수 Java, JPA 어노테이션 없음)
│   ├── aggregate/
│   │   └── PopupStoreAggregate.java (루트 어그리게이트)
│   └── port/
│       ├── PopupStoreRepositoryPort.java
│       └── PopupStoreServicePort.java
├── application/ (유스케이스)
│   └── service/
│       └── PopupStoreApplicationService.java
└── infrastructure/ (어댑터)
    ├── persistence/
    │   └── PopupStoreJpaRepository.java
    └── web/
        └── PopupStoreController.java
```

## 🔄 리팩토링 단계

### 1단계: 도메인 모듈 정리

- Spring 의존성 제거
- JPA 어노테이션 제거
- 순수 Java 엔티티로 변경
- 포트 인터페이스 정의
- 어그리게이트 루트 생성

### 2단계: 인프라스트럭처 모듈 강화

- JPA Repository 어댑터 구현
- 외부 서비스 어댑터 구현
- 포트 구현체 생성

### 3단계: 애플리케이션 서비스 정리

- 유스케이스 구현
- 포트를 통한 도메인 호출
- 트랜잭션 관리

### 4단계: API 모듈 정리

- 컨트롤러만 유지
- 의존성 주입 설정

## 🎯 핵사고날 아키텍처 원칙

1. **도메인 순수성**: 도메인은 외부 의존성 없이 순수 Java
2. **포트-어댑터 패턴**: 도메인은 포트만 정의, 어댑터가 구현
3. **어그리게이트 패턴**: 루트 어그리게이트를 통한 접근 제어
4. **도메인 불변성**: 도메인 객체는 불변성을 유지
5. **의존성 역전**: 도메인이 인프라스트럭처에 의존하지 않음
