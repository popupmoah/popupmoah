# 🏗️ 올바른 핵사고날 아키텍처 구조

## 📋 기술 중립적인 Infrastructure 구조

### ✅ 올바른 구조

```
popupstore/
├── domain/ (순수 Java)
│   ├── entity/
│   │   └── PopupStore.java (순수 Java, 기술 의존성 없음)
│   ├── aggregate/
│   │   └── PopupStoreAggregate.java (루트 어그리게이트)
│   └── port/
│       ├── PopupStoreRepositoryPort.java (기술 중립 포트)
│       └── PopupStoreServicePort.java (서비스 포트)
├── application/ (유스케이스)
│   └── service/
│       └── PopupStoreApplicationService.java
└── infrastructure/ (기술별 어댑터)
    ├── persistence/
    │   ├── PopupStoreRepositoryAdapter.java (기술 중립 어댑터)
    │   ├── jpa/
    │   │   ├── PopupStoreJpaRepository.java
    │   │   ├── PopupStoreJpaEntity.java
    │   │   └── PopupStoreJpaEntityRepository.java
    │   └── r2dbc/
    │       ├── PopupStoreR2dbcRepository.java
    │       ├── PopupStoreR2dbcEntity.java
    │       └── PopupStoreR2dbcEntityRepository.java
    └── web/
        └── PopupStoreController.java
```

## 🔄 기술 변경 시나리오

### JPA → R2DBC 변경

**변경 전 (JPA 사용)**:

```java
@Repository
@RequiredArgsConstructor
public class PopupStoreRepositoryAdapter implements PopupStoreRepositoryPort {
    private final PopupStoreJpaRepository jpaRepository;

    @Override
    public PopupStore save(PopupStore popupStore) {
        return jpaRepository.save(popupStore);
    }
}
```

**변경 후 (R2DBC 사용)**:

```java
@Repository
@RequiredArgsConstructor
public class PopupStoreRepositoryAdapter implements PopupStoreRepositoryPort {
    private final PopupStoreR2dbcRepository r2dbcRepository;

    @Override
    public PopupStore save(PopupStore popupStore) {
        return r2dbcRepository.save(popupStore);
    }
}
```

## 🎯 핵사고날 아키텍처 원칙

### 1. **도메인 순수성**

- 도메인은 순수 Java 코드만 포함
- 외부 프레임워크 의존성 없음 (Spring, JPA, R2DBC 등)
- 기술 중립적인 포트만 정의

### 2. **포트-어댑터 패턴**

- **포트**: 도메인이 정의하는 인터페이스 (기술 중립)
- **어댑터**: 포트를 구현하는 기술별 구현체
- 도메인은 포트만 알고, 어댑터는 모름

### 3. **기술 중립성**

- Infrastructure는 기술별로 분리 (jpa/, r2dbc/, mongodb/ 등)
- 기술 변경 시 해당 기술의 어댑터만 수정
- 도메인과 애플리케이션 레이어는 변경 불필요

### 4. **의존성 방향**

```
Web Controller → Application Service → Domain Aggregate → Port Interface ← Technology Adapter
```

## 🔧 실제 사용 예시

### JPA 사용 시

```java
// application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:postgresql://localhost:5432/popupmoah
```

### R2DBC 사용 시

```java
// application.yml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/popupmoah
```

**변경 사항**:

1. `application.yml` 설정만 변경
2. `PopupStoreRepositoryAdapter`에서 주입받는 구현체만 변경
3. **도메인, 애플리케이션 레이어는 전혀 변경 불필요**

## 🚀 장점

1. **기술 독립성**: 데이터베이스 기술 변경 시 도메인 로직 영향 없음
2. **테스트 용이성**: 도메인은 순수 Java로 쉽게 테스트 가능
3. **유지보수성**: 기술별 구현체가 명확히 분리됨
4. **확장성**: 새로운 기술 추가 시 해당 기술의 어댑터만 구현
5. **팀 협업**: 도메인 팀과 인프라 팀이 독립적으로 작업 가능
