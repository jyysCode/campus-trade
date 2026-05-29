# 🚀 校园二手交易平台 - 项目启动指南

## 📋 环境要求

- **Java 21** (后端)
- **Node.js** (前端，推荐 v18+)
- **MySQL 8.0** (数据库)

---

## 📦 项目架构

```
campus-trade-backend/
├── src/                    # Spring Boot 后端代码
│   └── main/java/com/campus/trade/
│       └── CampusTradeApplication.java  # 后端主启动类
├── frontend/               # Vue 3 前端代码
│   └── src/
│       └── views/
└── src/main/resources/     # SQL脚本
```

---

## 🎯 启动步骤

### 第一步：数据库准备

确保MySQL服务已启动，然后执行以下步骤：

1. **创建数据库**：
```sql
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **初始化表结构**：
   执行 `src/main/resources/schema.sql`

3. **更新数据**：
   执行 `src/main/resources/update.sql`

4. **添加测试商品**：
   执行 `src/main/resources/init_products.sql`

5. **修复分类功能**：
   执行 `src/main/resources/category_search_fix.sql`

---

### 第二步：启动后端

#### 方式1：使用 IDEA 启动
1. 用 IntelliJ IDEA 打开项目根目录
2. 找到 [CampusTradeApplication.java](file:///d:/Intelij%20IDEA%201/campus-trade-backend/src/main/java/com/campus/trade/CampusTradeApplication.java)
3. 右键 → Run 'CampusTradeApplication'
4. 后端将在 **http://localhost:8080** 启动

#### 方式2：使用 Maven 命令
```bash
# 在项目根目录
mvn spring-boot:run
```

---

### 第三步：启动前端

#### 方式1：使用 npm 命令
```bash
cd frontend
npm install  # 首次运行安装依赖
npm run dev  # 启动开发服务器
```

#### 方式2：使用 VSCode 或其他编辑器
1. 在 `frontend` 目录打开终端
2. 运行 `npm install`
3. 运行 `npm run dev`
4. 前端将在 **http://localhost:5173** 启动

---

## 🔧 配置说明

### 数据库配置
文件位置: [src/main/resources/application.yml](file:///d:/Intelij%20IDEA%201/campus-trade-backend/src/main/resources/application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trade
    username: root
    password: jjs060903  # 根据实际情况修改
```

### 前端代理配置
文件位置: [frontend/vite.config.js](file:///d:/Intelij%20IDEA%201/campus-trade-backend/frontend/vite.config.js)

```javascript
server: {
  port: 5173,
  proxy: {
    '/api': 'http://localhost:8080',  // 后端地址
  }
}
```

---

## 📖 访问地址

启动成功后，访问以下地址：

- **前端页面**: http://localhost:5173
- **后端API**: http://localhost:8080/api

---

## 🧪 测试账号

根据 `update.sql` 文件，已有测试账号：
- **用户名**: admin / 普通用户
- **密码**: 123456

---

## ⚠️ 常见问题

1. **Java版本问题**: 确保使用 Java 21
2. **数据库连接失败**: 检查 MySQL 服务是否启动，密码是否正确
3. **端口被占用**: 修改 `application.yml` 或 `vite.config.js` 中的端口号
4. **前端依赖安装失败**: 删除 `node_modules`，重新运行 `npm install`

---

## ✅ 功能检查清单

- [ ] 后端正常启动在 8080 端口
- [ ] 前端正常启动在 5173 端口
- [ ] 数据库连接成功
- [ ] 可以浏览商品列表（无需登录）
- [ ] 分类筛选功能正常
- [ ] 未登录操作提示登录
- [ ] 登录后可以正常使用用户功能

---

## 📚 相关文档

- [功能实现总结](file:///d:/Intelij%20IDEA%201/campus-trade-backend/IMPLEMENTATION_SUMMARY.md)
