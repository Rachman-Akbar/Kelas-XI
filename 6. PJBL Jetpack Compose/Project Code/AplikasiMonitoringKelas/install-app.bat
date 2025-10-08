@echo off
echo ========================================
echo  Install Aplikasi Monitoring Kelas
echo ========================================
echo.

echo Building APK...
call gradlew assembleDebug --quiet

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful!
echo.
echo Checking connected devices...
adb devices

echo.
echo Installing APK to device...
adb install -r app\build\outputs\apk\debug\app-debug.apk

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Installation successful!
    echo ========================================
    echo.
    echo You can now:
    echo 1. Open the app on your phone
    echo 2. Login with:
    echo    Email: admin@sekolah.com
    echo    Password: admin123
    echo.
    echo Make sure Laravel server is running!
    echo ========================================
) else (
    echo.
    echo ERROR: Installation failed!
    echo.
    echo Please check:
    echo - USB debugging is enabled on your phone
    echo - Phone is connected via USB
    echo - Run 'adb devices' to verify connection
)

echo.
pause
