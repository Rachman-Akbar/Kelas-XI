# Setup Android Studio untuk Koneksi ke Laravel API

## Struktur Project Android

Project sudah dikonfigurasi dengan:

- ✅ Retrofit untuk HTTP requests
- ✅ Gson untuk JSON parsing
- ✅ OkHttp untuk logging
- ✅ Coroutines untuk async operations
- ✅ ViewModel dan Compose

## Konfigurasi Base URL

### Lokasi File: `utils/ApiConfig.kt`

File ini berisi berbagai base URL untuk environment berbeda:

```kotlin
object ApiConfig {
    object BaseUrls {
        const val EMULATOR = "http://10.0.2.2:8000/api/"
        const val LOCALHOST = "http://127.0.0.1:8000/api/"
        const val PHYSICAL_DEVICE = "http://192.168.1.100:8000/api/"
        const val PRODUCTION = "https://your-domain.com/api/"
    }
}
```

### Cara Mengganti Base URL

**File:** `data/api/RetrofitClient.kt`

#### Untuk Android Emulator (Default):

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/api/"
```

#### Untuk Physical Device:

1. Cari IP komputer Anda:

   - Windows: Buka Command Prompt, ketik `ipconfig`
   - Cari **IPv4 Address** (contoh: 192.168.1.100)

2. Update BASE_URL:

```kotlin
private const val BASE_URL = "http://192.168.1.100:8000/api/"
```

**PENTING:** Ganti `192.168.1.100` dengan IP Address komputer Anda!

3. Pastikan:
   - Komputer dan HP di WiFi yang sama
   - Firewall tidak memblokir port 8000
   - Laravel server berjalan (`php artisan serve`)

#### Untuk Production:

```kotlin
private const val BASE_URL = "https://yourdomain.com/api/"
```

## Menggunakan API

### 1. Login

```kotlin
// Di ViewModel
viewModelScope.launch {
    try {
        val response = RetrofitClient.apiService.login(
            LoginRequest(
                email = "admin@sekolah.com",
                password = "admin123"
            )
        )

        if (response.isSuccessful && response.body()?.success == true) {
            val userData = response.body()?.data
            // Handle success
        } else {
            // Handle error
        }
    } catch (e: Exception) {
        // Handle exception
    }
}
```

### 2. Register

```kotlin
val response = RetrofitClient.apiService.register(
    RegisterRequest(
        nama = "John Doe",
        email = "john@example.com",
        password = "password123",
        role = "siswa"
    )
)
```

### 3. Get Users

```kotlin
val response = RetrofitClient.apiService.getAllUsers()
if (response.isSuccessful && response.body()?.success == true) {
    val users = response.body()?.data
    // Display users
}
```

### 4. Test Connection

```kotlin
val response = RetrofitClient.apiService.testConnection()
if (response.isSuccessful) {
    Log.d("API", "Connection successful!")
}
```

## Akun Test

Gunakan akun ini untuk testing:

| Email                  | Password     | Role           |
| ---------------------- | ------------ | -------------- |
| admin@sekolah.com      | admin123     | admin          |
| kepsek@sekolah.com     | kepsek123    | kepala_sekolah |
| kurikulum@sekolah.com  | kurikulum123 | kurikulum      |
| budi.guru@sekolah.com  | budi123      | siswa          |
| andi.siswa@sekolah.com | andi123      | siswa          |

## Permissions

Pastikan di `AndroidManifest.xml` ada permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Dan attribute:

```xml
android:usesCleartextTraffic="true"
```

✅ Sudah dikonfigurasi di project ini!

## Debugging

### Melihat API Logs

Logs akan muncul di Logcat dengan tag `OkHttp`:

1. Buka Logcat di Android Studio
2. Filter dengan "OkHttp" atau "API"
3. Lihat request dan response

### Common Issues

#### Error: Unable to resolve host / Connection refused

**Solusi:**

- Pastikan Laravel server berjalan
- Cek BASE_URL sudah benar
- Untuk emulator, gunakan `10.0.2.2` bukan `localhost` atau `127.0.0.1`
- Untuk physical device, pastikan di WiFi yang sama

#### Error: Cleartext HTTP traffic not permitted

**Solusi:**

- Sudah ada `android:usesCleartextTraffic="true"` di manifest
- Atau gunakan HTTPS untuk production

#### Error: 404 Not Found

**Solusi:**

- Cek endpoint di `ApiService.kt` cocok dengan `routes/api.php`
- Pastikan ada `/api/` di base URL
- Jalankan `php artisan route:list` untuk melihat available routes

#### Error: JSON parsing error

**Solusi:**

- Cek struktur ApiResponse<T> cocok dengan response dari Laravel
- Lihat response di Logcat
- Pastikan `@SerializedName` cocok dengan field dari API

## Testing Flow

1. **Start Laravel Server**

   ```bash
   cd aplikasimonitoring-api
   php artisan serve
   ```

2. **Test di Browser**

   ```
   http://127.0.0.1:8000/api/test
   ```

3. **Update BASE_URL di Android**

   - Emulator: `http://10.0.2.2:8000/api/`
   - Physical: `http://YOUR_IP:8000/api/`

4. **Run Android App**
   - Build & Run
   - Cek Logcat untuk API logs
   - Test login dengan akun test

## Struktur File Penting

```
app/src/main/java/com/komputerkit/aplikasimonitoringapp/
├── data/
│   ├── api/
│   │   ├── ApiService.kt          # API endpoints
│   │   ├── ApiModels.kt           # Request/Response models
│   │   └── RetrofitClient.kt      # Retrofit configuration
│   ├── repository/
│   │   └── AuthRepository.kt      # Data layer
│   └── DataModels.kt              # App models
├── ui/
│   ├── viewmodel/
│   │   └── LoginViewModel.kt      # Business logic
│   └── screens/
│       ├── LoginScreen.kt         # Login UI
│       └── HomeScreen.kt          # Home UI
└── utils/
    └── ApiConfig.kt               # URL configurations
```

## Menambah Endpoint Baru

### 1. Update ApiService.kt

```kotlin
@GET("jadwal")
suspend fun getJadwal(): Response<ApiResponse<List<Jadwal>>>
```

### 2. Buat Model Data

```kotlin
data class Jadwal(
    @SerializedName("id")
    val id: Int,
    @SerializedName("hari")
    val hari: String,
    @SerializedName("mata_pelajaran")
    val mataPelajaran: String
)
```

### 3. Panggil di ViewModel

```kotlin
viewModelScope.launch {
    val response = RetrofitClient.apiService.getJadwal()
    // Handle response
}
```

### 4. Buat Endpoint di Laravel

```php
// routes/api.php
Route::get('/jadwal', [JadwalController::class, 'index']);
```

## Tips Development

1. **Gunakan Postman** untuk test API dulu sebelum implement di Android
2. **Cek Logcat** untuk debug API calls
3. **Handle errors** dengan try-catch
4. **Loading state** untuk UX yang baik
5. **Save token** untuk authenticated requests (jika perlu)

## Production Checklist

- [ ] Ganti BASE_URL ke production URL
- [ ] Gunakan HTTPS
- [ ] Remove atau disable logging interceptor
- [ ] Setup ProGuard rules untuk Retrofit & Gson
- [ ] Test di real device
- [ ] Handle error cases dengan baik
- [ ] Add retry logic untuk network failures
- [ ] Implement proper token management

## Support

Jika ada masalah:

1. Cek Laravel logs: `aplikasimonitoring-api/storage/logs/laravel.log`
2. Cek Logcat di Android Studio
3. Test endpoint dengan Postman
4. Pastikan Laravel server running
5. Cek firewall & network connectivity
