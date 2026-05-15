CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_reference VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
);
