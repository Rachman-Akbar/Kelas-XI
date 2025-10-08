# 🚀 Complete API Controllers & Routes Setup

## Files Created

### Models (✅ Complete with Relationships)

1. ✅ Guru.php
2. ✅ Siswa.php
3. ✅ MataPelajaran.php
4. ✅ Kelas.php
5. ✅ Jadwal.php
6. ✅ Kehadiran.php

### Migrations (✅ Complete with Schema)

1. ✅ create_gurus_table.php
2. ✅ create_siswas_table.php
3. ✅ create_mata_pelajarans_table.php
4. ✅ create_kelas_table.php
5. ✅ create_jadwals_table.php
6. ✅ create_kehadirans_table.php

### Controllers (Partial - Need to Complete)

1. ✅ GuruController.php (COMPLETE)
2. ✅ SiswaController.php (COMPLETE)
3. ⏳ MataPelajaranController.php
4. ⏳ KelasController.php
5. ⏳ JadwalController.php
6. ⏳ KehadiranController.php

---

## Next Steps

### 1. Complete Remaining Controllers

Run these commands to generate the full controller code. I'll provide the complete code for each:

#### MataPelajaranController.php

#### KelasController.php

#### JadwalController.php

#### KehadiranController.php

### 2. Update routes/api.php

Add these routes to enable all API endpoints.

### 3. Run Migrations

```bash
php artisan migrate
```

### 4. Create Seeders (Optional)

Generate sample data for testing.

---

## API Endpoints Structure

### Guru (Teacher) Endpoints

```
GET    /api/gurus           # List all teachers
POST   /api/gurus           # Create new teacher
GET    /api/gurus/{id}      # Get teacher details
PUT    /api/gurus/{id}      # Update teacher
DELETE /api/gurus/{id}      # Delete teacher
```

### Siswa (Student) Endpoints

```
GET    /api/siswas          # List all students
POST   /api/siswas          # Create new student
GET    /api/siswas/{id}     # Get student details
PUT    /api/siswas/{id}     # Update student
DELETE /api/siswas/{id}     # Delete student
```

### MataPelajaran (Subject) Endpoints

```
GET    /api/mata-pelajarans     # List all subjects
POST   /api/mata-pelajarans     # Create new subject
GET    /api/mata-pelajarans/{id} # Get subject details
PUT    /api/mata-pelajarans/{id} # Update subject
DELETE /api/mata-pelajarans/{id} # Delete subject
```

### Kelas (Class) Endpoints

```
GET    /api/kelas           # List all classes
POST   /api/kelas           # Create new class
GET    /api/kelas/{id}      # Get class details
PUT    /api/kelas/{id}      # Update class
DELETE /api/kelas/{id}      # Delete class
```

### Jadwal (Schedule) Endpoints

```
GET    /api/jadwals         # List all schedules
POST   /api/jadwals         # Create new schedule
GET    /api/jadwals/{id}    # Get schedule details
PUT    /api/jadwals/{id}    # Update schedule
DELETE /api/jadwals/{id}    # Delete schedule
```

### Kehadiran (Attendance) Endpoints

```
GET    /api/kehadirans      # List all attendance
POST   /api/kehadirans      # Record attendance
GET    /api/kehadirans/{id} # Get attendance details
PUT    /api/kehadirans/{id} # Update attendance
DELETE /api/kehadirans/{id} # Delete attendance
```

---

## Quick Setup Commands

```bash
# Navigate to project
cd aplikasimonitoring-api

# Run migrations
php artisan migrate

# Create seeders (optional)
php artisan make:seeder GuruSeeder
php artisan make:seeder SiswaSeeder
php artisan make:seeder MataPelajaranSeeder
php artisan make:seeder KelasSeeder
php artisan make:seeder JadwalSeeder

# Run seeders
php artisan db:seed

# Start server
php artisan serve
```

---

## Testing the API

### Example: Create a Guru (Teacher)

**POST** `/api/gurus`

```json
{
    "nip": "198901012015041001",
    "nama": "Budi Santoso",
    "email": "budi@sekolah.com",
    "no_telp": "081234567890",
    "alamat": "Jl. Pendidikan No. 123",
    "jenis_kelamin": "L",
    "tanggal_lahir": "1989-01-01",
    "status": "aktif"
}
```

### Example: Get All Gurus with Filter

**GET** `/api/gurus?status=aktif&search=Budi&per_page=10`

### Example: Create a Siswa (Student)

**POST** `/api/siswas`

```json
{
    "nis": "2021001",
    "nisn": "0012345678",
    "nama": "Andi Wijaya",
    "email": "andi@student.com",
    "no_telp": "081234567890",
    "jenis_kelamin": "L",
    "kelas_id": 1,
    "status": "aktif"
}
```

---

## Database Relationships

```
User (Authentication)
├── (role: admin, kepala_sekolah, kurikulum, siswa)

Guru (Teacher)
├── hasMany: Jadwal
└── hasMany: Kelas (as wali_kelas)

Siswa (Student)
├── belongsTo: Kelas
└── hasMany: Kehadiran

MataPelajaran (Subject)
└── hasMany: Jadwal

Kelas (Class)
├── belongsTo: Guru (wali_kelas)
├── hasMany: Siswa
└── hasMany: Jadwal

Jadwal (Schedule)
├── belongsTo: Kelas
├── belongsTo: MataPelajaran
├── belongsTo: Guru
└── hasMany: Kehadiran

Kehadiran (Attendance)
├── belongsTo: Jadwal
└── belongsTo: Siswa
```

---

## Status

✅ Models Created  
✅ Migrations Created  
✅ 2/6 Controllers Complete  
⏳ Routes Need to be Added  
⏳ Seeders Optional

**Next:** I'll provide the remaining controller code and routes configuration.
