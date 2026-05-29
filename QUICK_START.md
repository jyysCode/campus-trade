# 🚀 快速启动指南

由于当前环境限制，建议使用 **IntelliJ IDEA** 启动项目。以下是详细步骤：

---

## 📋 启动步骤

### 步骤1：打开项目
用 IntelliJ IDEA 打开 `d:\Intelij IDEA 1\campus-trade-backend` 文件夹

### 步骤2：启动后端
1. 在项目导航器中找到：
   ```
   src/main/java/com/campus/trade/
   └── CampusTradeApplication.java
   ```

2. **右键点击** `CampusTradeApplication.java`

3. 选择 **"Run 'CampusTradeApplication'"**

4. 等待启动，看到以下信息表示成功：
   ```
   Started CampusTradeApplication in X.XXX seconds
   ```

5. 后端运行在：**http://localhost:8080**

---

## 🌐 启动前端

### 方式A：使用Vite开发服务器（推荐 - 支持热更新）

1. 打开IDEA的Terminal（底部终端）

2. 进入前端目录：
   ```bash
   cd frontend
   ```

3. 安装依赖（如果还没安装）：
   ```bash
   npm install
   ```

4. 启动开发服务器：
   ```bash
   npm run dev
   ```

5. 看到以下信息表示成功：
   ```
   VITE v5.x.x  ready in XXX ms
   ➜  Local:   http://localhost:5173/
   ```

6. 在浏览器打开：**http://localhost:5173**

### 方式B：使用已构建的生产版本

前端已经构建完成，可以直接使用：

1. 确保后端在 **http://localhost:8080** 运行

2. 将 `frontend/dist` 目录部署到Web服务器

3. 或修改 `frontend/dist/index.html` 中的资源路径

---

## 🎯 快速测试

### 测试1：后端API
在浏览器中访问：
- http://localhost:8080/api/product/list
- 应该返回JSON格式的商品列表

### 测试2：前端页面
访问：http://localhost:5173
- 应该能看到校园交易平台首页
- 可以浏览商品（无需登录）
- 可以使用分类筛选功能

---

## ⚠️ 常见问题

### 问题1：端口被占用
**症状**：启动失败，提示端口8080被占用

**解决**：
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080

# 结束进程（将PID替换为实际进程ID）
taskkill /PID <PID> /F
```

### 问题2：数据库连接失败
**症状**：后端启动失败，提示数据库连接错误

**检查**：
1. MySQL服务是否启动
2. 数据库 `campus_trade` 是否存在
3. `application.yml` 中的用户名密码是否正确

### 问题3：前端无法连接后端
**症状**：浏览器访问5173端口，但数据加载失败

**检查**：
1. 后端是否在8080端口运行
2. 前端是否正确启动在5173端口
3. 检查浏览器控制台错误信息

---

## 📝 快捷键提示

在IntelliJ IDEA中：

| 功能 | 快捷键 |
|------|--------|
| 打开项目 | Ctrl + O |
| 运行当前文件 | Shift + F10 |
| 停止运行 | Ctrl + F2 |
| 打开Terminal | Alt + F12 |
| 搜索文件 | Ctrl + Shift + N |

---

## ✅ 验证清单

启动后请检查：

- [ ] 后端启动成功（无报错）
- [ ] 前端启动成功（无报错）
- [ ] http://localhost:8080 可以访问
- [ ] http://localhost:5173 可以访问
- [ ] 首页显示商品列表
- [ ] 分类筛选功能正常
- [ ] 未登录时操作会提示登录

---

## 🆘 如果遇到问题

1. **查看IDEA控制台输出**：通常有详细的错误信息
2. **检查数据库**：确保MySQL服务运行，数据库存在
3. **查看日志**：Spring Boot会在控制台输出详细日志
4. **重启服务**：有时重启可以解决临时问题

---

## 📞 获取帮助

如果仍有问题，请提供：
1. IDEA控制台的错误信息
2. 浏览器的错误截图
3. 你尝试的操作步骤
