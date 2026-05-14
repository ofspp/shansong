@echo off
chcp 65001 >nul
title Delivery Platform - Start All

echo ========================================
echo   Starting All Services...
echo ========================================
echo.

echo Starting backend...
start "Backend" cmd /c "cd /d "%~dp0" && start-backend.bat"

echo Waiting 5 seconds for backend...
timeout /t 5 /nobreak >nul

echo Starting user frontend...
start "User Frontend" cmd /c "cd /d "%~dp0" && start-user.bat"

echo Starting merchant frontend...
start "Merchant Frontend" cmd /c "cd /d "%~dp0" && start-merchant.bat"

echo Starting rider frontend...
start "Rider Frontend" cmd /c "cd /d "%~dp0" && start-rider.bat"

echo.
echo ========================================
echo   All services started!
echo ========================================
echo.
echo   Backend API:  http://localhost:8080
echo   Swagger:      http://localhost:8080/swagger-ui.html
echo   User:         http://localhost:5173
echo   Merchant:     http://localhost:5174
echo   Rider:        http://localhost:5175
echo.
echo   Run stop-all.bat to stop all services.
echo.

pause
