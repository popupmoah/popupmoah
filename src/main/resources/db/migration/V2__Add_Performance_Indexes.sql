-- 성능 최적화를 위한 인덱스 추가

-- 팝업스토어 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_popup_stores_active ON popup_stores(active);
CREATE INDEX IF NOT EXISTS idx_popup_stores_category ON popup_stores(category);
CREATE INDEX IF NOT EXISTS idx_popup_stores_status ON popup_stores(status);
CREATE INDEX IF NOT EXISTS idx_popup_stores_start_date ON popup_stores(start_date);
CREATE INDEX IF NOT EXISTS idx_popup_stores_end_date ON popup_stores(end_date);
CREATE INDEX IF NOT EXISTS idx_popup_stores_location ON popup_stores(location);
CREATE INDEX IF NOT EXISTS idx_popup_stores_created_at ON popup_stores(created_at);

-- 복합 인덱스 (자주 함께 사용되는 조건들)
CREATE INDEX IF NOT EXISTS idx_popup_stores_active_dates ON popup_stores(active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_popup_stores_category_active ON popup_stores(category, active);
CREATE INDEX IF NOT EXISTS idx_popup_stores_status_created ON popup_stores(status, created_at);

-- 텍스트 검색을 위한 인덱스 (PostgreSQL의 경우)
-- CREATE INDEX IF NOT EXISTS idx_popup_stores_name_text ON popup_stores USING gin(to_tsvector('korean', name));
-- CREATE INDEX IF NOT EXISTS idx_popup_stores_description_text ON popup_stores USING gin(to_tsvector('korean', description));

-- 예약 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_reservations_member_id ON reservations(member_id);
CREATE INDEX IF NOT EXISTS idx_reservations_popup_store_id ON reservations(popup_store_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_date_time ON reservations(reservation_date_time);
CREATE INDEX IF NOT EXISTS idx_reservations_created_at ON reservations(created_at);

-- 복합 인덱스
CREATE INDEX IF NOT EXISTS idx_reservations_member_status ON reservations(member_id, status);
CREATE INDEX IF NOT EXISTS idx_reservations_popupstore_status ON reservations(popup_store_id, status);
CREATE INDEX IF NOT EXISTS idx_reservations_date_status ON reservations(reservation_date_time, status);

-- 회원 테이블 인덱스 (기존 테이블이 있다면)
-- CREATE INDEX IF NOT EXISTS idx_members_email ON members(email);
-- CREATE INDEX IF NOT EXISTS idx_members_status ON members(status);
-- CREATE INDEX IF NOT EXISTS idx_members_created_at ON members(created_at);

-- 카테고리 테이블 인덱스 (기존 테이블이 있다면)
-- CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);
-- CREATE INDEX IF NOT EXISTS idx_categories_active ON categories(active);



