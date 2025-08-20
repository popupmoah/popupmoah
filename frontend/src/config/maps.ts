/**
 * 지도 API 설정
 * 카카오맵과 네이버맵 API 키 및 설정을 관리합니다.
 */

// 환경 변수에서 API 키 가져오기
export const MAP_CONFIG = {
  // 카카오맵 API 설정
  KAKAO: {
    API_KEY: import.meta.env.VITE_KAKAO_MAP_API_KEY || '',
    SERVICES: {
      MAP: 'kakao.maps.services',
      PLACES: 'kakao.maps.services.Places',
      GEOCORDER: 'kakao.maps.services.Geocoder',
    },
    DEFAULT_OPTIONS: {
      center: new kakao.maps.LatLng(37.5665, 126.978), // 서울시청
      level: 3,
    },
  },

  // 네이버맵 API 설정
  NAVER: {
    CLIENT_ID: import.meta.env.VITE_NAVER_MAP_CLIENT_ID || '',
    CLIENT_SECRET: import.meta.env.VITE_NAVER_MAP_CLIENT_SECRET || '',
    DEFAULT_OPTIONS: {
      center: new naver.maps.LatLng(37.5665, 126.978), // 서울시청
      zoom: 10,
    },
  },

  // 공통 설정
  COMMON: {
    DEFAULT_ZOOM: 10,
    DEFAULT_CENTER: {
      lat: 37.5665,
      lng: 126.978,
    },
    MARKER_OPTIONS: {
      KAKAO: {
        imageSrc: '/images/marker.png',
        imageSize: new kakao.maps.Size(24, 35),
        imageOption: { offset: new kakao.maps.Point(12, 35) },
      },
      NAVER: {
        icon: {
          content: '<div class="custom-marker"></div>',
          size: new naver.maps.Size(24, 35),
          anchor: new naver.maps.Point(12, 35),
        },
      },
    },
  },
} as const

// API 키 유효성 검사
export const validateMapApiKeys = (): { kakao: boolean; naver: boolean } => {
  return {
    kakao: !!MAP_CONFIG.KAKAO.API_KEY,
    naver: !!(MAP_CONFIG.NAVER.CLIENT_ID && MAP_CONFIG.NAVER.CLIENT_SECRET),
  }
}

// 지도 타입 정의
export type MapProvider = 'kakao' | 'naver'

// 좌표 타입 정의
export interface Coordinates {
  lat: number
  lng: number
}

// 팝업스토어 위치 정보 타입
export interface PopupStoreLocation {
  id: number
  name: string
  address: string
  coordinates: Coordinates
  category?: string
  active?: boolean
}

// 지도 이벤트 타입
export interface MapEvent {
  type: 'click' | 'dragend' | 'zoom_changed'
  coordinates?: Coordinates
  zoom?: number
}

// 마커 클러스터링 설정
export const CLUSTER_CONFIG = {
  KAKAO: {
    minClusterSize: 2,
    maxClusterSize: 10,
    gridSize: 60,
    averageCenter: true,
    minLevel: 1,
  },
  NAVER: {
    minClusterSize: 2,
    maxClusterSize: 10,
    gridSize: 60,
    averageCenter: true,
    minLevel: 1,
  },
} as const

export default MAP_CONFIG
