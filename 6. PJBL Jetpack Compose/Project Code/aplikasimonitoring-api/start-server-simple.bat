@echo off
cls
echo ========================================
echo  Starting Laravel Development Server
echo ========================================
echo.

cd /d "C:\Akbar\6. PJBL Jetpack Compose\Project Code\aplikasimonitoring-api"

echo Stopping any existing PHP processes...
taskkill /F /IM php.exe >nul 2>&1

echo.
echo Starting server at http://127.0.0.1:8000
echo.
echo To access from Android device, use:
echo   http://192.168.1.4:8000
echo.
echo ========================================
echo  Server is now running!
echo  Press Ctrl+C to stop
echo ========================================
echo.

php artisan serve

pause
