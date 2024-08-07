-- 파일 업로드 테이블 생성
CREATE TABLE file_upload (
    id BIGSERIAL PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_extension VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    upload_type VARCHAR(20) NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(20),
    thumbnail_url VARCHAR(500),
    optimized_url VARCHAR(500),
    image_width INTEGER,
    image_height INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- 인덱스 생성
CREATE INDEX idx_file_upload_reference ON file_upload(reference_type, reference_id);
CREATE INDEX idx_file_upload_active ON file_upload(active);
CREATE INDEX idx_file_upload_upload_type ON file_upload(upload_type);
CREATE INDEX idx_file_upload_created_at ON file_upload(created_at);

-- 업데이트 트리거 함수 생성
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 업데이트 트리거 생성
CREATE TRIGGER update_file_upload_updated_at 
    BEFORE UPDATE ON file_upload 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column(); 