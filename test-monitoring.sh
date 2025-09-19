#!/bin/bash

# 팝업모아 모니터링 시스템 테스트 스크립트
# ELK Stack, Prometheus, Grafana 연동 테스트

set -e

echo "🚀 팝업모아 모니터링 시스템 테스트 시작..."

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 서비스 상태 확인 함수
check_service() {
    local service_name=$1
    local service_url=$2
    local max_attempts=30
    local attempt=1

    log_info "서비스 상태 확인: $service_name ($service_url)"

    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$service_url" > /dev/null 2>&1; then
            log_success "$service_name 서비스가 정상적으로 실행 중입니다."
            return 0
        fi

        log_info "시도 $attempt/$max_attempts - $service_name 서비스 대기 중..."
        sleep 2
        ((attempt++))
    done

    log_error "$service_name 서비스가 응답하지 않습니다."
    return 1
}

# Elasticsearch 테스트
test_elasticsearch() {
    log_info "Elasticsearch 연결 테스트 시작..."
    
    if check_service "Elasticsearch" "http://localhost:9200"; then
        # 클러스터 상태 확인
        local cluster_health=$(curl -s "http://localhost:9200/_cluster/health" | jq -r '.status')
        log_info "Elasticsearch 클러스터 상태: $cluster_health"
        
        # 인덱스 목록 확인
        local indices=$(curl -s "http://localhost:9200/_cat/indices?v")
        log_info "Elasticsearch 인덱스 목록:"
        echo "$indices"
        
        log_success "Elasticsearch 테스트 완료"
    else
        log_error "Elasticsearch 테스트 실패"
        return 1
    fi
}

# Kibana 테스트
test_kibana() {
    log_info "Kibana 연결 테스트 시작..."
    
    if check_service "Kibana" "http://localhost:5601"; then
        # Kibana 상태 확인
        local kibana_status=$(curl -s "http://localhost:5601/api/status" | jq -r '.status.overall.state')
        log_info "Kibana 상태: $kibana_status"
        
        log_success "Kibana 테스트 완료"
    else
        log_error "Kibana 테스트 실패"
        return 1
    fi
}

# Prometheus 테스트
test_prometheus() {
    log_info "Prometheus 연결 테스트 시작..."
    
    if check_service "Prometheus" "http://localhost:9090"; then
        # Prometheus 상태 확인
        local prometheus_status=$(curl -s "http://localhost:9090/-/healthy")
        log_info "Prometheus 상태: $prometheus_status"
        
        # 타겟 상태 확인
        local targets=$(curl -s "http://localhost:9090/api/v1/targets" | jq -r '.data.activeTargets[] | select(.health == "up") | .labels.job')
        log_info "활성 타겟: $targets"
        
        log_success "Prometheus 테스트 완료"
    else
        log_error "Prometheus 테스트 실패"
        return 1
    fi
}

# Grafana 테스트
test_grafana() {
    log_info "Grafana 연결 테스트 시작..."
    
    if check_service "Grafana" "http://localhost:3000"; then
        # Grafana 상태 확인
        local grafana_status=$(curl -s "http://localhost:3000/api/health" | jq -r '.database')
        log_info "Grafana 데이터베이스 상태: $grafana_status"
        
        # 데이터소스 확인
        local datasources=$(curl -s "http://admin:admin@localhost:3000/api/datasources" | jq -r '.[].name')
        log_info "Grafana 데이터소스: $datasources"
        
        log_success "Grafana 테스트 완료"
    else
        log_error "Grafana 테스트 실패"
        return 1
    fi
}

# 애플리케이션 메트릭 테스트
test_application_metrics() {
    log_info "애플리케이션 메트릭 테스트 시작..."
    
    local app_url="http://localhost:8080"
    
    if check_service "팝업모아 애플리케이션" "$app_url/actuator/health"; then
        # Health Check
        local health_status=$(curl -s "$app_url/actuator/health" | jq -r '.status')
        log_info "애플리케이션 상태: $health_status"
        
        # 메트릭 확인
        local metrics=$(curl -s "$app_url/actuator/metrics" | jq -r '.names[]' | head -10)
        log_info "수집 중인 메트릭 (상위 10개):"
        echo "$metrics"
        
        # Prometheus 메트릭 확인
        local prometheus_metrics=$(curl -s "$app_url/actuator/prometheus" | grep -c "# HELP")
        log_info "Prometheus 메트릭 개수: $prometheus_metrics"
        
        log_success "애플리케이션 메트릭 테스트 완료"
    else
        log_error "애플리케이션 메트릭 테스트 실패"
        return 1
    fi
}

# 로그 수집 테스트
test_log_collection() {
    log_info "로그 수집 테스트 시작..."
    
    # 테스트 로그 생성
    local test_log='{"timestamp":"2025-09-01T12:30:00.000Z","level":"INFO","message":"모니터링 테스트 로그","service":"popupmoah","environment":"test"}'
    
    # Elasticsearch에 테스트 로그 전송
    local response=$(curl -s -X POST "http://localhost:9200/popupmoah-test-logs/_doc" \
        -H "Content-Type: application/json" \
        -d "$test_log")
    
    local doc_id=$(echo "$response" | jq -r '._id')
    
    if [ "$doc_id" != "null" ]; then
        log_success "테스트 로그가 Elasticsearch에 저장되었습니다. (ID: $doc_id)"
        
        # 로그 검색 테스트
        sleep 2
        local search_result=$(curl -s "http://localhost:9200/popupmoah-test-logs/_search?q=message:테스트" | jq -r '.hits.total.value')
        log_info "검색된 로그 개수: $search_result"
        
        # 테스트 인덱스 삭제
        curl -s -X DELETE "http://localhost:9200/popupmoah-test-logs" > /dev/null
        log_info "테스트 인덱스가 삭제되었습니다."
        
        log_success "로그 수집 테스트 완료"
    else
        log_error "로그 수집 테스트 실패"
        return 1
    fi
}

# 성능 테스트
test_performance() {
    log_info "성능 테스트 시작..."
    
    local app_url="http://localhost:8080"
    
    # API 응답 시간 테스트
    local response_time=$(curl -s -w "%{time_total}" -o /dev/null "$app_url/actuator/health")
    log_info "API 응답 시간: ${response_time}초"
    
    # 동시 요청 테스트
    log_info "동시 요청 테스트 (10개 요청)..."
    local start_time=$(date +%s.%N)
    
    for i in {1..10}; do
        curl -s "$app_url/actuator/health" > /dev/null &
    done
    wait
    
    local end_time=$(date +%s.%N)
    local duration=$(echo "$end_time - $start_time" | bc)
    log_info "10개 동시 요청 처리 시간: ${duration}초"
    
    log_success "성능 테스트 완료"
}

# 메인 테스트 실행
main() {
    log_info "팝업모아 모니터링 시스템 종합 테스트 시작"
    echo "=========================================="
    
    local failed_tests=0
    
    # 각 서비스 테스트
    test_elasticsearch || ((failed_tests++))
    echo "------------------------------------------"
    
    test_kibana || ((failed_tests++))
    echo "------------------------------------------"
    
    test_prometheus || ((failed_tests++))
    echo "------------------------------------------"
    
    test_grafana || ((failed_tests++))
    echo "------------------------------------------"
    
    test_application_metrics || ((failed_tests++))
    echo "------------------------------------------"
    
    test_log_collection || ((failed_tests++))
    echo "------------------------------------------"
    
    test_performance || ((failed_tests++))
    echo "=========================================="
    
    # 테스트 결과 요약
    if [ $failed_tests -eq 0 ]; then
        log_success "모든 모니터링 테스트가 성공적으로 완료되었습니다! 🎉"
        echo ""
        log_info "모니터링 대시보드 접속 정보:"
        echo "  - Grafana: http://localhost:3000 (admin/admin)"
        echo "  - Kibana: http://localhost:5601"
        echo "  - Prometheus: http://localhost:9090"
        echo "  - Elasticsearch: http://localhost:9200"
    else
        log_error "$failed_tests 개의 테스트가 실패했습니다."
        exit 1
    fi
}

# 스크립트 실행
main "$@"