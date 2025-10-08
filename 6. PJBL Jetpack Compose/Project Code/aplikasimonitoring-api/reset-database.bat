@echo off
echo ========================================
echo  Database Migration and Seeder
echo ========================================
echo.

cd /d "%~dp0"

echo This will:
echo - Drop all existing tables
echo - Create fresh tables
echo - Seed test data
echo.
echo WARNING: All existing data will be lost!
echo.
set /p confirm="Continue? (y/n): "

if /i "%confirm%" neq "y" (
    echo Operation cancelled.
    pause
    exit /b 0
)

echo.
echo Running migrations and seeders...
php artisan migrate:fresh --seed

echo.
echo ========================================
echo Database setup complete!
echo.
echo Test accounts created:
echo - admin@sekolah.com (password: admin123)
echo - kepsek@sekolah.com (password: kepsek123)
echo - kurikulum@sekolah.com (password: kurikulum123)
echo - budi.guru@sekolah.com (password: budi123)
echo - andi.siswa@sekolah.com (password: andi123)
echo ========================================
echo.
pause
