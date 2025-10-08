@echo off
echo Building CGPA Calculator App...
echo.

echo Stopping existing Gradle daemons...
gradlew --stop

echo.
echo Cleaning project...
gradlew clean

echo.
echo Building debug APK...
gradlew assembleDebug

echo.
if %ERRORLEVEL% EQU 0 (
    echo ================================
    echo Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo ================================
) else (
    echo Build failed. Check the error messages above.
)

pause
