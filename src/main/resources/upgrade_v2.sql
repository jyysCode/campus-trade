USE campus_trade;

ALTER TABLE user ADD COLUMN nickname VARCHAR(50) COMMENT '昵称';
ALTER TABLE user ADD COLUMN avatar VARCHAR(255) COMMENT '头像URL';
ALTER TABLE user ADD COLUMN email VARCHAR(100) COMMENT '邮箱';
ALTER TABLE user ADD COLUMN status INT DEFAULT 0 COMMENT '账号状态';
ALTER TABLE user ADD COLUMN deleted INT DEFAULT 0 COMMENT '逻辑删除';

ALTER TABLE product ADD COLUMN original_price DECIMAL(10,2) COMMENT '原价';
ALTER TABLE product ADD COLUMN sales INT DEFAULT 0 COMMENT '销量';
ALTER TABLE product ADD COLUMN views INT DEFAULT 0 COMMENT '浏览量';
ALTER TABLE product ADD COLUMN location VARCHAR(100) COMMENT '发货地';
ALTER TABLE product ADD COLUMN `condition` VARCHAR(50) COMMENT '成色';
ALTER TABLE product ADD COLUMN deleted INT DEFAULT 0 COMMENT '逻辑删除';

ALTER TABLE `order` ADD COLUMN order_no VARCHAR(32) COMMENT '订单编号';

ALTER TABLE message ADD COLUMN type VARCHAR(20) DEFAULT 'text' COMMENT '消息类型';

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_fav_user (user_id),
    INDEX idx_fav_product (product_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_cart_user (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(50),
    rating INT NOT NULL,
    content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_review_product (product_id),
    INDEX idx_review_user (user_id),
    INDEX idx_review_order (order_id),
    UNIQUE KEY uk_order_review (order_id),
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

UPDATE `order` SET order_no = CONCAT('ORD', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 8, '0')) WHERE order_no IS NULL OR order_no = '';
