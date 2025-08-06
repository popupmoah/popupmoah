# 개선된 settings.gradle 제안

## 현재 settings.gradle의 문제점

1. **중복 모듈**: popupmoah-popupstore와 popupmoah-popupstore-\* 모듈들이 중복
2. **사용되지 않는 모듈**: popupmoah-domain, popupmoah-application, popupmoah-infrastructure 등
3. **불명확한 모듈 경계**: adapter 모듈들이 실제로 사용되지 않음

## 개선된 settings.gradle

```gradle
rootProject.name = 'popupmoah'

// ===== 핵심 모듈들 =====
include 'popupmoah-api'                    // API 레이어 (웹 컨트롤러, REST API)
include 'popupmoah-shared'                 // 공통 모듈 (유틸리티, 공통 엔티티)
include 'popupmoah-infrastructure-common'  // 공통 인프라 설정

// ===== 도메인별 모듈들 (Hexagonal Architecture) =====
include 'popupmoah-popupstore'            // 팝업스토어 도메인
include 'popupmoah-category'              // 카테고리 도메인
include 'popupmoah-file'                  // 파일 도메인
include 'popupmoah-community'             // 커뮤니티 도메인

// ===== 제거할 중복 모듈들 =====
// include 'popupmoah-popupstore-domain'      // ❌ 중복 - popupmoah-popupstore에 통합
// include 'popupmoah-popupstore-application' // ❌ 중복 - popupmoah-popupstore에 통합
// include 'popupmoah-popupstore-infrastructure' // ❌ 중복 - popupmoah-popupstore에 통합
// include 'popupmoah-domain'                 // ❌ 사용되지 않음
// include 'popupmoah-application'            // ❌ 사용되지 않음
// include 'popupmoah-infrastructure'         // ❌ 사용되지 않음
// include 'popupmoah-adapter-web'            // ❌ 사용되지 않음
// include 'popupmoah-adapter-persistence'    // ❌ 사용되지 않음
// include 'popupmoah-core'                   // ❌ popupmoah-shared로 통합

// ===== 모듈 설명 =====
/*
모듈 구조 설명:

1. popupmoah-api
   - 웹 요청/응답 처리
   - REST API 엔드포인트
   - 컨트롤러, DTO, API 문서화

2. popupmoah-shared
   - 공통 유틸리티, 공통 엔티티
   - 공통 응답 형식
   - 최소한의 Spring Boot 의존성만

3. popupmoah-infrastructure-common
   - 공통 인프라 설정 (보안, 캐시, 모니터링)
   - AWS S3, Redis, Prometheus 등

4. popupmoah-{domain} (popupstore, category, file, community)
   - 각 도메인별 완전한 Hexagonal Architecture
   - domain/: 순수한 비즈니스 로직
   - application/: 유스케이스, 포트 인터페이스
   - infrastructure/: 포트 구현체, 외부 시스템 연동

의존성 규칙:
- API → Domain Application
- Domain Application → Domain
- Infrastructure → Domain Application
- 모든 모듈 → Shared
- 모든 모듈 → Infrastructure Common
*/
```

## 모듈 정리 계획

### Phase 1: 중복 모듈 제거

```bash
# 제거할 모듈들
rm -rf popupmoah-popupstore-domain
rm -rf popupmoah-popupstore-application
rm -rf popupmoah-popupstore-infrastructure
rm -rf popupmoah-domain
rm -rf popupmoah-application
rm -rf popupmoah-infrastructure
rm -rf popupmoah-adapter-web
rm -rf popupmoah-adapter-persistence
rm -rf popupmoah-core
```

### Phase 2: 의존성 정리

각 모듈의 build.gradle에서 중복 의존성을 제거하고, popupmoah-shared에 공통 의존성을 정의

### Phase 3: 검증

```bash
./gradlew clean build
./gradlew test
```

## MSA 준비를 위한 모듈 구조

현재 구조는 MSA 전환에 매우 적합합니다:

1. **각 도메인이 독립적인 모듈로 분리됨**
2. **도메인 간 의존성이 최소화됨**
3. **공통 인프라가 분리됨**

향후 MSA 전환 시:

- 각 popupmoah-{domain} 모듈을 독립적인 서비스로 분리
- popupmoah-shared와 popupmoah-infrastructure-common을 각 서비스에 복제
- API Gateway 패턴 적용
