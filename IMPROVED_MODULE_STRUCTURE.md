# 개선된 모듈 구조 제안

## 현재 문제점

1. **모듈 네이밍**: `popupmoah-*` prefix가 너무 길고 일반적이지 않음
2. **중복 모듈**: `popupmoah-popupstore-*`와 `popupmoah-popupstore` 중복
3. **일관성 부족**: 일부는 도메인별, 일부는 레이어별로 분리됨

## 개선된 구조 제안

### Option 1: 도메인 중심 구조 (권장)

```
popupmoah/
├── api/                    # Web Controllers
├── core/                   # 공통 도메인 로직
├── shared/                 # 공유 유틸리티
├── infrastructure/         # 공통 인프라
├── popupstore/            # 팝업스토어 도메인
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── category/              # 카테고리 도메인
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── community/             # 커뮤니티 도메인
│   ├── domain/
│   ├── application/
│   └── infrastructure/
└── file/                  # 파일 도메인
    ├── domain/
    ├── application/
    └── infrastructure/
```

### Option 2: 레이어 중심 구조

```
popupmoah/
├── web/                   # Web Layer
├── application/           # Application Layer
├── domain/               # Domain Layer
├── infrastructure/       # Infrastructure Layer
└── shared/              # Shared Layer
```

## settings.gradle 개선안

```gradle
rootProject.name = 'popupmoah'

// Core modules
include 'api'
include 'core'
include 'shared'
include 'infrastructure'

// Domain modules
include 'popupstore'
include 'category'
include 'community'
include 'file'

// 각 도메인 모듈의 하위 모듈들
include 'popupstore:domain'
include 'popupstore:application'
include 'popupstore:infrastructure'

include 'category:domain'
include 'category:application'
include 'category:infrastructure'

include 'community:domain'
include 'community:application'
include 'community:infrastructure'

include 'file:domain'
include 'file:application'
include 'file:infrastructure'
```

## 기업 사례 분석

### 1. **Netflix**

- **패턴**: `dgs-*` prefix 사용
- **이유**: 도메인별 분리, 마이크로서비스 준비

### 2. **Spring PetClinic**

- **패턴**: `*-service` suffix 사용
- **이유**: 서비스 중심 아키텍처

### 3. **Axon Framework**

- **패턴**: `axon-example-*` prefix 사용
- **이유**: CQRS 패턴에 맞춘 분리

## 권장사항

1. **Option 1 (도메인 중심)** 선택
2. **짧은 모듈명** 사용 (`popupstore`, `category` 등)
3. **일관된 네이밍 컨벤션** 적용
4. **마이크로서비스 전환 준비**를 위한 구조 설계

## 마이그레이션 계획

1. **Phase 1**: 새 모듈 구조 생성
2. **Phase 2**: 기존 코드 점진적 이전
3. **Phase 3**: 중복 모듈 제거
4. **Phase 4**: 테스트 및 검증
