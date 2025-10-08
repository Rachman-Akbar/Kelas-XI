# Easy Bot - Android Chatbot dengan OpenAI GPT

Aplikasi chatbot Android yang dibangun menggunakan Kotlin, Jetpack Compose, dan OpenAI GPT API.

## Fitur

- ✅ Antarmuka chat modern dengan Jetpack Compose
- ✅ Header "Easy Bot" dengan tombol hapus chat
- ✅ LazyColumn untuk menampilkan riwayat pesan yang dapat digulir
- ✅ Pesan pengguna di sisi kanan (biru) dan bot di sisi kiri (abu-abu)
- ✅ Input pesan di bagian bawah dengan tombol kirim
- ✅ Indikator "Typing..." saat menunggu respons
- ✅ Integrasi dengan OpenAI GPT API
- ✅ Menyimpan riwayat percakapan untuk konteks
- ✅ Penanganan error dasar
- ✅ Auto-scroll ke pesan terbaru
- ✅ Welcome screen untuk pengalaman pengguna yang lebih baik

## Instalasi dan Penggunaan

### 1. Setup API Key

1. Buat akun di [OpenAI Platform](https://platform.openai.com/)
2. Dapatkan API Key dari [OpenAI API Keys](https://platform.openai.com/api-keys)
3. Buka file `app/src/main/java/com/komputerkit/easybot/config/OpenAIConfig.kt`
4. Ganti `YOUR_OPENAI_API_KEY_HERE` dengan API Key yang valid:

```kotlin
const val API_KEY = "your_actual_openai_api_key_here"
```

### 2. Build dan Run

1. Pastikan Android Studio dan SDK ter-update
2. Buka proyek di Android Studio
3. Sync dependencies dengan menekan "Sync Now"
4. Run aplikasi di emulator atau device fisik

### 3. Penggunaan

1. Buka aplikasi EasyBot
2. Ketik pesan di kolom input di bagian bawah
3. Tekan tombol kirim (ikon panah)
4. Bot akan menampilkan "Typing..." saat memproses
5. Respons bot akan muncul di sisi kiri
6. Gunakan tombol hapus (trash icon) untuk membersihkan chat

## Struktur Proyek

```
app/src/main/java/com/komputerkit/easybot/
├── config/
│   └── OpenAIConfig.kt          # Konfigurasi API Key OpenAI
├── data/
│   ├── Message.kt               # Data class untuk pesan
│   └── openai/
│       └── OpenAIModels.kt      # Data models untuk OpenAI API
├── network/
│   ├── OpenAIService.kt         # Retrofit service untuk OpenAI API
│   └── NetworkModule.kt         # Konfigurasi network
├── repository/
│   └── ChatRepository.kt        # Repository untuk mengelola API calls
├── ui/
│   ├── components/
│   │   ├── MessageBubble.kt     # Komponen bubble pesan
│   │   └── MessageInput.kt      # Komponen input pesan
│   └── screens/
│       └── ChatScreen.kt        # Layar utama chat
├── viewmodel/
│   └── ChatViewModel.kt         # ViewModel untuk logika bisnis
└── MainActivity.kt              # Activity utama
```

## Dependencies

- **Jetpack Compose**: UI toolkit modern untuk Android
- **ViewModel Compose**: State management yang reactive
- **Retrofit**: HTTP client untuk API calls
- **OkHttp**: HTTP client library
- **Gson**: JSON serialization/deserialization
- **Coroutines**: Async programming
- **Material 3**: Design system terbaru dari Google

## Cara Mendapatkan OpenAI API Key

1. **Daftar/Login** ke [OpenAI Platform](https://platform.openai.com/)
2. **Verifikasi akun** dengan nomor telepon
3. **Pergi ke** [API Keys page](https://platform.openai.com/api-keys)
4. **Klik** "Create new secret key"
5. **Copy** API key dan simpan dengan aman
6. **Isi** credit/billing jika diperlukan

**⚠️ Catatan**: OpenAI memberikan $5 free credit untuk akun baru.

## Keamanan

⚠️ **Penting**: Jangan commit API Key ke repository publik. Untuk produksi, pertimbangkan:

- Menggunakan BuildConfig untuk menyimpan API Key
- Menyimpan di file properties yang di-gitignore
- Menggunakan Android Keystore untuk enkripsi
- Implementasi server-side proxy untuk API calls

## Troubleshooting

### Error: API Key tidak valid

- Pastikan API Key sudah benar dan aktif
- Cek quota dan billing di OpenAI Platform
- Pastikan format "Bearer YOUR_API_KEY" sudah benar

### Error: Network/Internet

- Pastikan permission INTERNET sudah ditambahkan
- Cek koneksi internet device/emulator
- Test API key dengan curl atau Postman terlebih dahulu

### Error: Rate Limit (429)

- Tunggu beberapa menit sebelum mencoba lagi
- Upgrade plan OpenAI jika perlu
- Implementasi retry mechanism

### Error: Build/Compile

- Pastikan semua dependencies sudah ter-sync
- Clean dan rebuild project

## Kontribusi

Jika ingin berkontribusi:

1. Fork repository ini
2. Buat feature branch
3. Commit perubahan
4. Push ke branch
5. Buat Pull Request

## Lisensi

Proyek ini dibuat untuk tujuan edukasi dan pembelajaran.

---

**Dibuat dengan ❤️ menggunakan Kotlin dan Jetpack Compose**
