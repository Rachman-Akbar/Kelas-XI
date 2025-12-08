# Aplikasi Monitoring Kelas - Panduan Konfigurasi Jaringan

## Permasalahan Login Lambat dan Timeout

Permasalahan login yang lambat dan timeout biasanya disebabkan oleh beberapa faktor:

### 1. Konfigurasi Jaringan
- Aplikasi harus terhubung ke server backend yang benar
- Penggunaan IP yang salah akan menyebabkan koneksi timeout

### 2. Konfigurasi Backend
- Database SQLite lambat untuk operasi concurrent
- Indexing database yang kurang optimal
- Kueri database yang tidak efisien

### 3. Konfigurasi Aplikasi Android
- Timeout yang terlalu lama
- Tidak ada penanganan kesalahan jaringan yang baik

## Solusi yang Telah Diimplementasikan

### Backend (api-aplikasimonitoringkelas):
1. **Optimasi Query Login**
   - Menggunakan select kolom spesifik daripada `select *`
   - Mengurangi jumlah data yang diambil saat login

2. **Penambahan Index Database**
   - Menambahkan index pada kolom `role`, `guru_id`, `kelas_id`, dan `email`
   - Meningkatkan kecepatan pencarian pengguna

3. **Optimasi Autentikasi**
   - Mengurangi operasi yang tidak perlu saat login
   - Menyederhanakan proses verifikasi pengguna

### Android App (AplikasiMonitoringKelas):
1. **Konfigurasi Jaringan Fleksibel**
   - Menggunakan `local.properties` untuk konfigurasi IP server
   - Memungkinkan perubahan IP server tanpa modifikasi kode

2. **Optimasi Timeout**
   - Mengurangi timeout dari 120-180 detik menjadi 15-60 detik
   - Mencegah aplikasi "membeku" saat jaringan buruk

3. **Penanganan Kesalahan yang Lebih Baik**
   - Menyediakan pesan error yang lebih informatif
   - Membedakan jenis kesalahan jaringan

## Cara Mengkonfigurasi Aplikasi untuk Bekerja dengan Server

### 1. Jalankan Server Backend
```bash
cd api-aplikasimonitoringkelas
php artisan serve --host=0.0.0.0 --port=8000
```

Pastikan server berjalan di IP lokal Anda (misal: 192.168.1.100:8000) dan dapat diakses dari perangkat Android.

### 2. Tentukan IP Komputer Anda
- Windows: Buka Command Prompt dan ketik `ipconfig`
- Linux/Mac: Buka Terminal dan ketik `ifconfig` atau `ip addr`
- Catat alamat IPv4 Anda (biasanya dalam format 192.168.x.x atau 10.x.x.x)

### 3. Konfigurasi Aplikasi Android
1. Buat file `local.properties` di folder `AplikasiMonitoringKelas/` (satu level dengan `app/` folder)
2. Salin isi dari `local.properties.example` ke `local.properties`
3. Ganti IP dengan IP komputer Anda:

```
backend.ip=192.168.1.100  # Ganti dengan IP komputer Anda
backend.port=8000
backend.protocol=http
```

### 4. Alternatif Konfigurasi
- **Untuk emulator Android Studio**: Gunakan `backend.ip=10.0.2.2`
- **Untuk perangkat fisik di jaringan yang sama**: Gunakan IP komputer Anda
- **Untuk pengujian di perangkat berbeda jaringan**: Gunakan IP publik server (jika tersedia)

### 5. Pastikan Firewall Memungkinkan
- Pastikan firewall komputer Anda mengizinkan koneksi masuk di port 8000
- Jika menggunakan Windows Defender Firewall, buat aturan untuk mengizinkan aplikasi PHP

## Tips untuk Kinerja Lebih Baik

### Untuk Pengembangan Lokal:
1. Gunakan database MySQL daripada SQLite (lebih cepat untuk pengujian bersama)
2. Jalankan server di jaringan lokal yang sama (WiFi)
3. Minimalkan jumlah fitur yang dijalankan saat pengujian login

### Untuk Produksi:
1. Gunakan protokol HTTPS
2. Gunakan server web production seperti Nginx atau Apache
3. Gunakan database MySQL/PostgreSQL daripada SQLite
4. Gunakan caching untuk endpoint yang sering diakses

## Troubleshooting

### Jika Masih Mengalami Timeout:
1. Pastikan server berjalan: `http://IP_KOMPUTER:8000`
2. Pastikan port 8000 terbuka di firewall
3. Pastikan jaringan stabil dan perangkat dalam jaringan yang sama
4. Coba ping IP server dari perangkat Android

### Jika Tidak Dapat Login:
1. Pastikan email dan password valid
2. Pastikan akun aktif dan memiliki role yang benar
3. Periksa log server untuk informasi kesalahan lebih lanjut
4. Gunakan fitur debug untuk melihat permintaan jaringan