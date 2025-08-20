# frontend

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

## Environment Configuration

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수들을 설정하세요:

```env
# API 설정
VITE_API_BASE_URL=http://localhost:8080/api
VITE_API_TIMEOUT=10000

# 카카오맵 API 키
# 카카오 개발자 콘솔(https://developers.kakao.com/)에서 발급받은 JavaScript 키를 입력하세요
VITE_KAKAO_MAP_API_KEY=your_kakao_map_api_key_here

# 네이버맵 API 키
# 네이버 클라우드 플랫폼(https://www.ncloud.com/)에서 발급받은 API 키를 입력하세요
VITE_NAVER_MAP_CLIENT_ID=your_naver_map_client_id_here
VITE_NAVER_MAP_CLIENT_SECRET=your_naver_map_client_secret_here
```

### 지도 API 키 발급 방법

#### 카카오맵 API 키 발급

1. [카카오 개발자 콘솔](https://developers.kakao.com/)에 접속
2. 애플리케이션 생성 또는 기존 애플리케이션 선택
3. 플랫폼 설정에서 Web 플랫폼 추가
4. JavaScript 키 복사하여 `VITE_KAKAO_MAP_API_KEY`에 설정

#### 네이버맵 API 키 발급

1. [네이버 클라우드 플랫폼](https://www.ncloud.com/)에 접속
2. Maps API 서비스 신청
3. Client ID와 Client Secret 발급
4. 각각 `VITE_NAVER_MAP_CLIENT_ID`, `VITE_NAVER_MAP_CLIENT_SECRET`에 설정

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```
