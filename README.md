# PopupMoah - 팝업스토어 플랫폼

팝업스토어 정보를 제공하고 커뮤니티 기능을 결합한 종합 플랫폼입니다.

## 🚀 기술 스택

### 백엔드

- **Java 21** - 최신 LTS 버전
- **Spring Boot 3.5.0** - 최신 Spring Boot 버전
- **Spring Security** - JWT 기반 인증
- **Spring Data JPA** - 데이터 접근 계층
- **Spring WebFlux** - 비동기 처리
- **PostgreSQL 15** - 메인 데이터베이스
- **Redis 7** - 캐싱 및 세션 저장소
- **AWS S3** - 파일 저장소

### 인프라 & 모니터링

- **Docker & Docker Compose** - 컨테이너화
- **Prometheus** - 메트릭 수집
- **Grafana** - 모니터링 대시보드
- **Jasypt** - 설정 암호화

## 📋 사전 요구사항

- Java 21
- Docker & Docker Compose
- Git

## 🛠️ 로컬 개발 환경 설정

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd popupmoah
```

### 2. 환경 변수 설정

```bash
# .env 파일 생성 (선택사항)
cp .env.example .env
```

### 3. 로컬 실행

```bash
# Gradle을 사용한 로컬 실행
./gradlew bootRun

# 또는 Docker Compose로 전체 인프라 실행
docker compose up -d
```

## 🐳 Docker 환경 실행

### 전체 인프라 실행

```bash
# 모든 서비스 시작
docker compose up -d

# 로그 확인
docker compose logs -f app
```

### 개별 서비스 실행

```bash
# 데이터베이스만 실행
docker compose up -d postgres redis

# 애플리케이션만 실행
docker compose up -d app
```

## 📊 모니터링

### Prometheus

- URL: http://localhost:9090
- Spring Boot 애플리케이션 메트릭 수집

### Grafana

- URL: http://localhost:3000
- 기본 계정: admin / admin123
- Prometheus 데이터 소스 추가 필요

## 🔧 주요 엔드포인트

### 애플리케이션

- **메인 애플리케이션**: http://localhost:5032
- **H2 콘솔**: http://localhost:5032/h2-console

### Actuator 엔드포인트

- **Health Check**: http://localhost:5032/actuator/health
- **Info**: http://localhost:5032/actuator/info
- **Metrics**: http://localhost:5032/actuator/metrics
- **Prometheus**: http://localhost:5032/actuator/prometheus

## 🗄️ 데이터베이스

### PostgreSQL (Docker)

- **Host**: localhost
- **Port**: 5432
- **Database**: popupmoah
- **Username**: popupmoah
- **Password**: popupmoah123

### Redis (Docker)

- **Host**: localhost
- **Port**: 6379

## 🔐 보안 설정

### JWT 설정

- JWT 토큰 만료 시간: 24시간
- 시크릿 키는 환경 변수로 설정

### Actuator 보안

- `/actuator/**` 엔드포인트는 개발 환경에서 허용
- 프로덕션에서는 적절한 보안 설정 필요

## 📝 로그

### 로그 위치

- **로컬**: `logs/popupmoah.log`
- **Docker**: 컨테이너 내부 `/app/logs/popupmoah.log`

### 로그 레벨

- **Root**: INFO
- **애플리케이션**: DEBUG
- **Spring Security**: DEBUG

## 🧪 테스트

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "*ControllerTest"

# 통합 테스트
./gradlew integrationTest
```

### Testcontainers

- PostgreSQL 컨테이너를 사용한 통합 테스트
- H2 인메모리 데이터베이스 사용

## 🚀 배포

### Docker 이미지 빌드

```bash
# 이미지 빌드
docker build -t popupmoah:latest .

# 이미지 실행
docker run -p 5032:5032 popupmoah:latest
```

### Docker Compose 배포

```bash
# 프로덕션 환경 변수 설정
export SPRING_PROFILES_ACTIVE=production

# 배포
docker compose -f docker-compose.prod.yml up -d
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/sgyj/popupmoah/
│   │   ├── config/          # 설정 클래스
│   │   ├── infra/           # 인프라 관련
│   │   └── module/          # 비즈니스 모듈
│   │       ├── category/    # 카테고리 관리
│   │       ├── community/   # 커뮤니티 기능
│   │       ├── crawling/    # 크롤링 시스템
│   │       └── popupstore/  # 팝업스토어 관리
│   └── resources/
│       ├── application.yml
│       └── application-docker.yml
└── test/                    # 테스트 코드
```

## 🔧 개발 가이드

### 새로운 모듈 추가

1. `src/main/java/com/sgyj/popupmoah/module/` 하위에 새 패키지 생성
2. Controller, Service, Repository, Entity 클래스 구현
3. 테스트 코드 작성

### API 문서화

- Swagger/OpenAPI 3.0 사용 예정
- API 문서는 `/swagger-ui.html`에서 확인 가능

## 🐛 문제 해결

### 일반적인 문제들

#### 포트 충돌

```bash
# 포트 사용 확인
lsof -i :5032
lsof -i :5432
lsof -i :6379

# Docker 컨테이너 정리
docker compose down
docker system prune
```

#### 데이터베이스 연결 실패

```bash
# PostgreSQL 컨테이너 상태 확인
docker compose ps postgres

# 로그 확인
docker compose logs postgres
```

#### Redis 연결 실패

```bash
# Redis 컨테이너 상태 확인
docker compose ps redis

# Redis CLI 접속
docker compose exec redis redis-cli
```

## 📞 지원

- **이슈 리포트**: GitHub Issues 사용
- **문서**: 이 README 및 코드 주석 참조
- **개발팀**: 프로젝트 메인테이너에게 문의

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
