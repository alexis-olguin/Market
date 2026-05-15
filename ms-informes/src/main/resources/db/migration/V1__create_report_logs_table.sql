CREATE TABLE report_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL,
    generated_at DATETIME NOT NULL,
    description VARCHAR(500) NOT NULL
);
