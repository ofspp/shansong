@echo off
chcp 65001 >nul
title Stop All Services

echo ========================================
echo   Stopping all services...
echo ========================================
echo.

for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Stopping backend PID: %%a ...
    taskkill /pid %%a /f >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":5173 " ^| findstr "LISTENING"') do (
    echo Stopping user frontend PID: %%a ...
    taskkill /pid %%a /f >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":5174 " ^| findstr "LISTENING"') do (
    echo Stopping merchant frontend PID: %%a ...
    taskkill /pid %%a /f >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":5175 " ^| findstr "LISTENING"') do (
    echo Stopping rider frontend PID: %%a ...
    taskkill /pid %%a /f >nul 2>&1
)

echo.
echo All services stopped!
echo.

timeout /t 2 >nul
