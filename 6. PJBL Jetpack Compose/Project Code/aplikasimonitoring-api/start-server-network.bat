@echo off
echo ========================================
echo  Laravel API Server - Network Access
echo ========================================
echo.

echo Detecting your IP address...
echo.

for /f "tokens=14" %%a in ('ipconfig ^| findstr /C:"IPv4" ^| findstr "192.168"') do (
    set IP=%%a
    goto :found
)

:found
echo ========================================
echo Your Computer IP: %IP%
echo ========================================
echo.
echo IMPORTANT - Update Android App:
echo.
echo 1. Open file: ApiClient.kt
echo    Path: app/src/main/java/.../data/network/ApiClient.kt
echo.
echo 2. Change BASE_URL to:
echo    private const val BASE_URL = "http://%IP%:8000/api/"
echo.
echo ========================================
echo.
echo Starting Laravel server...
echo Server accessible from: http://%IP%:8000
echo.
echo Test from phone browser: http://%IP%:8000/api/test
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

php artisan serve --host=0.0.0.0 --port=8000

pause
