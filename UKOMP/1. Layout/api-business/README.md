# 📘 SI-UMKM - Sistem Informasi Manajemen UMKM

<p align="center">
  <strong>Aplikasi web untuk mengelola operasional usaha kecil menengah (bakery/food production)</strong>
</p>

<p align="center">
  <a href="https://laravel.com"><img src="https://img.shields.io/badge/Laravel-11-FF2D20?logo=laravel" alt="Laravel 11"></a>
  <a href="https://www.php.net"><img src="https://img.shields.io/badge/PHP-8.1+-777BB4?logo=php" alt="PHP 8.1+"></a>
  <a href="https://getbootstrap.com"><img src="https://img.shields.io/badge/Bootstrap-5-7952B3?logo=bootstrap" alt="Bootstrap 5"></a>
  <img src="https://img.shields.io/badge/Status-Production%20Ready-success" alt="Production Ready">
</p>

---

## 📚 DOKUMENTASI LENGKAP

**Semua dokumentasi ada di folder `/docs`** → **[📁 Buka Dokumentasi](docs/README.md)**

### 🚀 Quick Start Documentation

| 📄 Dokumen                                                 | 📝 Deskripsi                              | 👥 Untuk           |
| ---------------------------------------------------------- | ----------------------------------------- | ------------------ |
| [📖 README.md](docs/README.md)                             | **START HERE!** Index & navigasi          | Semua              |
| [🎯 00-OVERVIEW.md](docs/00-OVERVIEW.md)                   | Overview sistem & arsitektur              | Developer, PM      |
| [🗺️ 09-UI-FLOWS.md](docs/09-UI-FLOWS.md)                   | Alur halaman per role (Owner, Admin, dll) | Designer, QA       |
| [🗄️ 01-DATABASE-SCHEMA.md](docs/01-DATABASE-SCHEMA.md)     | Schema database (13 tabel)                | Backend Dev        |
| [🔧 02-MODELS.md](docs/02-MODELS.md)                       | Eloquent models & relationships           | Backend Dev        |
| [⚙️ 03-SERVICES.md](docs/03-SERVICES.md)                   | Business logic layer                      | Backend Dev        |
| [✨ 08-FEATURES.md](docs/08-FEATURES.md)                   | Penjelasan fitur & tombol UI              | Designer, QA, User |
| [📡 10-API-DOCUMENTATION.md](docs/10-API-DOCUMENTATION.md) | API endpoints untuk mobile                | Mobile Dev         |

---

## ⚡ Quick Setup

```bash
# Clone & install dependencies
git clone https://github.com/yourusername/api-business.git
cd api-business
composer install
npm install

# Setup environment
cp .env.example .env
php artisan key:generate

# Database setup
php artisan migrate --seed

# Start development server
php artisan serve
# Access: http://localhost:8000
```

**Login Development**:

- Owner: `owner@umkm.com` / `password`
- Admin: `admin@umkm.com` / `password`
- Kasir: `kasir@umkm.com` / `password`

---

## 🎯 Fitur Utama

### 📊 Dashboard

- Real-time metrics (produksi, penjualan, keuangan)
- Interactive charts (Chart.js)
- Stock alerts & notifications

### 🗃️ Master Data

- **Bahan Baku**: Stock management + auto-alert
- **Produk**: HPP auto-calculation
- **Komposisi**: Recipe management + **Bulk Calculate** (planning produksi massal)

### ⚙️ Operasional

- **Produksi**: Material validation, stock auto-update
- **Penjualan**: Invoice generation, profit tracking

### 💰 Keuangan

- Pemasukan/Pengeluaran, Hutang/Piutang
- **Analisis BEP**: Break-even point analysis
- **Analisis HPP**: Cost & margin analysis

### 📊 Laporan

- Laporan Penjualan, Produksi, Keuangan, Stok
- Export Excel/PDF

---

## 🎭 User Roles

| Role         | Dashboard | Master Data | Produksi | Penjualan | Keuangan | Laporan | User Mgmt |
| ------------ | :-------: | :---------: | :------: | :-------: | :------: | :-----: | :-------: |
| **Owner**    |    ✅     |     ✅      |    ✅    |    ✅     |    ✅    |   ✅    |    ✅     |
| **Admin**    |    ✅     |     ✅      |    ✅    |    ✅     |    ✅    |   ✅    |    ❌     |
| **Produksi** |    ✅     |     ✅      |    ✅    |    ❌     |    ❌    |   ⚠️    |    ❌     |
| **Kasir**    |    ✅     |     👁️      |    ❌    |    ✅     |    ❌    |   ⚠️    |    ❌     |
| **Keuangan** |    ✅     |     👁️      |    ❌    |    ❌     |    ✅    |   ✅    |    ❌     |

**Legend**: ✅ Full Access | 👁️ Read Only | ⚠️ Partial | ❌ No Access

---

## 🛠️ Tech Stack

- **Backend**: Laravel 11, PHP 8.1+
- **Frontend**: Blade, Bootstrap 5, Chart.js
- **Database**: MySQL 5.7+ / PostgreSQL 10+
- **Cache**: Redis (optional)
- **API**: RESTful dengan Laravel Sanctum
- **Queue**: Redis/Database

---

## 📂 Struktur Folder

```
api-business/
├── app/
│   ├── Http/
│   │   ├── Controllers/        # Web & API controllers
│   │   └── Middleware/
│   ├── Models/                 # Eloquent models (13 models)
│   ├── Services/               # Business logic layer
│   └── Policies/               # Authorization
├── docs/                       # 📚 DOKUMENTASI LENGKAP
│   ├── README.md               # Index dokumentasi
│   ├── 00-OVERVIEW.md          # System overview
│   ├── 01-DATABASE-SCHEMA.md   # Database schema
│   ├── 02-MODELS.md            # Models & relationships
│   ├── 03-SERVICES.md          # Business logic
│   ├── 08-FEATURES.md          # Features & UI
│   ├── 09-UI-FLOWS.md          # User flows per role
│   └── ...                     # More docs
├── resources/
│   ├── views/                  # Blade templates
│   │   ├── dashboard/
│   │   ├── bahan-baku/
│   │   ├── produk/
│   │   ├── produksi/
│   │   ├── penjualan/
│   │   └── keuangan/
│   └── js/css/                 # Assets
├── routes/
│   ├── web.php                 # Web routes
│   └── api.php                 # API routes
└── database/
    ├── migrations/             # Database migrations
    └── seeders/                # Seed data
```

---

## 🌟 Fitur Unggulan

### 1️⃣ **Bulk Calculate** (Planning Produksi Massal)

**Lokasi**: Komposisi Produk

Fitur untuk planning produksi multi-produk sekaligus:

- ✅ Select multiple products + quantity
- ✅ Auto-calculate total material needs
- ✅ Material requirements aggregation
- ✅ Export planning to Excel
- ✅ Generate PO for supplier

[📖 Dokumentasi Lengkap Bulk Calculate](docs/BULK-CALCULATE-GUIDE.md)

### 2️⃣ **HPP Auto-Calculation**

- HPP dihitung otomatis dari komposisi bahan
- Update realtime saat harga bahan berubah
- Breakdown detail per material

### 3️⃣ **BEP Analysis**

- Break-even point total bisnis
- BEP per produk individual
- Margin of safety calculation
- Sensitivity analysis

### 4️⃣ **Stock Management**

- Auto-update stok dari produksi/penjualan
- Low stock alerts (menipis & kritis)
- Real-time stock reporting

---

## 🧪 Testing

```bash
# Run all tests
php artisan test

# Run with coverage
php artisan test --coverage

# Run specific test
php artisan test --filter ProductionServiceTest
```

---

## 🚀 Deployment

**Production deployment guide**: [docs/11-DEPLOYMENT.md](docs/11-DEPLOYMENT.md)

Quick checklist:

- [ ] Set `APP_ENV=production`
- [ ] Set `APP_DEBUG=false`
- [ ] Generate `APP_KEY`
- [ ] Configure database
- [ ] Setup Redis (optional)
- [ ] Run migrations
- [ ] Setup cron jobs (optional)
- [ ] Setup queue workers (optional)
- [ ] Enable HTTPS

---

## 🐛 Troubleshooting

Common issues: [docs/11-DEPLOYMENT.md#troubleshooting](docs/11-DEPLOYMENT.md)

---

## 📞 Support & Documentation

- **📚 Dokumentasi Lengkap**: [/docs](/docs)
- **🗺️ Alur Halaman per Role**: [docs/09-UI-FLOWS.md](docs/09-UI-FLOWS.md)
- **📡 API Documentation**: [docs/10-API-DOCUMENTATION.md](docs/10-API-DOCUMENTATION.md)
- **🚀 Deployment Guide**: [docs/11-DEPLOYMENT.md](docs/11-DEPLOYMENT.md)
- Laravel Docs: https://laravel.com/docs

---

## 📄 License

Proprietary software. All rights reserved.

---

## 🙏 Built With

- [Laravel Framework](https://laravel.com)
- [Bootstrap](https://getbootstrap.com)
- [Chart.js](https://www.chartjs.org)
- [Font Awesome](https://fontawesome.com)

---

<p align="center">
  <strong>🚀 Happy Coding! Sukses untuk UMKM Indonesia! 🇮🇩</strong>
</p>

---

## 📚 Learn More

### Laravel Resources

Laravel is a web application framework with expressive, elegant syntax:

- [Laravel Documentation](https://laravel.com/docs)
- [Laravel Bootcamp](https://bootcamp.laravel.com)
- [Laracasts Video Tutorials](https://laracasts.com)

In order to ensure that the Laravel community is welcoming to all, please review and abide by the [Code of Conduct](https://laravel.com/docs/contributions#code-of-conduct).

## Security Vulnerabilities

If you discover a security vulnerability within Laravel, please send an e-mail to Taylor Otwell via [taylor@laravel.com](mailto:taylor@laravel.com). All security vulnerabilities will be promptly addressed.

## License

The Laravel framework is open-sourced software licensed under the [MIT license](https://opensource.org/licenses/MIT).
