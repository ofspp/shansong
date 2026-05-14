@echo off
chcp 65001 >nul
title Delivery Platform - User Frontend

echo ========================================
echo   Starting User Frontend...
echo   URL: http://localhost:5173
echo ========================================
echo.

cd /d "%~dp0delivery-frontend-user"

if not exist node_modules (
    echo Installing dependencies...
    call npm install --cache D:\waimai1\.npm-cache
)

call npm run dev -- --port 5173 --host

pause
