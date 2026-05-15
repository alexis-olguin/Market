CREATE TABLE sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    user_id BIGINT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE sale_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_sale FOREIGN KEY (sale_id) REFERENCES sales(id)
);
