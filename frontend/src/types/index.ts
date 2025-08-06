// Google Maps API 타입 정의
declare global {
  interface Window {
    google: typeof google
  }
}

declare namespace google {
  namespace maps {
    class Map {
      constructor(element: HTMLElement, options?: MapOptions)
      setCenter(latLng: LatLng | LatLngLiteral): void
      addListener(eventName: string, handler: Function): void
    }

    class Marker {
      constructor(options?: MarkerOptions)
      setMap(map: Map | null): void
      addListener(eventName: string, handler: Function): void
    }

    class InfoWindow {
      constructor(options?: InfoWindowOptions)
      open(map: Map, anchor?: Marker): void
    }

    class Geocoder {
      geocode(
        request: GeocoderRequest,
        callback: (results: GeocoderResult[] | null, status: GeocoderStatus) => void,
      ): void
    }

    interface MapOptions {
      center?: LatLng | LatLngLiteral
      zoom?: number
      mapTypeId?: MapTypeId
      mapTypeControl?: boolean
      streetViewControl?: boolean
      fullscreenControl?: boolean
      zoomControl?: boolean
      gestureHandling?: string
      styles?: MapTypeStyle[]
    }

    interface MarkerOptions {
      position?: LatLng | LatLngLiteral
      map?: Map
      title?: string
      animation?: Animation
    }

    interface InfoWindowOptions {
      content?: string
    }

    interface GeocoderRequest {
      address?: string
    }

    interface GeocoderResult {
      geometry: {
        location: LatLng
      }
    }

    interface LatLng {
      lat(): number
      lng(): number
    }

    interface LatLngLiteral {
      lat: number
      lng: number
    }

    interface MapMouseEvent {
      latLng?: LatLng
    }

    enum MapTypeId {
      ROADMAP = 'roadmap',
    }

    enum Animation {
      DROP = 2,
    }

    enum GeocoderStatus {
      OK = 'OK',
    }

    interface MapTypeStyle {
      featureType?: string
      elementType?: string
      stylers?: Array<{ [key: string]: any }>
    }
  }
}

// 기본 API 응답 타입
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message?: string
  timestamp: string
}

// 페이지네이션 타입
export interface PaginationParams {
  page: number
  size: number
  sort?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
  first: boolean
  last: boolean
}

// 팝업스토어 관련 타입
export interface PopupStore {
  id: number
  name: string
  description: string
  location: string
  address: string
  period: string
  startDate: string
  endDate: string
  rating: number
  categoryId: number
  categoryName: string
  status: '진행중' | '예정' | '종료'
  latitude?: number
  longitude?: number
  images?: string[]
  createdAt: string
  updatedAt: string
}

export interface CreatePopupStoreRequest {
  name: string
  description: string
  location: string
  address: string
  startDate: string
  endDate: string
  categoryId: number
  latitude?: number
  longitude?: number
  images?: string[]
}

export interface UpdatePopupStoreRequest extends Partial<CreatePopupStoreRequest> {
  id: number
}

// 카테고리 관련 타입
export interface Category {
  id: number
  name: string
  description?: string
  parentId?: number
  level: number
  orderIndex: number
  isActive: boolean
  children?: Category[]
}

export interface CreateCategoryRequest {
  name: string
  description?: string
  parentId?: number
  orderIndex?: number
  isActive?: boolean
}

// 파일 관련 타입
export interface FileUpload {
  id: number
  originalName: string
  storedName: string
  fileSize: number
  mimeType: string
  url: string
  uploadedAt: string
}

export interface FileUploadResponse {
  fileId: number
  fileName: string
  fileUrl: string
  fileSize: number
}

// 리뷰 관련 타입
export interface Review {
  id: number
  popupStoreId: number
  userId: number
  userName: string
  rating: number
  content: string
  images?: string[]
  createdAt: string
  updatedAt: string
}

export interface CreateReviewRequest {
  popupStoreId: number
  rating: number
  content: string
  images?: File[]
}

// 댓글 관련 타입
export interface Comment {
  id: number
  popupStoreId: number
  userId: number
  userName: string
  content: string
  parentId?: number
  isDeleted: boolean
  createdAt: string
  updatedAt: string
  children?: Comment[]
}

export interface CreateCommentRequest {
  popupStoreId: number
  content: string
  parentId?: number
}

// 사용자 관련 타입
export interface User {
  id: number
  email: string
  name: string
  role: 'USER' | 'ADMIN'
  createdAt: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  name: string
}

export interface AuthResponse {
  user: User
  token: string
  refreshToken: string
}

// 검색 및 필터링 타입
export interface SearchParams {
  keyword?: string
  categoryId?: number
  status?: string
  location?: string
  startDate?: string
  endDate?: string
}

// 지도 관련 타입
export interface Location {
  latitude: number
  longitude: number
  address: string
}

// UI 관련 타입
export interface LoadingState {
  isLoading: boolean
  error: string | null
}

export interface FormState<T> extends LoadingState {
  data: T
  isValid: boolean
}
