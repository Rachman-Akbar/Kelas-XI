# 📮 Postman Collection - Complete API Examples

## 🔐 AUTHENTICATION

### 1. Login (POST)

**URL:** `http://127.0.0.1:8000/api/login`

**Headers:**

```
Content-Type: application/json
Accept: application/json
```

**Body (raw JSON):**

```json
{
    "email": "admin@example.com",
    "password": "password"
}
```

**Response (200 OK):**

```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "user": {
            "id": 1,
            "name": "Admin User",
            "email": "admin@example.com",
            "role": "admin",
            "created_at": "2025-10-08T10:00:00.000000Z",
            "updated_at": "2025-10-08T10:00:00.000000Z"
        },
        "token": "1|abcdefghijklmnopqrstuvwxyz123456"
    }
}
```

**⚠️ IMPORTANT:** Copy the `token` value and use it in all subsequent requests!

---

## 👨‍🏫 GURU MANAGEMENT

### 2. Get All Gurus (GET)

**URL:** `http://127.0.0.1:8000/api/gurus`

**Headers:**

```
Authorization: Bearer 1|abcdefghijklmnopqrstuvwxyz123456
Accept: application/json
```

**Query Parameters (Optional):**

```
?status=aktif&search=budi&per_page=10
```

**Response (200 OK):**

```json
{
    "success": true,
    "message": "Data guru berhasil diambil",
    "data": {
        "current_page": 1,
        "data": [
            {
                "id": 1,
                "nip": "198501012010011001",
                "nama": "Budi Santoso",
                "email": "budi@sekolah.com",
                "no_telp": "081234567890",
                "alamat": "Jl. Pendidikan No. 123",
                "jenis_kelamin": "L",
                "tanggal_lahir": "1985-01-01",
                "status": "aktif",
                "created_at": "2025-10-08T10:00:00.000000Z",
                "updated_at": "2025-10-08T10:00:00.000000Z"
            }
        ],
        "first_page_url": "http://127.0.0.1:8000/api/gurus?page=1",
        "from": 1,
        "last_page": 1,
        "last_page_url": "http://127.0.0.1:8000/api/gurus?page=1",
        "next_page_url": null,
        "path": "http://127.0.0.1:8000/api/gurus",
        "per_page": 15,
        "prev_page_url": null,
        "to": 1,
        "total": 1
    }
}
```

### 3. Create Guru (POST)

**URL:** `http://127.0.0.1:8000/api/gurus`

**Headers:**

```
Authorization: Bearer 1|abcdefghijklmnopqrstuvwxyz123456
Content-Type: application/json
Accept: application/json
```

**Body (raw JSON):**

```json
{
    "nip": "198501012010011001",
    "nama": "Budi Santoso",
    "email": "budi.santoso@sekolah.com",
    "no_telp": "081234567890",
    "alamat": "Jl. Pendidikan No. 123, Jakarta",
    "jenis_kelamin": "L",
    "tanggal_lahir": "1985-01-01",
    "status": "aktif"
}
```

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Guru berhasil ditambahkan",
    "data": {
        "id": 1,
        "nip": "198501012010011001",
        "nama": "Budi Santoso",
        "email": "budi.santoso@sekolah.com",
        "no_telp": "081234567890",
        "alamat": "Jl. Pendidikan No. 123, Jakarta",
        "jenis_kelamin": "L",
        "tanggal_lahir": "1985-01-01",
        "status": "aktif",
        "created_at": "2025-10-08T10:15:00.000000Z",
        "updated_at": "2025-10-08T10:15:00.000000Z"
    }
}
```

**Error Response (422 Validation Error):**

```json
{
    "success": false,
    "message": "Validation Error",
    "errors": {
        "nip": ["The nip field is required."],
        "email": ["The email has already been taken."]
    }
}
```

### 4. Get Single Guru (GET)

**URL:** `http://127.0.0.1:8000/api/gurus/1`

**Headers:**

```
Authorization: Bearer 1|abcdefghijklmnopqrstuvwxyz123456
Accept: application/json
```

**Response (200 OK):**

```json
{
    "success": true,
    "message": "Data guru berhasil diambil",
    "data": {
        "id": 1,
        "nip": "198501012010011001",
        "nama": "Budi Santoso",
        "email": "budi.santoso@sekolah.com",
        "no_telp": "081234567890",
        "alamat": "Jl. Pendidikan No. 123, Jakarta",
        "jenis_kelamin": "L",
        "tanggal_lahir": "1985-01-01",
        "status": "aktif",
        "created_at": "2025-10-08T10:15:00.000000Z",
        "updated_at": "2025-10-08T10:15:00.000000Z",
        "jadwals": [],
        "kelas_wali": null
    }
}
```

### 5. Update Guru (PUT)

**URL:** `http://127.0.0.1:8000/api/gurus/1`

**Headers:**

```
Authorization: Bearer 1|abcdefghijklmnopqrstuvwxyz123456
Content-Type: application/json
Accept: application/json
```

**Body (raw JSON):**

```json
{
    "nama": "Budi Santoso Updated",
    "no_telp": "081234567999",
    "status": "aktif"
}
```

**Response (200 OK):**

```json
{
    "success": true,
    "message": "Guru berhasil diupdate",
    "data": {
        "id": 1,
        "nip": "198501012010011001",
        "nama": "Budi Santoso Updated",
        "email": "budi.santoso@sekolah.com",
        "no_telp": "081234567999",
        "alamat": "Jl. Pendidikan No. 123, Jakarta",
        "jenis_kelamin": "L",
        "tanggal_lahir": "1985-01-01",
        "status": "aktif",
        "created_at": "2025-10-08T10:15:00.000000Z",
        "updated_at": "2025-10-08T10:30:00.000000Z"
    }
}
```

### 6. Delete Guru (DELETE)

**URL:** `http://127.0.0.1:8000/api/gurus/1`

**Headers:**

```
Authorization: Bearer 1|abcdefghijklmnopqrstuvwxyz123456
Accept: application/json
```

**Response (200 OK):**

```json
{
    "success": true,
    "message": "Guru berhasil dihapus",
    "data": null
}
```

---

## 🏫 KELAS MANAGEMENT

### 7. Create Kelas (POST)

**URL:** `http://127.0.0.1:8000/api/kelas`

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

**Body:**

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

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Kelas berhasil ditambahkan",
    "data": {
        "id": 1,
        "nama": "X IPA 1",
        "tingkat": 10,
        "jurusan": "IPA",
        "wali_kelas_id": 1,
        "kapasitas": 36,
        "jumlah_siswa": 0,
        "ruangan": "R101",
        "status": "aktif",
        "created_at": "2025-10-08T10:45:00.000000Z",
        "updated_at": "2025-10-08T10:45:00.000000Z",
        "wali_kelas": {
            "id": 1,
            "nip": "198501012010011001",
            "nama": "Budi Santoso"
        }
    }
}
```

---

## 👨‍🎓 SISWA MANAGEMENT

### 8. Create Siswa (POST)

**URL:** `http://127.0.0.1:8000/api/siswas`

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

**Body:**

```json
{
    "nis": "2021001",
    "nisn": "0012345678",
    "nama": "Ahmad Rizki",
    "email": "ahmad.rizki@student.com",
    "no_telp": "081298765432",
    "alamat": "Jl. Siswa No. 1",
    "jenis_kelamin": "L",
    "tanggal_lahir": "2008-05-15",
    "kelas_id": 1,
    "nama_orang_tua": "Bapak Ahmad",
    "no_telp_orang_tua": "081234567890",
    "status": "aktif"
}
```

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Siswa berhasil ditambahkan",
    "data": {
        "id": 1,
        "nis": "2021001",
        "nisn": "0012345678",
        "nama": "Ahmad Rizki",
        "email": "ahmad.rizki@student.com",
        "no_telp": "081298765432",
        "alamat": "Jl. Siswa No. 1",
        "jenis_kelamin": "L",
        "tanggal_lahir": "2008-05-15",
        "kelas_id": 1,
        "nama_orang_tua": "Bapak Ahmad",
        "no_telp_orang_tua": "081234567890",
        "status": "aktif",
        "created_at": "2025-10-08T11:00:00.000000Z",
        "updated_at": "2025-10-08T11:00:00.000000Z",
        "kelas": {
            "id": 1,
            "nama": "X IPA 1",
            "tingkat": 10
        }
    }
}
```

---

## 📚 MATA PELAJARAN MANAGEMENT

### 9. Create Mata Pelajaran (POST)

**URL:** `http://127.0.0.1:8000/api/mata-pelajarans`

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

**Body:**

```json
{
    "kode": "MTK",
    "nama": "Matematika",
    "deskripsi": "Mata pelajaran matematika untuk tingkat SMA",
    "kkm": 75,
    "status": "aktif"
}
```

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Mata pelajaran berhasil ditambahkan",
    "data": {
        "id": 1,
        "kode": "MTK",
        "nama": "Matematika",
        "deskripsi": "Mata pelajaran matematika untuk tingkat SMA",
        "kkm": 75,
        "status": "aktif",
        "created_at": "2025-10-08T11:15:00.000000Z",
        "updated_at": "2025-10-08T11:15:00.000000Z"
    }
}
```

---

## 📅 JADWAL MANAGEMENT

### 10. Create Jadwal (POST)

**URL:** `http://127.0.0.1:8000/api/jadwals`

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

**Body:**

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

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Jadwal berhasil ditambahkan",
    "data": {
        "id": 1,
        "kelas_id": 1,
        "mata_pelajaran_id": 1,
        "guru_id": 1,
        "hari": "Senin",
        "jam_mulai": "07:00:00",
        "jam_selesai": "08:30:00",
        "ruangan": "R101",
        "semester": "Ganjil",
        "tahun_ajaran": "2024/2025",
        "status": "aktif",
        "created_at": "2025-10-08T11:30:00.000000Z",
        "updated_at": "2025-10-08T11:30:00.000000Z",
        "kelas": {
            "id": 1,
            "nama": "X IPA 1"
        },
        "mata_pelajaran": {
            "id": 1,
            "kode": "MTK",
            "nama": "Matematika"
        },
        "guru": {
            "id": 1,
            "nip": "198501012010011001",
            "nama": "Budi Santoso"
        }
    }
}
```

---

## ✅ KEHADIRAN MANAGEMENT

### 11. Create Kehadiran (POST)

**URL:** `http://127.0.0.1:8000/api/kehadirans`

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

**Body:**

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

**Response (201 Created):**

```json
{
    "success": true,
    "message": "Kehadiran berhasil dicatat",
    "data": {
        "id": 1,
        "jadwal_id": 1,
        "siswa_id": 1,
        "tanggal": "2025-10-08",
        "status": "Hadir",
        "waktu_absen": "2025-10-08 07:05:00",
        "keterangan": null,
        "created_at": "2025-10-08T07:05:00.000000Z",
        "updated_at": "2025-10-08T07:05:00.000000Z",
        "jadwal": {
            "id": 1,
            "hari": "Senin",
            "jam_mulai": "07:00:00",
            "jam_selesai": "08:30:00"
        },
        "siswa": {
            "id": 1,
            "nis": "2021001",
            "nama": "Ahmad Rizki"
        }
    }
}
```

---

## 🔍 Query Parameters Examples

### Filter by Status

```
GET http://127.0.0.1:8000/api/gurus?status=aktif
```

### Search

```
GET http://127.0.0.1:8000/api/gurus?search=budi
```

### Pagination

```
GET http://127.0.0.1:8000/api/gurus?per_page=10&page=2
```

### Multiple Filters

```
GET http://127.0.0.1:8000/api/gurus?status=aktif&search=budi&per_page=5
```

### Filter Siswa by Kelas

```
GET http://127.0.0.1:8000/api/siswas?kelas_id=1
```

### Filter Jadwal by Hari

```
GET http://127.0.0.1:8000/api/jadwals?hari=Senin&kelas_id=1
```

### Filter Kehadiran by Tanggal

```
GET http://127.0.0.1:8000/api/kehadirans?tanggal=2025-10-08&status=Hadir
```

---

## ⚠️ Common Errors

### 401 Unauthenticated

```json
{
    "message": "Unauthenticated."
}
```

**Solution:** Add `Authorization: Bearer {token}` header

### 404 Not Found

```json
{
    "success": false,
    "message": "Guru tidak ditemukan",
    "data": null
}
```

**Solution:** Check if the ID exists

### 422 Validation Error

```json
{
    "success": false,
    "message": "Validation Error",
    "errors": {
        "email": ["The email has already been taken."],
        "nip": ["The nip field is required."]
    }
}
```

**Solution:** Fix the validation errors in your request body

### 500 Server Error

```json
{
    "success": false,
    "message": "Terjadi kesalahan: ...",
    "data": null
}
```

**Solution:** Check Laravel logs in `storage/logs/laravel.log`

---

## 📝 Postman Tips

### Save Token Automatically

Add this to Tests tab of Login request:

```javascript
var jsonData = pm.response.json();
pm.environment.set("auth_token", jsonData.data.token);
```

### Use Token in Requests

In Authorization tab, select "Bearer Token" and use:

```
{{auth_token}}
```

### Pre-request Script for Auth Header

```javascript
pm.request.headers.add({
    key: "Authorization",
    value: "Bearer " + pm.environment.get("auth_token"),
});
```

---

**Happy Testing! 🚀**
