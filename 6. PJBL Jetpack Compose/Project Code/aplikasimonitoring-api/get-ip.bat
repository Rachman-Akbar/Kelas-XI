@echo off
echo ========================================
echo  Check IP Address for Physical Device
echo ========================================
echo.

echo Your IP Addresses:
echo.
ipconfig | findstr /i "IPv4"

echo.
echo ========================================
echo Use one of the IPv4 addresses above
echo Example: http://192.168.1.100:8000/api/
echo.
echo Update BASE_URL in Android Studio:
echo File: data/api/RetrofitClient.kt
echo ========================================
echo.
pause
