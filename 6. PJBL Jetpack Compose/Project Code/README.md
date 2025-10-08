# 🎓 Aplikasi Monitoring Kelas - Full Stack Project

Project ini terdiri dari 2 bagian:

1. **Backend API** - Laravel (folder: `aplikasimonitoring-api`)
2. **Mobile App** - Android Studio dengan Jetpack Compose (folder: `AplikasiMonitoringKelas`)

## 📋 Daftar Isi

- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Setup Backend (Laravel)](#setup-backend-laravel)
- [Setup Frontend (Android)](#setup-frontend-android)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)

## 🚀 Quick Start

### Minimum Requirements

- PHP 8.2+
- Composer
- Android Studio Hedgehog | 2023.1.1+
- JDK 11+
- Windows/Mac/Linux

### Start Laravel Server

```bash
cd aplikasimonitoring-api
start-server.bat    # Windows
# atau
php artisan serve   # Manual
```

Server akan berjalan di: `http://127.0.0.1:8000`

### Open Android Studio

```bash
cd AplikasiMonitoringKelas
# Buka folder ini di Android Studio
```

**Important:** Update BASE_URL di `RetrofitClient.kt`:

- **Emulator:** `http://10.0.2.2:8000/api/`
- **Physical Device:** `http://YOUR_IP:8000/api/` (jalankan `get-ip.bat` untuk cek IP)

## 📁 Project Structure

```
.
├── aplikasimonitoring-api/          # Laravel Backend
│   ├── app/
│   │   ├── Http/Controllers/        # API Controllers
│   │   │   ├── AuthController.php   # Login, Register, Logout
│   │   │   └── UserController.php   # CRUD Users
│   │   └── Models/
│   │       └── User.php             # User Model
│   ├── database/
│   │   ├── migrations/              # Database schema
│   │   └── seeders/                 # Test data
│   ├── routes/
│   │   └── api.php                  # API Routes
│   ├── start-server.bat             # Quick start script
│   ├── reset-database.bat           # Reset DB script
│   ├── get-ip.bat                   # Check IP for physical device
│   └── SETUP_GUIDE.md               # Backend setup guide
│
└── AplikasiMonitoringKelas/         # Android Frontend
    ├── app/src/main/java/.../
    │   ├── data/
    │   │   ├── api/
    │   │   │   ├── ApiService.kt       # API Endpoints
    │   │   │   ├── ApiModels.kt        # Request/Response models
    │   │   │   └── RetrofitClient.kt   # HTTP Client config
    │   │   ├── repository/
    │   │   └── DataModels.kt
    │   ├── ui/
    │   │   ├── screens/                # UI Screens
    │   │   └── viewmodel/              # Business Logic
    │   └── utils/
    │       └── ApiConfig.kt            # API Configuration
    └── ANDROID_SETUP_GUIDE.md          # Android setup guide
```

## 🔧 Setup Backend (Laravel)

### 1. Install Dependencies

```bash
cd aplikasimonitoring-api
composer install
```

### 2. Setup Database

Database menggunakan **MySQL** dengan nama **aplikasimonitoringkelas**

**Buat database MySQL:**

```bash
mysql -u root -p
CREATE DATABASE aplikasimonitoringkelas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

Atau via PHPMyAdmin, buat database bernama `aplikasimonitoringkelas`

**Konfigurasi di `.env` sudah diset:**

```
DB_CONNECTION=mysql
DB_DATABASE=aplikasimonitoringkelas
DB_USERNAME=root
DB_PASSWORD=
```

**Jalankan migrations:**

```bash
php artisan migrate:fresh --seed
```

atau jalankan: `reset-database.bat`

### 3. Test API

```bash
php artisan serve
```

Buka browser: http://127.0.0.1:8000/api/test

✅ Response:

```json
{
  "success": true,
  "message": "API Connected Successfully!",
  "data": "Laravel API is running"
}
```

### Test Accounts

| Email                  | Password     | Role           |
| ---------------------- | ------------ | -------------- |
| admin@sekolah.com      | admin123     | admin          |
| kepsek@sekolah.com     | kepsek123    | kepala_sekolah |
| kurikulum@sekolah.com  | kurikulum123 | kurikulum      |
| budi.guru@sekolah.com  | budi123      | siswa          |
| andi.siswa@sekolah.com | andi123      | siswa          |

**Dokumentasi lengkap:** [SETUP_GUIDE.md](aplikasimonitoring-api/SETUP_GUIDE.md)

## 📱 Setup Frontend (Android)

### 1. Open Project

```bash
cd AplikasiMonitoringKelas
```

Buka folder ini di Android Studio

### 2. Configure BASE_URL

**File:** `app/src/main/java/.../data/api/RetrofitClient.kt`

#### Untuk Android Emulator:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/api/"
```

#### Untuk Physical Device:

1. Jalankan `get-ip.bat` di folder Laravel
2. Update dengan IP Anda:

```kotlin
private const val BASE_URL = "http://192.168.1.XXX:8000/api/"
```

### 3. Sync & Build

1. Sync Gradle
2. Build & Run

### 4. Test Login

Gunakan salah satu test account di atas untuk login.

**Dokumentasi lengkap:** [ANDROID_SETUP_GUIDE.md](AplikasiMonitoringKelas/ANDROID_SETUP_GUIDE.md)

## 🧪 Testing

### Test Backend API (Laravel)

#### 1. Test Connection

```bash
curl http://127.0.0.1:8000/api/test
```

#### 2. Test Login dengan Postman/curl

**POST** `http://127.0.0.1:8000/api/login`

Headers:

```
Content-Type: application/json
Accept: application/json
```

Body:

```json
{
  "email": "admin@sekolah.com",
  "password": "admin123"
}
```

Response:

```json
{
    "success": true,
    "message": "Login berhasil",
    "data": {
        "user": { ... },
        "token": "..."
    }
}
```

### Test Android App

1. Start Laravel server
2. Run Android app di emulator/device
3. Login dengan test account
4. Cek Logcat untuk API logs (tag: OkHttp)

## 📚 API Documentation

### Base URL

- Development: `http://127.0.0.1:8000/api/`
- Emulator: `http://10.0.2.2:8000/api/`
- Physical Device: `http://YOUR_IP:8000/api/`

### Public Endpoints

#### Test Connection

```
GET /api/test
```

#### Login

```
POST /api/login
Body: { "email": "...", "password": "..." }
```

#### Register

```
POST /api/register
Body: { "nama": "...", "email": "...", "password": "...", "role": "..." }
```

### Protected Endpoints

Tambahkan header: `Authorization: Bearer {token}`

#### Logout

```
POST /api/logout
```

#### Get Current User

```
GET /api/user
```

#### Users Management

```
GET    /api/users        # Get all users
GET    /api/users/{id}   # Get user by ID
POST   /api/users        # Create user
PUT    /api/users/{id}   # Update user
DELETE /api/users/{id}   # Delete user
```

### Response Format

Success:

```json
{
    "success": true,
    "message": "...",
    "data": { ... }
}
```

Error:

```json
{
    "success": false,
    "message": "...",
    "errors": { ... }
}
```

## 🐛 Troubleshooting

### Backend Issues

#### Server tidak bisa start

```bash
# Check port 8000
netstat -ano | findstr :8000

# Kill process jika perlu
taskkill /PID <PID> /F

# Start ulang
php artisan serve
```

#### Database error

```bash
# Reset database
php artisan migrate:fresh --seed
```

#### CORS error

- File `bootstrap/app.php` sudah dikonfigurasi
- Pastikan Laravel Sanctum terinstall

### Android Issues

#### Connection Refused / Unable to resolve host

**Untuk Emulator:**

- Gunakan `http://10.0.2.2:8000/api/`
- JANGAN gunakan `localhost` atau `127.0.0.1`

**Untuk Physical Device:**

1. Jalankan `get-ip.bat` untuk cek IP
2. Update BASE_URL dengan IP tersebut
3. Pastikan device & komputer di WiFi yang sama
4. Disable firewall atau allow port 8000

#### Cleartext HTTP traffic not permitted

- Sudah ada `android:usesCleartextTraffic="true"` di AndroidManifest.xml

#### 404 Not Found

- Pastikan Laravel server running
- Cek BASE_URL ada `/api/` di akhir
- Test endpoint di browser dulu

### Network Testing

```bash
# Test dari Android Emulator
adb shell ping 10.0.2.2

# Test API dari Postman
GET http://127.0.0.1:8000/api/test
```

## 📝 Development Workflow

### Menambah Endpoint Baru

#### 1. Laravel (Backend)

```php
// routes/api.php
Route::get('/jadwal', [JadwalController::class, 'index']);

// app/Http/Controllers/JadwalController.php
public function index() {
    return response()->json([
        'success' => true,
        'data' => Jadwal::all()
    ]);
}
```

#### 2. Android (Frontend)

```kotlin
// ApiService.kt
@GET("jadwal")
suspend fun getJadwal(): Response<ApiResponse<List<Jadwal>>>

// ViewModel
val response = RetrofitClient.apiService.getJadwal()
```

## 🎯 Features

- ✅ User Authentication (Login, Register, Logout)
- ✅ Role-based Access (Siswa, Kurikulum, Kepala Sekolah, Admin)
- ✅ User Management (CRUD)
- ✅ API Token Authentication (Laravel Sanctum)
- ✅ Modern UI (Jetpack Compose)
- ✅ Reactive Programming (Kotlin Coroutines)
- ✅ RESTful API
- ✅ SQLite Database

## 📄 License

This project is for educational purposes.

## 👥 Contributors

- Akbar (Developer)

## 📞 Support

Jika ada masalah:

1. Baca [SETUP_GUIDE.md](aplikasimonitoring-api/SETUP_GUIDE.md) untuk backend
2. Baca [ANDROID_SETUP_GUIDE.md](AplikasiMonitoringKelas/ANDROID_SETUP_GUIDE.md) untuk Android
3. Cek Laravel logs: `aplikasimonitoring-api/storage/logs/laravel.log`
4. Cek Logcat di Android Studio
5. Test API dengan Postman/curl

---

**Happy Coding! 🚀**
