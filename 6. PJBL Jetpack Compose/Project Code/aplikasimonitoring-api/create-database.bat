@echo off
echo ========================================
echo  Create MySQL Database
echo ========================================
echo.

echo This script will create a MySQL database named: aplikasimonitoringkelas
echo.
echo Prerequisites:
echo - MySQL Server must be installed and running
echo - Default MySQL root user (or update credentials below)
echo.
set /p confirm="Continue? (y/n): "

if /i "%confirm%" neq "y" (
    echo Operation cancelled.
    pause
    exit /b 0
)

echo.
echo Creating database...
echo.

REM Create database using MySQL command
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS aplikasimonitoringkelas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Database 'aplikasimonitoringkelas' created successfully!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Run: php artisan migrate:fresh --seed
    echo 2. Or use: reset-database.bat
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to create database!
    echo.
    echo Please check:
    echo - MySQL Server is running
    echo - MySQL root password is correct
    echo - You have permission to create database
    echo ========================================
)

echo.
pause
