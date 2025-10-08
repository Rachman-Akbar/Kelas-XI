@echo off
echo ========================================
echo  Laravel API Server - Quick Start
echo ========================================
echo.

cd /d "%~dp0"

echo [1/2] Checking Laravel setup...
if not exist ".env" (
    echo Error: .env file not found!
    echo Please copy .env.example to .env first
    pause
    exit /b 1
)

if not exist "vendor" (
    echo Installing dependencies...
    call composer install
)

echo.
echo [2/2] Starting Laravel server...
echo.
echo Server will run at: http://127.0.0.1:8000
echo API Test URL: http://127.0.0.1:8000/api/test
echo.
echo For Android Emulator, use: http://10.0.2.2:8000/api/
echo For Physical Device, use: http://YOUR_IP:8000/api/
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

php artisan serve
