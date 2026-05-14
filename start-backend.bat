@echo off
chcp 65001 >nul
title Delivery Platform - Backend

echo ========================================
echo   Starting Backend Service...
echo ========================================
echo.

cd /d "%~dp0delivery-backend"

call mvn spring-boot:run

pause
