# Struktur AplikasiMonitoringKelas

## Lapisan utama

- `app/src/main/java/.../data/api`: konfigurasi Retrofit dan kontrak endpoint API.
- `app/src/main/java/.../data/models`: DTO/model respons dan request API.
- `app/src/main/java/.../data/repository`: sumber data autentikasi dan fitur monitoring.
- `app/src/main/java/.../guru`: layar dan ViewModel role guru.
- `app/src/main/java/.../siswa`: layar dan ViewModel role siswa.
- `app/src/main/java/.../kurikulum`: layar dan ViewModel role kurikulum.
- `app/src/main/java/.../kepsek`: layar dan ViewModel role kepala sekolah.
- `app/src/main/java/.../common`: model/utilitas lintas fitur.
- `app/src/main/res`: resource Android yang sudah ada dan tidak diubah secara visual dalam perbaikan ini.

## Konfigurasi backend

Salin `local.properties.example` menjadi `local.properties`, lalu isi `backend.ip`, `backend.port`, dan `backend.protocol`. Untuk Android Emulator, host lokal komputer biasanya menggunakan `10.0.2.2`.

Konfigurasi URL hanya dipusatkan melalui `BuildConfig.BASE_URL` dan `ApiConfig` agar alamat server tidak tersebar di ViewModel/repository.
