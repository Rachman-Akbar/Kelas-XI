# Setup Laravel API untuk Android Studio

## Langkah 1: Install Dependencies

```bash
cd aplikasimonitoring-api
composer install
```

Jika Laravel Sanctum belum terinstall, jalankan:

```bash
composer require laravel/sanctum
```

## Langkah 2: Setup Database

### Option A: MySQL Database (Recommended - Sudah Dikonfigurasi)

1. **Pastikan MySQL Server sudah terinstall dan berjalan**

2. **Buat database** menggunakan salah satu cara berikut:

    **Cara 1: Manual via MySQL Command Line**

    ```bash
    mysql -u root -p
    CREATE DATABASE aplikasimonitoringkelas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    EXIT;
    ```

    **Cara 2: Menggunakan Script SQL**

    ```bash
    mysql -u root -p < create-database.sql
    ```

    **Cara 3: Via PHPMyAdmin**

    - Buka PHPMyAdmin
    - Klik "New" atau "Database"
    - Nama: `aplikasimonitoringkelas`
    - Collation: `utf8mb4_unicode_ci`
    - Klik "Create"

3. **Konfigurasi sudah di `.env`:**

    ```
    DB_CONNECTION=mysql
    DB_HOST=127.0.0.1
    DB_PORT=3306
    DB_DATABASE=aplikasimonitoringkelas
    DB_USERNAME=root
    DB_PASSWORD=
    ```

    **Note:** Jika MySQL Anda menggunakan password, update `DB_PASSWORD`

4. **Jalankan migrasi dan seeder:**
    ```bash
    php artisan migrate:fresh --seed
    ```

### Option B: SQLite Database (Alternative)

1. Update `.env`:

    ```
    DB_CONNECTION=sqlite
    # DB_HOST=127.0.0.1
    # DB_PORT=3306
    # DB_DATABASE=aplikasimonitoringkelas
    # DB_USERNAME=root
    # DB_PASSWORD=
    ```

2. Jalankan migrasi:
    ```bash
    php artisan migrate:fresh --seed
    ```

## Langkah 3: Publish Sanctum Configuration (Opsional)

```bash
php artisan vendor:publish --provider="Laravel\Sanctum\SanctumServiceProvider"
```

## Langkah 4: Jalankan Server Laravel

```bash
php artisan serve
```

Server akan berjalan di: `http://127.0.0.1:8000`

## Langkah 5: Test API Connection

Buka browser atau Postman dan test endpoint:

```
GET http://127.0.0.1:8000/api/test
```

Response yang diharapkan:

```json
{
    "success": true,
    "message": "API Connected Successfully!",
    "data": "Laravel API is running"
}
```

## Langkah 6: Test Login

**POST** `http://127.0.0.1:8000/api/login`

**Headers:**

```
Content-Type: application/json
Accept: application/json
```

**Body (JSON):**

```json
{
    "email": "admin@sekolah.com",
    "password": "admin123"
}
```

**Response:**

```json
{
    "success": true,
    "message": "Login berhasil",
    "data": {
        "user": {
            "id": 1,
            "nama": "Admin",
            "email": "admin@sekolah.com",
            "role": "admin",
            "created_at": "2025-10-08T..."
        },
        "token": "1|laravel_sanctum_..."
    }
}
```

## Akun Test yang Tersedia

| Email                  | Password     | Role           |
| ---------------------- | ------------ | -------------- |
| admin@sekolah.com      | admin123     | admin          |
| kepsek@sekolah.com     | kepsek123    | kepala_sekolah |
| kurikulum@sekolah.com  | kurikulum123 | kurikulum      |
| budi.guru@sekolah.com  | budi123      | siswa          |
| andi.siswa@sekolah.com | andi123      | siswa          |
| siti.siswa@sekolah.com | siti123      | siswa          |
| dewi.siswa@sekolah.com | dewi123      | siswa          |

## Langkah 7: Konfigurasi untuk Android Studio

### Untuk Android Emulator:

URL: `http://10.0.2.2:8000/api/`

### Untuk Physical Device:

1. Cari IP Address komputer Anda:
    - Windows: `ipconfig` (cari IPv4 Address)
    - Mac/Linux: `ifconfig` atau `ip addr`
2. Update `RetrofitClient.kt` dengan IP Anda:

```kotlin
private const val BASE_URL = "http://192.168.1.XXX:8000/api/"
```

3. Pastikan komputer dan device di network yang sama (WiFi sama)

### Untuk Testing dengan Postman:

URL: `http://127.0.0.1:8000/api/`

## API Endpoints

### Public Endpoints (No Authentication)

-   `GET /api/test` - Test koneksi
-   `POST /api/login` - Login user
-   `POST /api/register` - Register user baru

### Protected Endpoints (Requires Authentication)

Tambahkan header:

```
Authorization: Bearer {token}
```

-   `POST /api/logout` - Logout user
-   `GET /api/user` - Get current user
-   `GET /api/users` - Get all users
-   `GET /api/users/{id}` - Get user by ID
-   `POST /api/users` - Create new user
-   `PUT /api/users/{id}` - Update user
-   `DELETE /api/users/{id}` - Delete user

## Troubleshooting

### Error: Connection Refused

-   Pastikan Laravel server berjalan (`php artisan serve`)
-   Cek firewall tidak memblokir port 8000
-   Untuk physical device, pastikan IP address benar

### Error: CORS

-   File `bootstrap/app.php` sudah dikonfigurasi untuk CORS
-   Pastikan menggunakan Laravel 12 atau Sanctum sudah terinstall

### Error: Database Connection

**MySQL:**

-   Pastikan MySQL Server berjalan
-   Cek database `aplikasimonitoringkelas` sudah dibuat
-   Verifikasi credentials di `.env` (DB_USERNAME, DB_PASSWORD)
-   Test koneksi: `php artisan db:show`
-   Jalankan `php artisan migrate:fresh --seed` untuk reset database

**SQLite:**

-   Pastikan file `database/database.sqlite` ada dan writable

### Error: 404 Not Found

-   Pastikan route di `routes/api.php` ada
-   Clear cache: `php artisan config:clear` dan `php artisan route:clear`

## Menambah Endpoint Baru

1. Tambahkan route di `routes/api.php`
2. Buat controller baru atau update yang ada
3. Update `ApiService.kt` di Android Studio
4. Update model data jika perlu

## Production Deployment

Untuk production:

1. Update `APP_ENV=production` di `.env`
2. Set `APP_DEBUG=false`
3. Generate key baru: `php artisan key:generate`
4. Setup database production (MySQL/PostgreSQL)
5. Setup HTTPS/SSL certificate
6. Update BASE_URL di Android app ke production URL
