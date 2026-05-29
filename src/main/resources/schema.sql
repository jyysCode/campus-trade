CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    user_type INT DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 1,
    image VARCHAR(255),
    description TEXT,
    seller_id BIGINT,
    seller_name VARCHAR(50),
    seller_phone VARCHAR(20),
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (seller_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    product_name VARCHAR(100),
    product_image VARCHAR(255),
    user_id BIGINT,
    seller_id BIGINT,
    quantity INT DEFAULT 1,
    price DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    status INT DEFAULT 0,
    address TEXT,
    remark TEXT,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (seller_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT,
    to_user_id BIGINT,
    content TEXT,
    product_id BIGINT,
    is_read INT DEFAULT 0,
    create_time DATETIME,
    FOREIGN KEY (from_user_id) REFERENCES user(id),
    FOREIGN KEY (to_user_id) REFERENCES user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);