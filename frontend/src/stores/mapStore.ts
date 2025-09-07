import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient } from '@/config/api'
import type {
  Coordinates,
  MapProvider,
  PopupStoreLocation,
  PlaceSearchRequest,
  PlaceSearchResponse,
  PlaceInfo,
} from '@/types'

export const useMapStore = defineStore('map', () => {
  // State
  const selectedProvider = ref<MapProvider>('kakao')
  const currentLocation = ref<Coordinates | null>(null)
  const popupStoreLocations = ref<PopupStoreLocation[]>([])
  const searchResults = ref<PlaceInfo[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const hasCurrentLocation = computed(() => currentLocation.value !== null)
  const hasPopupStores = computed(() => popupStoreLocations.value.length > 0)
  const hasSearchResults = computed(() => searchResults.value.length > 0)

  // Actions
  const setProvider = (provider: MapProvider) => {
    selectedProvider.value = provider
  }

  const setCurrentLocation = (coordinates: Coordinates) => {
    currentLocation.value = coordinates
  }

  const clearCurrentLocation = () => {
    currentLocation.value = null
  }

  const setPopupStoreLocations = (locations: PopupStoreLocation[]) => {
    popupStoreLocations.value = locations
  }

  const addPopupStoreLocation = (location: PopupStoreLocation) => {
    const existingIndex = popupStoreLocations.value.findIndex((l) => l.id === location.id)
    if (existingIndex >= 0) {
      popupStoreLocations.value[existingIndex] = location
    } else {
      popupStoreLocations.value.push(location)
    }
  }

  const removePopupStoreLocation = (id: number) => {
    const index = popupStoreLocations.value.findIndex((l) => l.id === id)
    if (index >= 0) {
      popupStoreLocations.value.splice(index, 1)
    }
  }

  const clearPopupStoreLocations = () => {
    popupStoreLocations.value = []
  }

  const setSearchResults = (results: PlaceInfo[]) => {
    searchResults.value = results
  }

  const clearSearchResults = () => {
    searchResults.value = []
  }

  const setLoading = (loading: boolean) => {
    isLoading.value = loading
  }

  const setError = (errorMessage: string | null) => {
    error.value = errorMessage
  }

  const clearError = () => {
    error.value = null
  }

  // API Actions
  const fetchPopupStoreLocations = async () => {
    try {
      setLoading(true)
      clearError()

      const response = await apiClient.get('/popupstores/locations')
      const locations: PopupStoreLocation[] = response.data.data

      setPopupStoreLocations(locations)
      return locations
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message || '팝업스토어 위치 정보를 가져오는데 실패했습니다.'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const searchPlaces = async (request: PlaceSearchRequest): Promise<PlaceSearchResponse> => {
    try {
      setLoading(true)
      clearError()

      const response = await apiClient.post('/maps/places/search', request)
      const searchResponse: PlaceSearchResponse = response.data.data

      setSearchResults(searchResponse.places)
      return searchResponse
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || '장소 검색에 실패했습니다.'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const geocodeAddress = async (address: string): Promise<Coordinates> => {
    try {
      setLoading(true)
      clearError()

      const response = await apiClient.get('/maps/geocode', {
        params: { address },
      })

      return response.data.data
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || '주소 변환에 실패했습니다.'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const reverseGeocode = async (coordinates: Coordinates): Promise<string> => {
    try {
      setLoading(true)
      clearError()

      const response = await apiClient.get('/maps/reverse-geocode', {
        params: {
          lat: coordinates.lat,
          lng: coordinates.lng,
        },
      })

      return response.data.data
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || '좌표 변환에 실패했습니다.'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const getCurrentLocationFromBrowser = (): Promise<Coordinates> => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by this browser.'))
        return
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const coordinates: Coordinates = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          }
          setCurrentLocation(coordinates)
          resolve(coordinates)
        },
        (error) => {
          let errorMessage = '위치 정보를 가져올 수 없습니다.'

          switch (error.code) {
            case error.PERMISSION_DENIED:
              errorMessage = '위치 정보 접근이 거부되었습니다.'
              break
            case error.POSITION_UNAVAILABLE:
              errorMessage = '위치 정보를 사용할 수 없습니다.'
              break
            case error.TIMEOUT:
              errorMessage = '위치 정보 요청이 시간 초과되었습니다.'
              break
          }

          setError(errorMessage)
          reject(new Error(errorMessage))
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 60000,
        },
      )
    })
  }

  const calculateDistance = (from: Coordinates, to: Coordinates): number => {
    const R = 6371000 // 지구 반지름 (미터)
    const lat1Rad = Math.toRadians(from.lat)
    const lat2Rad = Math.toRadians(to.lat)
    const deltaLatRad = Math.toRadians(to.lat - from.lat)
    const deltaLonRad = Math.toRadians(to.lng - from.lng)

    const a =
      Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
      Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return R * c
  }

  const getNearbyPopupStores = (
    center: Coordinates,
    radius: number = 1000,
  ): PopupStoreLocation[] => {
    return popupStoreLocations.value
      .filter((location) => {
        const distance = calculateDistance(center, location.coordinates)
        return distance <= radius
      })
      .sort((a, b) => {
        const distanceA = calculateDistance(center, a.coordinates)
        const distanceB = calculateDistance(center, b.coordinates)
        return distanceA - distanceB
      })
  }

  return {
    // State
    selectedProvider,
    currentLocation,
    popupStoreLocations,
    searchResults,
    isLoading,
    error,

    // Getters
    hasCurrentLocation,
    hasPopupStores,
    hasSearchResults,

    // Actions
    setProvider,
    setCurrentLocation,
    clearCurrentLocation,
    setPopupStoreLocations,
    addPopupStoreLocation,
    removePopupStoreLocation,
    clearPopupStoreLocations,
    setSearchResults,
    clearSearchResults,
    setLoading,
    setError,
    clearError,

    // API Actions
    fetchPopupStoreLocations,
    searchPlaces,
    geocodeAddress,
    reverseGeocode,
    getCurrentLocationFromBrowser,
    calculateDistance,
    getNearbyPopupStores,
  }
})
