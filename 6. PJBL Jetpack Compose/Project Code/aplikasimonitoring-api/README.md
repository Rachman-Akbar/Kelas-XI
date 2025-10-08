# 📱 Aplikasi Monitoring Kelas - Backend API

<p align="center">
<img src="https://img.shields.io/badge/Laravel-12.x-red" alt="Laravel">
<img src="https://img.shields.io/badge/PHP-8.2-blue" alt="PHP">
<img src="https://img.shields.io/badge/MySQL-8.0-orange" alt="MySQL">
<img src="https://img.shields.io/badge/Status-Ready-success" alt="Status">
</p>

Backend API untuk Aplikasi Monitoring Kelas menggunakan Laravel Framework dan MySQL Database. API ini menyediakan endpoint untuk manajemen guru, siswa, mata pelajaran, kelas, jadwal, dan kehadiran.

## 🚀 Quick Start

### 1. Start Server

```bash
cd aplikasimonitoring-api
php artisan serve
```

Server akan berjalan di: **http://127.0.0.1:8000**

### 2. Test Connection

```bash
GET http://127.0.0.1:8000/api/test
```

### 3. Login & Get Token

```bash
POST http://127.0.0.1:8000/api/login
Content-Type: application/json

{
    "email": "admin@example.com",
    "password": "password"
}
```

### 4. Test API Endpoint

```bash
GET http://127.0.0.1:8000/api/gurus
Authorization: Bearer {your_token}
```

## ✨ Features

-   🔐 **Authentication** - Login, Register, Logout dengan Laravel Sanctum
-   👨‍🏫 **Guru Management** - CRUD untuk data guru/pengajar
-   👨‍🎓 **Siswa Management** - CRUD untuk data siswa
-   📚 **Mata Pelajaran** - CRUD untuk data mata pelajaran
-   🏫 **Kelas** - CRUD untuk data kelas
-   📅 **Jadwal** - CRUD untuk jadwal pelajaran
-   ✅ **Kehadiran** - CRUD untuk pencatatan kehadiran
-   🔍 **Filter & Search** - Semua endpoint support filtering dan pencarian
-   📄 **Pagination** - Response data dengan pagination
-   🔗 **Relationships** - Eloquent relationships antar model

## 📊 Database Schema

### Tables

-   **users** - User authentication (admin, guru, siswa)
-   **gurus** - Data guru dengan NIP, nama, email, dll
-   **siswas** - Data siswa dengan NIS, NISN, nama, kelas_id, dll
-   **mata_pelajarans** - Data mata pelajaran dengan kode, nama, KKM
-   **kelas** - Data kelas dengan tingkat, jurusan, wali kelas
-   **jadwals** - Jadwal pelajaran dengan hari, jam, guru, kelas
-   **kehadirans** - Data kehadiran siswa per jadwal

### Relationships

```
User → hasOne → Guru/Siswa
Guru → hasMany → Jadwal
Guru → hasOne → Kelas (as wali kelas)
Siswa → belongsTo → Kelas
Kelas → hasMany → Siswa
Kelas → hasMany → Jadwal
MataPelajaran → hasMany → Jadwal
Jadwal → hasMany → Kehadiran
```

## 🔗 API Endpoints

### Authentication (Public)

-   `POST /api/register` - Register user baru
-   `POST /api/login` - Login dan dapatkan token
-   `GET /api/test` - Test connection

### Protected Endpoints (Require Token)

All endpoints below require `Authorization: Bearer {token}` header

#### Guru

-   `GET /api/gurus` - List all gurus (with filters & search)
-   `GET /api/gurus/{id}` - Get single guru
-   `POST /api/gurus` - Create new guru
-   `PUT /api/gurus/{id}` - Update guru
-   `DELETE /api/gurus/{id}` - Delete guru

#### Siswa

-   `GET /api/siswas` - List all siswas (with filters & search)
-   `GET /api/siswas/{id}` - Get single siswa
-   `POST /api/siswas` - Create new siswa
-   `PUT /api/siswas/{id}` - Update siswa
-   `DELETE /api/siswas/{id}` - Delete siswa

#### Mata Pelajaran

-   `GET /api/mata-pelajarans` - List all mata pelajaran
-   `GET /api/mata-pelajarans/{id}` - Get single mata pelajaran
-   `POST /api/mata-pelajarans` - Create new mata pelajaran
-   `PUT /api/mata-pelajarans/{id}` - Update mata pelajaran
-   `DELETE /api/mata-pelajarans/{id}` - Delete mata pelajaran

#### Kelas

-   `GET /api/kelas` - List all kelas
-   `GET /api/kelas/{id}` - Get single kelas
-   `POST /api/kelas` - Create new kelas
-   `PUT /api/kelas/{id}` - Update kelas
-   `DELETE /api/kelas/{id}` - Delete kelas

#### Jadwal

-   `GET /api/jadwals` - List all jadwals
-   `GET /api/jadwals/{id}` - Get single jadwal
-   `POST /api/jadwals` - Create new jadwal
-   `PUT /api/jadwals/{id}` - Update jadwal
-   `DELETE /api/jadwals/{id}` - Delete jadwal

#### Kehadiran

-   `GET /api/kehadirans` - List all kehadirans
-   `GET /api/kehadirans/{id}` - Get single kehadiran
-   `POST /api/kehadirans` - Create new kehadiran
-   `PUT /api/kehadirans/{id}` - Update kehadiran
-   `DELETE /api/kehadirans/{id}` - Delete kehadiran

## 📖 Documentation

Untuk dokumentasi lengkap, lihat file-file berikut:

-   📘 [SETUP_COMPLETE.md](./SETUP_COMPLETE.md) - Status setup dan overview
-   📗 [POSTMAN_TESTING_GUIDE.md](./POSTMAN_TESTING_GUIDE.md) - Panduan testing dengan Postman
-   📙 [QUICK_TEST.md](./QUICK_TEST.md) - Quick test commands
-   📕 [SETUP_GUIDE.md](./SETUP_GUIDE.md) - Setup guide dari awal
-   📔 [MYSQL_SETUP.md](./MYSQL_SETUP.md) - MySQL configuration guide

## 🔧 Configuration

### Database (.env)

```env
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=aplikasimonitoringkelas
DB_USERNAME=root
DB_PASSWORD=
```

### Android Configuration

```kotlin
// For Emulator
private const val BASE_URL = "http://10.0.2.2:8000/api/"

// For Physical Device (ganti dengan IP laptop)
private const val BASE_URL = "http://192.168.X.X:8000/api/"
```

## 🛠️ Tech Stack

-   **Framework:** Laravel 12.x
-   **Database:** MySQL 8.x
-   **Authentication:** Laravel Sanctum
-   **PHP:** 8.2+
-   **Composer:** 2.x

## 📦 Installation

```bash
# Clone repository
git clone <repository-url>

# Install dependencies
composer install

# Copy .env
cp .env.example .env

# Generate key
php artisan key:generate

# Configure database in .env
# Then run migrations
php artisan migrate:fresh --seed

# Start server
php artisan serve
```

## 🧪 Testing

### Test dengan curl

```bash
# Test connection
curl http://127.0.0.1:8000/api/test

# Login
curl -X POST http://127.0.0.1:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password"}'
```

### Test dengan Postman

Lihat panduan lengkap di [POSTMAN_TESTING_GUIDE.md](./POSTMAN_TESTING_GUIDE.md)

## 🤝 Integration dengan Android

API ini dirancang untuk diintegrasikan dengan aplikasi Android (Jetpack Compose).

### Retrofit Setup

```kotlin
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
```

## 📝 Sample Response

### Success Response

```json
{
    "success": true,
    "message": "Data berhasil diambil",
    "data": {
        "current_page": 1,
        "data": [...],
        "total": 10
    }
}
```

### Error Response

```json
{
    "success": false,
    "message": "Validation Error",
    "errors": {
        "field": ["Error message"]
    }
}
```

## 🎯 Project Status

✅ **100% Complete & Ready to Use**

-   [x] Database configured (MySQL)
-   [x] All migrations created and run
-   [x] All models with relationships
-   [x] All controllers with CRUD operations
-   [x] Authentication with Sanctum
-   [x] API routes registered
-   [x] Sample data seeded
-   [x] Documentation complete
-   [x] Tested and working

## 📞 Support

Jika menemukan masalah:

1. Cek `storage/logs/laravel.log`
2. Pastikan server running: `php artisan serve`
3. Pastikan database connected
4. Lihat dokumentasi di folder ini

## 📄 License

The Laravel framework is open-sourced software licensed under the [MIT license](https://opensource.org/licenses/MIT).

---

## About Laravel

Laravel is a web application framework with expressive, elegant syntax. We believe development must be an enjoyable and creative experience to be truly fulfilling. Laravel takes the pain out of development by easing common tasks used in many web projects, such as:

-   [Simple, fast routing engine](https://laravel.com/docs/routing).
-   [Powerful dependency injection container](https://laravel.com/docs/container).
-   Multiple back-ends for [session](https://laravel.com/docs/session) and [cache](https://laravel.com/docs/cache) storage.
-   Expressive, intuitive [database ORM](https://laravel.com/docs/eloquent).
-   Database agnostic [schema migrations](https://laravel.com/docs/migrations).
-   [Robust background job processing](https://laravel.com/docs/queues).
-   [Real-time event broadcasting](https://laravel.com/docs/broadcasting).

Laravel is accessible, powerful, and provides tools required for large, robust applications.

## Learning Laravel

Laravel has the most extensive and thorough [documentation](https://laravel.com/docs) and video tutorial library of all modern web application frameworks, making it a breeze to get started with the framework.

You may also try the [Laravel Bootcamp](https://bootcamp.laravel.com), where you will be guided through building a modern Laravel application from scratch.

If you don't feel like reading, [Laracasts](https://laracasts.com) can help. Laracasts contains thousands of video tutorials on a range of topics including Laravel, modern PHP, unit testing, and JavaScript. Boost your skills by digging into our comprehensive video library.

## Laravel Sponsors

We would like to extend our thanks to the following sponsors for funding Laravel development. If you are interested in becoming a sponsor, please visit the [Laravel Partners program](https://partners.laravel.com).

### Premium Partners

-   **[Vehikl](https://vehikl.com)**
-   **[Tighten Co.](https://tighten.co)**
-   **[Kirschbaum Development Group](https://kirschbaumdevelopment.com)**
-   **[64 Robots](https://64robots.com)**
-   **[Curotec](https://www.curotec.com/services/technologies/laravel)**
-   **[DevSquad](https://devsquad.com/hire-laravel-developers)**
-   **[Redberry](https://redberry.international/laravel-development)**
-   **[Active Logic](https://activelogic.com)**

## Contributing

Thank you for considering contributing to the Laravel framework! The contribution guide can be found in the [Laravel documentation](https://laravel.com/docs/contributions).

## Code of Conduct

In order to ensure that the Laravel community is welcoming to all, please review and abide by the [Code of Conduct](https://laravel.com/docs/contributions#code-of-conduct).

## Security Vulnerabilities

If you discover a security vulnerability within Laravel, please send an e-mail to Taylor Otwell via [taylor@laravel.com](mailto:taylor@laravel.com). All security vulnerabilities will be promptly addressed.

## License

The Laravel framework is open-sourced software licensed under the [MIT license](https://opensource.org/licenses/MIT).
