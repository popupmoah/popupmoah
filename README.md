# 팝업모아 (PopupMoah)

실시간 팝업스토어, 샘플세일, 이벤트 정보 수집 및 공유 플랫폼

## 🚀 프로젝트 개요

팝업모아는 사용자들이 실시간으로 팝업스토어 정보를 확인하고, 리뷰를 작성하며, 예약할 수 있는 플랫폼입니다.

## 🏗️ 기술 스택

### Backend

- **Spring Boot 3.5.0** - 메인 프레임워크
- **Java 17** - 프로그래밍 언어
- **Hexagonal Architecture** - 아키텍처 패턴 (MSA 대비 모듈 구성)
- **PostgreSQL** - 메인 데이터베이스
- **Flyway** - 데이터베이스 마이그레이션
- **JPA/Hibernate** - ORM
- **AWS S3** - 파일 저장소
- **Redis** - 캐싱
- **JWT** - 인증/인가
- **Docker** - 컨테이너화

### Frontend

- **Vue 3** - 프론트엔드 프레임워크
- **TypeScript** - 타입 안전성
- **Tailwind CSS v4** - 스타일링
- **Pinia** - 상태 관리
- **UI Framework** - Quasar/PrimeVue/기타 (선택 예정)
- **Vite** - 빌드 도구

### DevOps

- **Docker Compose** - 로컬 개발 환경
- **Prometheus** - 모니터링
- **Grafana** - 대시보드
- **GitHub Actions** - CI/CD

## 📁 프로젝트 구조

```
popupmoah/
├── popupmoah-domain/           # 도메인 모듈 (엔티티, 서비스, 포트)
├── popupmoah-api/              # API 모듈 (웹 컨트롤러, 설정)
├── popupmoah-adapter-persistence/  # 영속성 어댑터 (JPA, Repository)
├── popupmoah-common/           # 공통 모듈 (예외, 유틸리티, 상수)
├── popupmoah-core/             # 핵심 모듈 (공통 비즈니스 로직)
├── frontend/                   # Vue3 프론트엔드
├── docker-compose.yml          # 개발 환경
└── README.md
```

## 🏛️ 아키텍처

### Hexagonal Architecture (Ports & Adapters)

- **Domain Layer**: 비즈니스 로직, 엔티티, 도메인 서비스
- **Application Layer**: 유스케이스, 포트 인터페이스
- **Infrastructure Layer**: 어댑터 구현 (웹, 영속성, 외부 서비스)

### 모듈 구성 (MSA 대비)

- **Domain Module**: 순수한 도메인 로직
- **API Module**: 웹 인터페이스
- **Persistence Module**: 데이터 접근 계층
- **Common Module**: 공통 유틸리티, 예외, 상수
- **Core Module**: 핵심 비즈니스 로직

## 🚀 시작하기

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL
- Redis

### Backend 실행

```bash
# 프로젝트 루트에서
./gradlew bootRun
```

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

### Docker로 전체 실행

```bash
docker-compose up -d
```

## 📋 주요 기능

### 완료된 기능

- ✅ 프로젝트 초기 설정 및 환경 구성
- ✅ 데이터베이스 스키마 설계 및 JPA 엔티티 정의
- ✅ 팝업스토어 도메인 및 CRUD 서비스 레이어 구현
- ✅ 카테고리 관리 시스템 구현 (계층형 구조 지원)
- 🔄 S3 파일 업로드/다운로드 및 이미지 최적화 기능 구현 (진행중)

### 진행 예정 기능

- 🔲 Spring Boot Hexagonal Architecture 적용 및 멀티모듈 구조 설계
- 🔲 Vue3 프론트엔드 프레임워크 설정 및 UI 라이브러리 구성
- 🔲 팝업스토어 관리 화면 구현 (Vue3 + Tailwind CSS)
- 🔲 카테고리 관리 화면 구현 (계층형 구조 UI)
- 🔲 파일 업로드 및 이미지 관리 화면 구현
- 🔲 회원 관리 시스템 구현
- 🔲 리뷰 및 댓글 시스템 구현
- 🔲 REST API 엔드포인트 구현
- 🔲 지도 API 연동 및 위치 기반 서비스 구현
- 🔲 검색 및 필터링 기능 구현
- 🔲 예약 시스템 구현
- 🔲 관리자 기능 구현
- 🔲 성능 최적화 및 캐싱 구현
- 🔲 보안 및 인증 강화
- 🔲 테스트 코드 작성 및 CI/CD 파이프라인 구축
- 🔲 모니터링 및 로깅 시스템 구축
- 🔲 배포 및 운영 환경 구성
- 🔲 문서화 및 사용자 가이드 작성

## 🎨 UI/UX 설계 원칙

### Vue3 + Tailwind CSS 기반

- **반응형 디자인**: 모바일, 태블릿, 데스크톱 지원
- **사용자 친화적**: 직관적인 네비게이션과 인터랙션
- **접근성**: WCAG 가이드라인 준수
- **성능 최적화**: 빠른 로딩과 부드러운 애니메이션

### 상태 관리 (Pinia)

- **중앙 집중식 상태 관리**: 전역 상태와 로컬 상태 분리
- **타입 안전성**: TypeScript와 함께 사용
- **개발자 도구**: Vue DevTools 지원

## 🔧 개발 환경 설정

### Backend 개발 환경

```bash
# 데이터베이스 실행
docker-compose up postgres redis -d

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Frontend 개발 환경

```bash
cd frontend
npm install
npm run dev
```

### 모니터링 대시보드

```bash
# Grafana 접속
http://localhost:3000
# 기본 계정: admin/admin
```

## 📊 API 문서

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🧪 테스트

### Backend 테스트

```bash
# 단위 테스트
./gradlew test

# 통합 테스트
./gradlew integrationTest

# 전체 테스트
./gradlew check
```

### Frontend 테스트

```bash
cd frontend
npm run test
npm run test:e2e
```

## 🚀 배포

### Docker 이미지 빌드

```bash
# Backend 이미지 빌드
docker build -t popupmoah-backend .

# Frontend 이미지 빌드
cd frontend
docker build -t popupmoah-frontend .
```

### 프로덕션 배포

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 📝 개발 가이드

### 코드 컨벤션

- **Java**: Google Java Style Guide
- **Vue**: Vue Style Guide
- **TypeScript**: Airbnb TypeScript Style Guide
- **CSS**: Tailwind CSS 클래스 순서

### 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 프로세스 또는 보조 도구 변경
```

### 브랜치 전략

- **main**: 프로덕션 브랜치
- **develop**: 개발 브랜치
- **feature/\***: 기능 개발 브랜치
- **hotfix/\***: 긴급 수정 브랜치

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

프로젝트 링크: [https://github.com/your-username/popupmoah](https://github.com/your-username/popupmoah)

## 🙏 감사의 말

- Spring Boot 팀
- Vue.js 팀
- Tailwind CSS 팀
- 모든 오픈소스 기여자들
