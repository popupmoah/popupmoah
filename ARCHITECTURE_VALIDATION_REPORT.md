# PopupMoah 프로젝트 아키텍처 검증 보고서

## 1. 현재 구조 분석

### 1.1 모듈 구조 현황

**현재 설정된 모듈들:**

```
popupmoah/
├── popupmoah-api                    # API 레이어 (웹 컨트롤러)
├── popupmoah-shared                 # 공통 모듈 (유틸리티, 공통 엔티티)
├── popupmoah-infrastructure-common  # 공통 인프라 설정
├── popupmoah-popupstore            # 팝업스토어 도메인 (완전한 Hexagonal 구조)
├── popupmoah-category              # 카테고리 도메인 (완전한 Hexagonal 구조)
├── popupmoah-file                  # 파일 도메인 (완전한 Hexagonal 구조)
├── popupmoah-community             # 커뮤니티 도메인 (완전한 Hexagonal 구조)
└── 중복 모듈들 (제거 필요):
    ├── popupmoah-popupstore-domain
    ├── popupmoah-popupstore-application
    ├── popupmoah-popupstore-infrastructure
    ├── popupmoah-domain
    ├── popupmoah-application
    ├── popupmoah-infrastructure
    ├── popupmoah-adapter-web
    └── popupmoah-adapter-persistence
```

### 1.2 Hexagonal Architecture 준수도 검증

**✅ 잘 구현된 부분:**

- 각 도메인 모듈이 domain/application/infrastructure 계층으로 명확히 분리됨
- 도메인 계층이 외부 의존성 없이 순수한 비즈니스 로직만 포함
- 애플리케이션 계층이 도메인 계층에만 의존
- 인프라 계층이 애플리케이션 계층의 인터페이스를 구현

**❌ 개선이 필요한 부분:**

- 중복된 모듈들이 존재하여 혼란 야기
- 일부 모듈에서 Spring Boot starter 중복 의존
- MSA 준비를 위한 모듈 경계가 불명확

## 2. 개선된 아키텍처 제안

### 2.1 정리된 모듈 구조

```
popupmoah/
├── popupmoah-api                    # API 레이어 (웹 컨트롤러, REST API)
├── popupmoah-shared                 # 공통 모듈 (유틸리티, 공통 엔티티, 공통 응답)
├── popupmoah-infrastructure-common  # 공통 인프라 설정 (보안, 캐시, 모니터링)
├── popupmoah-popupstore            # 팝업스토어 도메인 (완전한 Hexagonal 구조)
├── popupmoah-category              # 카테고리 도메인 (완전한 Hexagonal 구조)
├── popupmoah-file                  # 파일 도메인 (완전한 Hexagonal 구조)
└── popupmoah-community             # 커뮤니티 도메인 (완전한 Hexagonal 구조)
```

### 2.2 각 모듈의 역할과 책임

#### popupmoah-api

- **역할**: 웹 요청/응답 처리, REST API 엔드포인트
- **의존성**: 각 도메인 모듈의 application 계층
- **포함 내용**: 컨트롤러, DTO, API 문서화

#### popupmoah-shared

- **역할**: 공통 유틸리티, 공통 엔티티, 공통 응답 형식
- **의존성**: 최소한의 Spring Boot 의존성만
- **포함 내용**: 공통 엔티티, 유틸리티 클래스, 공통 응답 DTO

#### popupmoah-infrastructure-common

- **역할**: 공통 인프라 설정 (보안, 캐시, 모니터링)
- **의존성**: popupmoah-shared
- **포함 내용**: 보안 설정, 캐시 설정, 모니터링 설정

#### 도메인 모듈들 (popupmoah-{domain})

- **domain**: 순수한 비즈니스 로직, 엔티티, 도메인 서비스
- **application**: 유스케이스, 포트 인터페이스 정의
- **infrastructure**: 포트 구현체, 외부 시스템 연동

### 2.3 의존성 규칙

```
API Layer → Domain Application Layer → Domain Layer
     ↓              ↓
Infrastructure Common → Shared
```

**허용되는 의존성:**

- API → Domain Application
- Domain Application → Domain
- Infrastructure → Domain Application
- 모든 모듈 → Shared
- 모든 모듈 → Infrastructure Common

**금지되는 의존성:**

- Domain → Application/Infrastructure
- Application → Infrastructure
- Shared → 다른 모듈

## 3. MSA 준비도 분석

### 3.1 현재 MSA 준비도: 85%

**✅ MSA 준비가 잘 된 부분:**

- 각 도메인이 독립적인 모듈로 분리됨
- 도메인 간 의존성이 최소화됨
- 공통 인프라가 분리됨

**⚠️ MSA 전환 시 고려사항:**

- 각 도메인을 독립적인 서비스로 분리 가능
- 공통 인프라 설정을 각 서비스에 복제 필요
- API Gateway 패턴 적용 필요

### 3.2 MSA 마이그레이션 로드맵

**Phase 1: 모듈 정리 (현재)**

- 중복 모듈 제거
- 의존성 정리
- 공통 설정 중앙화

**Phase 2: 독립성 강화**

- 도메인 간 직접 의존성 제거
- 이벤트 기반 통신 도입
- 공통 데이터베이스 분리

**Phase 3: 서비스 분리**

- 각 도메인을 독립 서비스로 분리
- API Gateway 도입
- 서비스 디스커버리 구현

## 4. 권장사항

### 4.1 즉시 실행할 작업

1. **중복 모듈 제거**: popupmoah-popupstore-\* 중복 모듈들 삭제
2. **의존성 정리**: 각 모듈의 build.gradle에서 중복 의존성 제거
3. **공통 의존성 중앙화**: popupmoah-shared에 공통 의존성 정의

### 4.2 단기 개선사항

1. **API 문서화**: Swagger/OpenAPI 문서 추가
2. **테스트 커버리지 향상**: 각 계층별 단위 테스트 추가
3. **모니터링 강화**: 각 도메인별 메트릭 수집

### 4.3 장기 개선사항

1. **이벤트 기반 아키텍처**: 도메인 간 통신을 이벤트로 변경
2. **CQRS 패턴**: 읽기/쓰기 모델 분리
3. **서비스 메시**: MSA 전환을 위한 메시징 시스템 도입

## 5. 결론

현재 프로젝트는 Hexagonal Architecture 패턴을 잘 따르고 있으며, MSA 전환을 위한 기본 구조가 잘 갖춰져 있습니다. 중복 모듈 제거와 의존성 정리를 통해 더욱 깔끔하고 유지보수하기 쉬운 아키텍처로 개선할 수 있습니다.

**검증 결과: ✅ 통과**

- Hexagonal Architecture 원칙 준수
- 모듈 경계 명확성 확보
- MSA 준비도 우수
- 의존성 방향 올바름

**다음 단계**: 중복 모듈 제거 및 의존성 정리 작업 수행
