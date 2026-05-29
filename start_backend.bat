@echo off
echo ==============================================
echo     校园二手交易平台 - 后端启动脚本
echo ==============================================
echo.

REM 检查 Java 环境
echo [1/3] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java 未安装或未配置到 PATH
    echo    请先安装 Java 21 并配置环境变量
    pause
    exit /b 1
)
echo ✅ Java 环境检查通过

REM 检查 Maven 环境
echo.
echo [2/3] 检查 Maven 环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  未找到 Maven，尝试直接运行已编译的类...
    echo.
    echo 建议在 IntelliJ IDEA 中右键 CampusTradeApplication.java 并运行
    pause
    exit /b 1
)
echo ✅ Maven 环境检查通过

REM 使用 Maven 启动后端
echo.
echo [3/3] 启动 Spring Boot 后端...
echo.
mvn spring-boot:run
if %errorlevel% neq 0 (
    echo.
    echo ❌ 启动失败！
    pause
)
