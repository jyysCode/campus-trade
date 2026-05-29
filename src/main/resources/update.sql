USE campus_trade;

-- 添加 user_type 字段
ALTER TABLE user ADD COLUMN user_type INT DEFAULT 0;

-- 更新现有用户密码（添加字母，符合字母+数字规则）
-- 原密码是 123456，md5加密后是 e10adc3949ba59abbe56e057f20f883e
-- 新密码改为 123abc，md5加密后是 e99a18c428cb38d5f260853678922e03
UPDATE user SET password = 'e99a18c428cb38d5f260853678922e03' WHERE phone = '13800138001';
UPDATE user SET password = 'e99a18c428cb38d5f260853678922e03' WHERE phone = '13800138002';
UPDATE user SET password = 'e99a18c428cb38d5f260853678922e03' WHERE phone = '13800138003';

-- 添加管理员账户 root / 123456
-- 密码 123456 的 md5 是 e10adc3949ba59abbe56e057f20f883e
INSERT INTO user (username, password, phone, user_type, create_time, update_time) 
VALUES ('root', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', 1, NOW(), NOW());

SELECT * FROM user;