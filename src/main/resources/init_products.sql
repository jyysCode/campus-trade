USE campus_trade;

-- 添加商品状态字段
ALTER TABLE product ADD COLUMN IF NOT EXISTS status INT DEFAULT 0 COMMENT '0:待审核,1:已通过,2:已拒绝';

-- 插入测试商品数据
INSERT INTO product (name, category, price, stock, image, description, seller_id, seller_name, seller_phone, status, create_time, update_time) VALUES
('iPhone 14 Pro 256GB', '电子产品', 6599.00, 3, 'https://picsum.photos/seed/phone1/400/400', '几乎全新，使用不到半年，无任何划痕，箱说齐全', 2, '张三', '13800138002', 1, NOW(), NOW()),
('MacBook Pro 14英寸 M2', '电子产品', 13999.00, 2, 'https://picsum.photos/seed/laptop1/400/400', '2023款M2芯片，16G+512G，保护完好，功能正常', 2, '张三', '13800138002', 1, NOW(), NOW()),
('高等数学同济第七版', '图书教材', 28.00, 5, 'https://picsum.photos/seed/book1/400/400', '同济第七版高数，考研必备，有笔记', 3, '李四', '13800138003', 1, NOW(), NOW()),
('大学英语四级词汇书', '图书教材', 35.00, 8, 'https://picsum.photos/seed/book2/400/400', '新东方四级词汇乱序版，几乎全新', 3, '李四', '13800138003', 1, NOW(), NOW()),
('羽毛球拍尤尼克斯', '运动器材', 299.00, 2, 'https://picsum.photos/seed/sports1/400/400', '尤尼克斯入门级球拍，使用3个月，送球包', 2, '张三', '13800138002', 1, NOW(), NOW()),
('篮球斯伯丁', '运动器材', 89.00, 4, 'https://picsum.photos/seed/sports2/400/400', '斯伯丁7号球，适合室内室外使用', 3, '李四', '13800138003', 1, NOW(), NOW()),
('小米手环7', '电子产品', 199.00, 6, 'https://picsum.photos/seed/watch1/400/400', '全新未拆封，功能正常，有发票', 2, '张三', '13800138002', 1, NOW(), NOW()),
('耐克运动鞋42码', '服饰鞋包', 459.00, 1, 'https://picsum.photos/seed/shoes1/400/400', 'Nike Air Max系列，42码，9成新', 3, '李四', '13800138003', 1, NOW(), NOW()),
('台灯LED护眼灯', '生活用品', 79.00, 10, 'https://picsum.photos/seed/lamp1/400/400', '可调节亮度，USB供电，全新', 2, '张三', '13800138002', 1, NOW(), NOW()),
('保温杯500ml', '生活用品', 45.00, 12, 'https://picsum.photos/seed/cup1/400/400', '真空不锈钢保温杯，保温效果好', 3, '李四', '13800138003', 1, NOW(), NOW());

-- 查看商品数据
SELECT * FROM product;