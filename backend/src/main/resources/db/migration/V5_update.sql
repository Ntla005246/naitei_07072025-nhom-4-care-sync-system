-- Users: Add column

ALTER TABLE users
ADD COLUMN verification_token VARCHAR(255) NULL,
ADD COLUMN token_expiry_date DATETIME NULL;

-- Thêm UNIQUE constraint để tìm kiếm token nhanh hơn
ALTER TABLE users ADD CONSTRAINT uk_verification_token UNIQUE (verification_token);
