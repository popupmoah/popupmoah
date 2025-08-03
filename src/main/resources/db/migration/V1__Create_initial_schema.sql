-- 팝업스토어 플랫폼 초기 스키마 생성
-- V1__Create_initial_schema.sql

-- 카테고리 테이블
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    sort_order DECIMAL(10,2) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 회원 테이블
CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    nickname VARCHAR(50),
    profile_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 팝업스토어 테이블
CREATE TABLE popup_store (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    reservation_date TIMESTAMP,
    store_number VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(500),
    category_id BIGINT REFERENCES category(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 팝업스토어 이미지 테이블
CREATE TABLE popup_store_image (
    id BIGSERIAL PRIMARY KEY,
    popup_store_id BIGINT NOT NULL REFERENCES popup_store(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    image_order INTEGER DEFAULT 0,
    is_main BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 리뷰 테이블
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    popup_store_id BIGINT NOT NULL REFERENCES popup_store(id) ON DELETE CASCADE,
    member_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 댓글 테이블
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    popup_store_id BIGINT NOT NULL REFERENCES popup_store(id) ON DELETE CASCADE,
    member_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES comment(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_popup_store_category ON popup_store(category_id);
CREATE INDEX idx_popup_store_dates ON popup_store(start_date, end_date);
CREATE INDEX idx_popup_store_location ON popup_store(latitude, longitude);
CREATE INDEX idx_review_popup_store ON review(popup_store_id);
CREATE INDEX idx_review_member ON review(member_id);
CREATE INDEX idx_comment_popup_store ON comment(popup_store_id);
CREATE INDEX idx_comment_member ON comment(member_id);
CREATE INDEX idx_comment_parent ON comment(parent_id);
CREATE INDEX idx_popup_store_image_store ON popup_store_image(popup_store_id);

-- 기본 카테고리 데이터 삽입
INSERT INTO category (name, description, sort_order, active) VALUES
('음식', '음식 관련 팝업스토어', 1.0, true),
('패션', '패션 관련 팝업스토어', 2.0, true),
('뷰티', '뷰티 관련 팝업스토어', 3.0, true),
('라이프스타일', '라이프스타일 관련 팝업스토어', 4.0, true),
('테크', '테크 관련 팝업스토어', 5.0, true),
('아트', '아트 관련 팝업스토어', 6.0, true); 