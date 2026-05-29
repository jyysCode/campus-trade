# 校园二手交易平台 - 启动脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   校园二手交易平台 - 启动中..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 设置Java环境
$env:JAVA_HOME = "D:\Java"
$env:PATH = "D:\Java\bin;$env:PATH"

# 验证Java
Write-Host "[1/3] 检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Java已就绪: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java未找到!" -ForegroundColor Red
    Write-Host "请确保已安装Java 21" -ForegroundColor Red
    pause
    exit 1
}

# 检查Maven
Write-Host ""
Write-Host "[2/3] 检查Maven环境..." -ForegroundColor Yellow
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCmd) {
    Write-Host "✓ Maven已就绪" -ForegroundColor Green
    $mvnAvailable = $true
} else {
    Write-Host "⚠ Maven不在PATH中，将尝试直接运行..." -ForegroundColor Yellow
    $mvnAvailable = $false
}

# 启动后端
Write-Host ""
Write-Host "[3/3] 启动Spring Boot后端 (端口8080)..." -ForegroundColor Yellow
Write-Host ""

# 检查是否有编译好的类
if (Test-Path ".\target\classes\com\campus\trade\CampusTradeApplication.class") {
    Write-Host "✓ 发现已编译的类文件" -ForegroundColor Green
    
    # 尝试使用Spring Boot直接运行
    if ($mvnAvailable) {
        Write-Host "使用Maven启动..." -ForegroundColor Cyan
        mvn spring-boot:run
    } else {
        # 尝试使用java直接运行
        Write-Host "尝试使用Java直接运行..." -ForegroundColor Cyan
        $springBootJar = Get-ChildItem -Path ".\target" -Filter "*.jar" -ErrorAction SilentlyContinue | Select-Object -First 1
        
        if ($springBootJar) {
            Write-Host "找到JAR文件: $($springBootJar.Name)" -ForegroundColor Green
            java -jar $springBootJar.FullName
        } else {
            Write-Host "✗ 未找到可执行的JAR文件" -ForegroundColor Red
            Write-Host ""
            Write-Host "请在IntelliJ IDEA中:" -ForegroundColor Yellow
            Write-Host "1. 打开 CampusTradeApplication.java" -ForegroundColor Yellow
            Write-Host "2. 右键 -> Run 'CampusTradeApplication'" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "或安装Maven后运行: mvn spring-boot:run" -ForegroundColor Yellow
            pause
        }
    }
} else {
    Write-Host "⚠ 未找到编译的类，需要先编译项目" -ForegroundColor Yellow
    if ($mvnAvailable) {
        Write-Host "正在编译项目..." -ForegroundColor Cyan
        mvn clean compile
    } else {
        Write-Host "请在IntelliJ IDEA中编译项目" -ForegroundColor Red
        pause
    }
}
