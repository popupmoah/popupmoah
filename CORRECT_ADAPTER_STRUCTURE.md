# 🏗️ 올바른 핵사고날 아키텍처 - 어댑터 구조

## ❌ 잘못된 구조 (기술이 infrastructure에 포함)

```
popupstore/
├── domain/
├── application/
└── infrastructure/
    └── persistence/
        ├── jpa/  ← 문제! 기술이 infrastructure에 포함됨
        │   ├── PopupStoreJpaRepository.java
        │   ├── PopupStoreJpaEntity.java
        │   └── PopupStoreJpaEntityRepository.java
        └── r2dbc/
            ├── PopupStoreR2dbcRepository.java
            ├── PopupStoreR2dbcEntity.java
            └── PopupStoreR2dbcEntityRepository.java
```

## ✅ 올바른 구조 (기술별 어댑터 분리)

```
popupstore/
├── domain/ (순수 Java)
│   ├── entity/
│   │   └── PopupStore.java (순수 Java)
│   ├── aggregate/
│   │   └── PopupStoreAggregate.java
│   └── port/
│       ├── PopupStoreRepositoryPort.java (기술 중립)
│       └── PopupStoreServicePort.java
├── application/ (유스케이스)
│   └── service/
│       └── PopupStoreApplicationService.java
├── infrastructure/ (기술 중립)
│   └── persistence/
│       └── PopupStoreRepositoryAdapter.java (기술 중립 어댑터)
└── adapters/ (기술별 어댑터)
    ├── jpa/
    │   ├── PopupStoreJpaRepository.java
    │   ├── PopupStoreJpaEntity.java
    │   └── PopupStoreJpaEntityRepository.java
    └── r2dbc/
        ├── PopupStoreR2dbcRepository.java
        ├── PopupStoreR2dbcEntity.java
        └── PopupStoreR2dbcEntityRepository.java
```

## 🎯 핵사고날 아키텍처 원칙

### 1. **Infrastructure는 기술 중립적**

- Infrastructure는 기술에 독립적이어야 함
- 기술별 구현체는 별도의 adapters 패키지에 위치

### 2. **Adapters는 기술별 구현체**

- JPA, R2DBC, MongoDB 등 기술별로 분리
- 각 기술의 특성에 맞는 구현체 제공

### 3. **의존성 방향**

```
Domain → Port Interface ← Infrastructure Adapter ← Technology Adapter
```

## 🔄 JPA → R2DBC 변경 시나리오

### 변경 전 (JPA 사용)

```java
@Repository
@RequiredArgsConstructor
public class PopupStoreRepositoryAdapter implements PopupStoreRepositoryPort {
    private final PopupStoreJpaRepository jpaRepository; // JPA 어댑터

    @Override
    public PopupStore save(PopupStore popupStore) {
        return jpaRepository.save(popupStore);
    }
}
```

### 변경 후 (R2DBC 사용)

```java
@Repository
@RequiredArgsConstructor
public class PopupStoreRepositoryAdapter implements PopupStoreRepositoryPort {
    private final PopupStoreR2dbcRepository r2dbcRepository; // R2DBC 어댑터

    @Override
    public PopupStore save(PopupStore popupStore) {
        return r2dbcRepository.save(popupStore);
    }
}
```

## 🚀 장점

1. **명확한 책임 분리**: Infrastructure는 기술 중립, Adapters는 기술별 구현
2. **기술 독립성**: Infrastructure는 기술 변경에 영향받지 않음
3. **확장성**: 새로운 기술 추가 시 adapters에만 추가
4. **테스트 용이성**: 각 레이어를 독립적으로 테스트 가능
5. **팀 협업**: 도메인 팀과 인프라 팀이 독립적으로 작업

## 📋 핵심 포인트

- **Infrastructure**: 기술 중립적인 어댑터 (포트 구현체)
- **Adapters**: 기술별 구체적인 구현체 (JPA, R2DBC 등)
- **Domain**: 순수 Java, 기술 의존성 없음
- **Application**: 유스케이스 구현, 포트를 통한 도메인 호출

이렇게 하면 **진정한 핵사고날 아키텍처**가 됩니다! 🎉
