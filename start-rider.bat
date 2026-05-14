@echo off
chcp 65001 >nul
title Delivery Platform - Rider Frontend

echo ========================================
echo   Starting Rider Frontend...
echo   URL: http://localhost:5175
echo ========================================
echo.

cd /d "%~dp0delivery-frontend-rider"

if not exist node_modules (
    echo Installing dependencies...
    call npm install --cache D:\waimai1\.npm-cache
)

call npm run dev -- --port 5175 --host

pause
