# Fix Connection Error - Aplikasi Monitoring Kelas

## ✅ Yang Sudah Dilakukan:

1. ✅ IP Address laptop terdeteksi: **192.168.1.4**
2. ✅ ApiClient.kt sudah diupdate dengan IP: **http://192.168.1.4:8000/api/**
3. ✅ Laravel server sudah running di: **http://192.168.1.4:8000**
4. ✅ Aplikasi Android sudah di-rebuild dengan konfigurasi baru

## ⚠️ Langkah Manual yang Perlu Dilakukan:

### 1. Allow Port 8000 di Windows Firewall

**Pilihan A - Via GUI (Recommended):**

1. Buka **Windows Security** atau **Windows Defender Firewall**
2. Klik **Advanced settings**
3. Klik **Inbound Rules** di panel kiri
4. Klik **New Rule** di panel kanan
5. Pilih **Port** → Next
6. Pilih **TCP** → Specific local ports: **8000** → Next
7. Pilih **Allow the connection** → Next
8. Centang semua (Domain, Private, Public) → Next
9. Nama: **Laravel Development Server** → Finish

**Pilihan B - Via PowerShell (Run as Administrator):**

```powershell
netsh advfirewall firewall add rule name="Laravel Server Port 8000" dir=in action=allow protocol=TCP localport=8000
```

### 2. Test Koneksi dari HP

Setelah firewall rule ditambahkan, test di browser HP:

```
http://192.168.1.4:8000/api/test
```

Jika berhasil, akan muncul:

```json
{
  "success": true,
  "message": "API Connected Successfully!",
  "data": "Laravel API is running"
}
```

### 3. Install Aplikasi ke HP

**Via Android Studio:**

- Build → Generate Signed Bundle / APK → APK → Debug
- Transfer ke HP dan install

**Via ADB (Jika HP terhubung USB):**

```bash
cd AplikasiMonitoringKelas
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

**Atau jalankan script:**

```bash
cd AplikasiMonitoringKelas
install-app.bat
```

### 4. Login di Aplikasi

Credentials:

- Email: `admin@sekolah.com`
- Password: `admin123`

## 🔄 Start Server di Masa Depan

**Script Otomatis (Recommended):**

```bash
cd aplikasimonitoring-api
start-server-network.bat
```

**Manual:**

```bash
cd aplikasimonitoring-api
php artisan serve --host=192.168.1.4 --port=8000
```

## 🛠️ Troubleshooting

### Error: "Failed to connect"

- ✅ Pastikan laptop dan HP di WiFi yang sama
- ✅ Pastikan firewall rule sudah ditambahkan
- ✅ Pastikan Laravel server masih running
- ✅ Test di browser HP dulu: http://192.168.1.4:8000/api/test

### IP Berubah (Laptop restart/ganti WiFi)

1. Check IP baru: `ipconfig`
2. Update `ApiClient.kt` dengan IP baru
3. Rebuild aplikasi
4. Start server dengan IP baru

### Port 8000 Sudah Dipakai

```bash
# Stop all PHP processes
taskkill /F /IM php.exe

# Or use different port
php artisan serve --host=192.168.1.4 --port=8080
# Update ApiClient.kt juga ganti port ke 8080
```

## 📁 Files Created

1. **start-server-network.bat** - Start server with network access
2. **allow-firewall.bat** - Add firewall rule (run as admin)
3. **install-app.bat** - Install APK to device
4. **SETUP_GUIDE.md** - This file

## 🎯 Next Steps

Setelah berhasil login:

1. Test semua fitur di aplikasi
2. Verify API calls working (check Logcat)
3. Implement Jadwal screen dengan data dari API
4. Implement Admin CRUD screens

---

**Status Saat Ini:**

- ✅ Backend: Running di http://192.168.1.4:8000
- ✅ Frontend: APK built dengan BASE_URL updated
- ⏳ Pending: Firewall rule (manual step required)
- ⏳ Pending: Install APK ke device

**Kontak jika ada masalah:**

- Check Laravel logs: `aplikasimonitoring-api/storage/logs/laravel.log`
- Check Android Logcat for detailed errors
