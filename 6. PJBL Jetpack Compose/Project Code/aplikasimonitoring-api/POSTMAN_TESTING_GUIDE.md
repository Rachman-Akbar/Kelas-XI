# 🧪 Panduan Testing API dengan Postman

## ✅ Persiapan

1. **Server Laravel harus running:**

    ```bash
    php artisan serve
    ```

    Server akan berjalan di: `http://127.0.0.1:8000`

2. **Database MySQL sudah termigrasi:**
    ```bash
    php artisan migrate:fresh --seed
    ```

## 🔑 Authentication Flow

### 1. Register User Baru

**Endpoint:** `POST http://127.0.0.1:8000/api/register`

**Headers:**

```
Content-Type: application/json
Accept: application/json
```

**Body (JSON):**

```json
{
    "name": "Admin Test",
    "email": "admin@test.com",
    "password": "password123",
    "password_confirmation": "password123",
    "role": "admin"
}
```

**Response:**

```json
{
    "success": true,
    "message": "User registered successfully",
    "data": {
        "user": { ... },
        "token": "1|xxxxxxxxxxxxx"
    }
}
```

### 2. Login

**Endpoint:** `POST http://127.0.0.1:8000/api/login`

**Headers:**

```
Content-Type: application/json
Accept: application/json
```

**Body (JSON):**

```json
{
    "email": "admin@test.com",
    "password": "password123"
}
```

**Response:**

```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "user": { ... },
        "token": "2|xxxxxxxxxxxxx"
    }
}
```

**⚠️ PENTING:** Simpan `token` dari response untuk digunakan di request selanjutnya!

---

## 👨‍🏫 GURU Management

### Get All Guru

**Endpoint:** `GET http://127.0.0.1:8000/api/gurus`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

**Query Parameters (Optional):**

-   `status=aktif` - Filter by status
-   `search=nama` - Search by nama, nip, or email
-   `per_page=10` - Items per page

**Example:**

```
GET http://127.0.0.1:8000/api/gurus?status=aktif&per_page=10
```

### Get Single Guru

**Endpoint:** `GET http://127.0.0.1:8000/api/gurus/{id}`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

### Create Guru

**Endpoint:** `POST http://127.0.0.1:8000/api/gurus`

**Headers:**

```
Authorization: Bearer {your_token_here}
Content-Type: application/json
Accept: application/json
```

**Body (JSON):**

```json
{
    "nip": "198501012010011001",
    "nama": "Budi Santoso",
    "email": "budi.santoso@sekolah.com",
    "no_telp": "081234567890",
    "alamat": "Jl. Pendidikan No. 123",
    "jenis_kelamin": "L",
    "tanggal_lahir": "1985-01-01",
    "status": "aktif"
}
```

### Update Guru

**Endpoint:** `PUT http://127.0.0.1:8000/api/gurus/{id}`

**Headers:**

```
Authorization: Bearer {your_token_here}
Content-Type: application/json
Accept: application/json
```

**Body (JSON):**

```json
{
    "nama": "Budi Santoso Updated",
    "status": "non-aktif"
}
```

### Delete Guru

**Endpoint:** `DELETE http://127.0.0.1:8000/api/gurus/{id}`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

---

## 👨‍🎓 SISWA Management

### Get All Siswa

**Endpoint:** `GET http://127.0.0.1:8000/api/siswas`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

**Query Parameters:**

-   `kelas_id=1` - Filter by kelas
-   `status=aktif` - Filter by status
-   `search=nama` - Search by nama, nis

### Create Siswa

**Endpoint:** `POST http://127.0.0.1:8000/api/siswas`

**Body (JSON):**

```json
{
    "nis": "2021001",
    "nisn": "0012345678",
    "nama": "Ahmad Rizki",
    "email": "ahmad.rizki@student.com",
    "jenis_kelamin": "L",
    "kelas_id": 1,
    "status": "aktif"
}
```

---

## 📚 MATA PELAJARAN Management

### Get All Mata Pelajaran

**Endpoint:** `GET http://127.0.0.1:8000/api/mata-pelajarans`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

### Create Mata Pelajaran

**Endpoint:** `POST http://127.0.0.1:8000/api/mata-pelajarans`

**Body (JSON):**

```json
{
    "kode": "MTK",
    "nama": "Matematika",
    "deskripsi": "Mata pelajaran matematika",
    "kkm": 75,
    "status": "aktif"
}
```

---

## 🏫 KELAS Management

### Get All Kelas

**Endpoint:** `GET http://127.0.0.1:8000/api/kelas`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

**Query Parameters:**

-   `tingkat=10` - Filter by tingkat (10, 11, 12)
-   `jurusan=IPA` - Filter by jurusan

### Create Kelas

**Endpoint:** `POST http://127.0.0.1:8000/api/kelas`

**Body (JSON):**

```json
{
    "nama": "X IPA 1",
    "tingkat": 10,
    "jurusan": "IPA",
    "wali_kelas_id": 1,
    "kapasitas": 36,
    "ruangan": "R101",
    "status": "aktif"
}
```

---

## 📅 JADWAL Management

### Get All Jadwal

**Endpoint:** `GET http://127.0.0.1:8000/api/jadwals`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

**Query Parameters:**

-   `kelas_id=1` - Filter by kelas
-   `guru_id=1` - Filter by guru
-   `hari=Senin` - Filter by hari

### Create Jadwal

**Endpoint:** `POST http://127.0.0.1:8000/api/jadwals`

**Body (JSON):**

```json
{
    "kelas_id": 1,
    "mata_pelajaran_id": 1,
    "guru_id": 1,
    "hari": "Senin",
    "jam_mulai": "07:00",
    "jam_selesai": "08:30",
    "ruangan": "R101",
    "semester": "Ganjil",
    "tahun_ajaran": "2024/2025",
    "status": "aktif"
}
```

---

## ✅ KEHADIRAN Management

### Get All Kehadiran

**Endpoint:** `GET http://127.0.0.1:8000/api/kehadirans`

**Headers:**

```
Authorization: Bearer {your_token_here}
Accept: application/json
```

**Query Parameters:**

-   `jadwal_id=1` - Filter by jadwal
-   `siswa_id=1` - Filter by siswa
-   `tanggal=2025-10-08` - Filter by tanggal
-   `status=Hadir` - Filter by status

### Create Kehadiran

**Endpoint:** `POST http://127.0.0.1:8000/api/kehadirans`

**Body (JSON):**

```json
{
    "jadwal_id": 1,
    "siswa_id": 1,
    "tanggal": "2025-10-08",
    "status": "Hadir",
    "waktu_absen": "2025-10-08 07:05:00",
    "keterangan": null
}
```

**Status Options:**

-   `Hadir`
-   `Sakit`
-   `Izin`
-   `Alpa`

---

## 🔧 Troubleshooting

### Error: "Unauthenticated"

**Solusi:**

1. Pastikan Anda sudah login dan mendapatkan token
2. Tambahkan header: `Authorization: Bearer {token}`
3. Pastikan token belum expired

### Error: "404 Not Found"

**Solusi:**

1. Pastikan server Laravel running: `php artisan serve`
2. Periksa URL sudah benar: `http://127.0.0.1:8000/api/...`
3. Periksa routes sudah terdaftar: `php artisan route:list`

### Error: "Validation Error"

**Solusi:**

1. Periksa required fields sudah diisi semua
2. Periksa format data (email, tanggal, enum)
3. Baca pesan error di response

### Error: "Foreign key constraint fails"

**Solusi:**

1. Pastikan data parent sudah ada (contoh: kelas_id harus ada di tabel kelas)
2. Jalankan seeder untuk data awal: `php artisan db:seed`

---

## 📝 Tips Postman

### 1. Simpan Token di Environment Variable

-   Buat Environment baru di Postman
-   Tambahkan variable `auth_token`
-   Gunakan di header: `{{auth_token}}`

### 2. Create Collection

-   Simpan semua request dalam satu collection
-   Mudah untuk export/import dan share

### 3. Use Pre-request Scripts

```javascript
// Auto set token from login response
pm.test("Save token", function () {
    var jsonData = pm.response.json();
    pm.environment.set("auth_token", jsonData.data.token);
});
```

---

## 🎯 Quick Test Checklist

-   [ ] Register user baru
-   [ ] Login dan simpan token
-   [ ] Test GET /api/gurus (harus ada auth header)
-   [ ] Test POST /api/gurus (create data baru)
-   [ ] Test GET /api/gurus/{id} (lihat detail)
-   [ ] Test PUT /api/gurus/{id} (update data)
-   [ ] Test DELETE /api/gurus/{id} (hapus data)
-   [ ] Ulangi untuk endpoint lain (siswas, kelas, dll)

---

## 📞 Support

Jika masih ada error, cek:

1. `storage/logs/laravel.log` - Laravel error logs
2. Response error message dari API
3. Database connection di `.env`
4. `php artisan route:list` - List semua routes

**Database Info:**

-   Host: localhost
-   Database: aplikasimonitoringkelas
-   User: root
-   Password: (sesuai .env)
