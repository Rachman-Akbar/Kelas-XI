# Perbaikan Error Login - Parameter Token Null

## Error yang Ditemukan

```
Error: Parameter specified as non-null is null: method
com.komputerkit.aplikasimonitoringapp.data.preferences.SessionManager.saveToken, parameter token
```

## Penyebab Error

Error terjadi karena ketidakcocokan antara struktur response API dengan model data di aplikasi Android:

1. **Field Token**: API mengirim `token`, tapi aplikasi mengharapkan `access_token`
2. **Field Nama User**: API mengirim `nama`, tapi aplikasi mengharapkan `name`
3. **Null Safety**: Token dideklarasikan sebagai non-null (`String`), tetapi bisa jadi null dari API

## Perbaikan yang Dilakukan

### 1. Memperbaiki Model LoginData (AuthModels.kt)

**Sebelum:**

```kotlin
data class LoginData(
    @SerializedName("access_token")
    val accessToken: String,
    val user: User
)
```

**Sesudah:**

```kotlin
data class LoginData(
    @SerializedName("token")
    val accessToken: String?,
    val user: User
)
```

**Perubahan:**

- Mengubah serialization name dari `access_token` menjadi `token` (sesuai response API)
- Mengubah tipe dari `String` menjadi `String?` (nullable) untuk menghindari crash

### 2. Memperbaiki Model User (AuthModels.kt)

**Sebelum:**

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    // ...
)
```

**Sesudah:**

```kotlin
data class User(
    val id: Int,
    @SerializedName("nama")
    val name: String,
    val email: String,
    val role: String,
    // ...
)
```

**Perubahan:**

- Menambahkan `@SerializedName("nama")` untuk mapping field `nama` dari API ke property `name`

### 3. Memperbaiki LoginViewModel

**Sebelum:**

```kotlin
if (loginResponse.success) {
    val sessionManager = SessionManager(context)
    sessionManager.saveToken(loginResponse.data.accessToken)
    sessionManager.saveUserData(loginResponse.data.user)
    _uiState.value = LoginUiState.Success(loginResponse.data.user.role)
}
```

**Sesudah:**

```kotlin
if (loginResponse.success) {
    // Validasi token tidak null
    val token = loginResponse.data.accessToken
    if (token.isNullOrEmpty()) {
        _uiState.value = LoginUiState.Error("Token tidak valid dari server")
        return@launch
    }

    // Save token dan user data
    val sessionManager = SessionManager(context)
    sessionManager.saveToken(token)
    sessionManager.saveUserData(loginResponse.data.user)

    // Success dengan role
    _uiState.value = LoginUiState.Success(loginResponse.data.user.role)
}
```

**Perubahan:**

- Menambahkan validasi untuk memeriksa apakah token null atau kosong
- Memberikan error message yang jelas jika token tidak valid
- Memastikan token tidak null sebelum dikirim ke `saveToken()`

## Struktur Response API (AuthController.php)

Response yang dikirim oleh API:

```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "user": {
      "id": 1,
      "nama": "Admin User",
      "email": "admin@sekolah.com",
      "role": "admin",
      "created_at": "2024-10-08T10:00:00.000000Z"
    },
    "token": "1|abcdefghijklmnopqrstuvwxyz123456"
  }
}
```

## Cara Testing

1. **Build ulang aplikasi:**

   ```bash
   cd AplikasiMonitoringKelas
   ./gradlew clean assembleDebug
   ```

2. **Install aplikasi:**

   ```bash
   ./gradlew installDebug
   ```

   atau gunakan:

   ```bash
   ./install-app.bat
   ```

3. **Test login dengan kredensial:**

   - Email: `admin@sekolah.com`
   - Password: `admin123`

   atau

   - Email: `kepsek@sekolah.com`
   - Password: `kepsek123`

## Hasil yang Diharapkan

- ✅ Login berhasil tanpa crash
- ✅ Token tersimpan dengan benar di SharedPreferences
- ✅ User data tersimpan dengan benar
- ✅ Redirect ke halaman sesuai role (Admin/Kepsek/Kurikulum)
- ✅ Tidak ada error "Parameter specified as non-null is null"

## File yang Dimodifikasi

1. `app/src/main/java/com/komputerkit/aplikasimonitoringapp/data/model/AuthModels.kt`
2. `app/src/main/java/com/komputerkit/aplikasimonitoringapp/ui/login/LoginViewModel.kt`

## Catatan Tambahan

- Pastikan API server sudah berjalan di `http://<IP>:8000`
- Pastikan koneksi jaringan aplikasi sudah diatur dengan benar
- Jika masih ada masalah, periksa log di Logcat untuk detail error
