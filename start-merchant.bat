@echo off
chcp 65001 >nul
title Delivery Platform - Merchant Frontend

echo ========================================
echo   Starting Merchant Frontend...
echo   URL: http://localhost:5174
echo ========================================
echo.

cd /d "%~dp0delivery-frontend-merchant"

if not exist node_modules (
    echo Installing dependencies...
    call npm install --cache D:\waimai1\.npm-cache
)

call npm run dev -- --port 5174 --host

pause
