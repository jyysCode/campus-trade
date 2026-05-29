-- =====================================================
-- 数据库健康检查与修复脚本
-- =====================================================
USE campus_trade;

-- =====================================================
-- 1. 检查商品表结构
-- =====================================================
DESCRIBE product;

-- =====================================================
-- 2. 检查是否有缺失的字段，并添加可选字段（增强功能）
-- =====================================================

-- 添加可选字段（如果不存在）
ALTER TABLE product ADD COLUMN IF NOT EXISTS sales INT DEFAULT 0 COMMENT '销量';
ALTER TABLE product ADD COLUMN IF NOT EXISTS views INT DEFAULT 0 COMMENT '浏览量';
ALTER TABLE product ADD COLUMN IF NOT EXISTS original_price DECIMAL(10,2) DEFAULT 0 COMMENT '原价';
ALTER TABLE product ADD COLUMN IF NOT EXISTS location VARCHAR(100) DEFAULT '校园' COMMENT '发货地';
ALTER TABLE product ADD COLUMN IF NOT EXISTS tags VARCHAR(255) COMMENT '标签，逗号分隔';
ALTER TABLE product ADD COLUMN IF NOT EXISTS `condition` VARCHAR(50) DEFAULT '几乎全新' COMMENT '成色';

-- =====================================================
-- 3. 检查并确保必要的索引存在
-- =====================================================

-- 为category字段添加索引（如果不存在）
-- 注意：MySQL不支持 ADD INDEX IF NOT EXISTS，需要先检查
-- SHOW INDEX FROM product WHERE Column_name = 'category';

-- =====================================================
-- 4. 检查数据完整性
-- =====================================================

-- 查看所有商品
SELECT id, name, category, price, status FROM product;

-- 检查是否有未设置分类的商品
SELECT id, name FROM product WHERE category IS NULL OR category = '';

-- 为没有分类的商品设置默认值
UPDATE product SET category = '其他' WHERE category IS NULL OR category = '';

-- 检查是否有未审核的商品
SELECT id, name, status FROM product WHERE status = 0;

-- =====================================================
-- 5. 统计各分类的商品数量
-- =====================================================
SELECT 
    category,
    COUNT(*) as product_count,
    AVG(price) as avg_price,
    MIN(price) as min_price,
    MAX(price) as max_price
FROM product 
WHERE status = 1 
GROUP BY category
ORDER BY product_count DESC;

-- =====================================================
-- 6. 检查用户表结构
-- =====================================================
DESCRIBE user;

-- =====================================================
-- 7. 检查用户数据
-- =====================================================
SELECT id, username, phone, user_type, create_time FROM user;

-- =====================================================
-- 8. 检查订单表结构
-- =====================================================
DESCRIBE `order`;

-- =====================================================
-- 9. 验证分类查询功能
-- =====================================================

-- 测试电子产品分类
SELECT COUNT(*) as 电子产品数量 
FROM product 
WHERE category = '电子产品' AND status = 1;

-- 测试图书教材分类
SELECT COUNT(*) as 图书教材数量 
FROM product 
WHERE category = '图书教材' AND status = 1;

-- =====================================================
-- 10. 数据库健康检查总结
-- =====================================================
SELECT 
    '商品总数' as metric,
    COUNT(*) as value
FROM product
UNION ALL
SELECT 
    '已审核商品数',
    COUNT(*)
FROM product WHERE status = 1
UNION ALL
SELECT 
    '待审核商品数',
    COUNT(*)
FROM product WHERE status = 0
UNION ALL
SELECT 
    '分类数量',
    COUNT(DISTINCT category)
FROM product WHERE category IS NOT NULL;
