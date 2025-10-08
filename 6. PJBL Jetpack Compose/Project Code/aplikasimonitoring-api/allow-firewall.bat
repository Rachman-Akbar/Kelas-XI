@echo off
echo ========================================
echo  Allow Laravel Server in Firewall
echo ========================================
echo.

echo This will add a Windows Firewall rule to allow
echo incoming connections to port 8000 (Laravel server)
echo.
echo Running as Administrator...
echo.

REM Add firewall rule for inbound connections
netsh advfirewall firewall add rule name="Laravel Development Server" dir=in action=allow protocol=TCP localport=8000

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Firewall rule added successfully!
    echo ========================================
    echo.
    echo Port 8000 is now open for incoming connections.
    echo You can now access the API from other devices.
    echo.
) else (
    echo.
    echo ERROR: Failed to add firewall rule!
    echo Please run this script as Administrator.
    echo.
    echo Right-click this file and select "Run as administrator"
)

pause
