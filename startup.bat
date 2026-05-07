@echo off
REM 无人机信息管理系统 - 启动脚本
REM 使用MySQL数据库（prod profile）

echo ========================================
echo 无人机信息管理系统
echo ========================================
echo.

REM 检查并启动 Redis
set REDIS_DIR=D:\Redis
echo 正在检查 Redis 服务...
tasklist /FI "IMAGENAME eq redis-server.exe" 2>nul | find /I "redis-server.exe" >nul
if %errorlevel% equ 0 (
    echo [OK] Redis 已在运行中
) else (
    if exist "%REDIS_DIR%\redis-server.exe" (
        echo [..] 正在启动 Redis...
        start "" /MIN "%REDIS_DIR%\redis-server.exe" "%REDIS_DIR%\redis.windows.conf"
        timeout /t 2 /nobreak >nul
        tasklist /FI "IMAGENAME eq redis-server.exe" 2>nul | find /I "redis-server.exe" >nul
        if %errorlevel% equ 0 (
            echo [OK] Redis 启动成功
        ) else (
            echo [!!] Redis 启动失败，请手动检查 %REDIS_DIR%
        )
    ) else (
        echo [!!] 未找到 Redis，请确认安装路径: %REDIS_DIR%\redis-server.exe
    )
)
echo.

REM 停止旧的Java进程
echo 正在停止旧进程...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

REM 编译项目
echo 正在编译项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 正在启动应用...
echo 数据库: MySQL
echo 端口: 8080
echo 访问地址:  http://localhost:8080/drones
echo.

java -jar target\drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod

pause
