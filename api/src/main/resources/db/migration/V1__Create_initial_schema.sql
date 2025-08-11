-- 팝업스토어 카테고리 테이블
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES category(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 팝업스토어 테이블
CREATE TABLE popup_store (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
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

-- 리뷰 테이블
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    popup_store_id BIGINT NOT NULL REFERENCES popup_store(id) ON DELETE CASCADE,
    member_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 댓글 테이블 (계층형 구조)
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    popup_store_id BIGINT NOT NULL REFERENCES popup_store(id) ON DELETE CASCADE,
    member_id BIGINT NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES comment(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_popup_store_category ON popup_store(category_id);
CREATE INDEX idx_popup_store_location ON popup_store(latitude, longitude);
CREATE INDEX idx_popup_store_dates ON popup_store(start_date, end_date);
CREATE INDEX idx_popup_store_image_store ON popup_store_image(popup_store_id);
CREATE INDEX idx_review_store ON review(popup_store_id);
CREATE INDEX idx_review_member ON review(member_id);
CREATE INDEX idx_comment_store ON comment(popup_store_id);
CREATE INDEX idx_comment_member ON comment(member_id);
CREATE INDEX idx_comment_parent ON comment(parent_id); 