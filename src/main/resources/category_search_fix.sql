-- =====================================================
-- 商品分类查找功能完善脚本
-- =====================================================
USE campus_trade;

-- 确保商品表有category字段
-- 根据schema.sql，product表已经有category字段 (VARCHAR(50))

-- 查看当前商品分类情况
SELECT category, COUNT(*) as count 
FROM product 
GROUP BY category 
ORDER BY count DESC;

-- 为没有分类的商品设置默认分类
UPDATE product SET category = '其他' WHERE category IS NULL OR category = '';

-- 插入更多测试商品数据，确保分类齐全
INSERT INTO product (name, category, price, stock, image, description, seller_id, seller_name, seller_phone, status, create_time, update_time) VALUES
('iPad Air 5 64GB', '电子产品', 3999.00, 2, 'https://picsum.photos/seed/ipad1/400/400', '2022款M1芯片，256G WiFi版，成色95新', 2, '张三', '13800138002', 1, NOW(), NOW()),
('AirPods Pro 2', '数码配件', 1299.00, 3, 'https://picsum.photos/seed/airpods2/400/400', '全新未拆封，支持主动降噪', 2, '张三', '13800138002', 1, NOW(), NOW()),
('线性代数教材', '图书教材', 25.00, 10, 'https://picsum.photos/seed/linebook/400/400', '同济版线性代数，考研必备，有少量笔记', 3, '李四', '13800138003', 1, NOW(), NOW()),
('索尼耳机 WH-1000XM4', '数码配件', 1599.00, 1, 'https://picsum.photos/seed/sony1/400/400', '黑色，几乎全新，降噪效果顶级', 2, '张三', '13800138002', 1, NOW(), NOW()),
('瑜伽垫加厚款', '运动器材', 59.00, 5, 'https://picsum.photos/seed/yoga1/400/400', '加厚10mm，183*61cm，送绑带', 3, '李四', '13800138003', 1, NOW(), NOW()),
('SK-II 神仙水 230ml', '美妆护肤', 899.00, 2, 'https://picsum.photos/seed/sk2/400/400', '日上购入，保真保正，有购买凭证', 2, '张三', '13800138002', 1, NOW(), NOW()),
('双肩包 Nike', '服饰鞋包', 189.00, 3, 'https://picsum.photos/seed/bag1/400/400', '黑色，30L大容量，适合学生通勤', 3, '李四', '13800138003', 1, NOW(), NOW()),
('电动牙刷 飞利浦', '生活用品', 299.00, 4, 'https://picsum.photos/seed/toothbrush/400/400', 'HX6730型号，9成新，包含3个刷头', 2, '张三', '13800138002', 1, NOW(), NOW());

-- 查看更新后的分类统计
SELECT category, COUNT(*) as count 
FROM product 
WHERE status = 1 
GROUP BY category 
ORDER BY count DESC;

-- 验证分类查询是否正常工作
-- 测试查询电子产品
SELECT id, name, category, price FROM product WHERE category = '电子产品' AND status = 1 LIMIT 5;

-- 测试查询图书教材
SELECT id, name, category, price FROM product WHERE category = '图书教材' AND status = 1 LIMIT 5;

-- 测试查询生活用品
SELECT id, name, category, price FROM product WHERE category = '生活用品' AND status = 1 LIMIT 5;

-- 统计总数
SELECT COUNT(*) as total_products, 
       COUNT(DISTINCT category) as total_categories 
FROM product 
WHERE status = 1;
