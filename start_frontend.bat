@echo off
echo ==============================================
echo     校园二手交易平台 - 前端启动脚本
echo ==============================================
echo.

cd frontend

REM 检查 Node.js 环境
echo [1/4] 检查 Node.js 环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js 未安装或未配置到 PATH
    echo    请先安装 Node.js 并配置环境变量
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('node -v') do set NODE_VER=%%i
echo ✅ Node.js %NODE_VER% 检查通过

REM 检查 npm 环境
echo.
echo [2/4] 检查 npm 环境...
npm -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ npm 不可用
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('npm -v') do set NPM_VER=%%i
echo ✅ npm %NPM_VER% 检查通过

REM 检查并安装依赖
echo.
echo [3/4] 检查依赖...
if not exist "node_modules" (
    echo ⚠️  未找到 node_modules，开始安装依赖...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖安装失败！
        pause
        exit /b 1
    )
