# 🚀 Setup Guide - EasyBot OpenAI

Panduan lengkap untuk setup aplikasi EasyBot dengan OpenAI API.

## 📋 Prerequisites

- Android Studio (versi terbaru)
- Android SDK 24+
- Internet connection
- OpenAI Account

## 🔑 Step 1: Mendapatkan OpenAI API Key

### 1.1 Daftar/Login OpenAI

1. Buka [https://platform.openai.com/](https://platform.openai.com/)
2. Klik "Sign Up" atau "Log In"
3. Lengkapi data akun jika belum punya

### 1.2 Verifikasi Akun

1. Verifikasi email
2. Tambahkan nomor telepon untuk verifikasi
3. **Penting**: Tanpa verifikasi phone, Anda tidak bisa menggunakan API

### 1.3 Generate API Key

1. Pergi ke [https://platform.openai.com/api-keys](https://platform.openai.com/api-keys)
2. Klik "Create new secret key"
3. Beri nama key (misal: "EasyBot Android")
4. **COPY dan SIMPAN** key dengan aman
5. ⚠️ Key hanya ditampilkan sekali, jadi pastikan tersimpan!

### 1.4 Setup Billing (Opsional)

- OpenAI memberikan $5 free credit untuk akun baru
- Jika sudah habis, perlu add payment method di [Billing Settings](https://platform.openai.com/account/billing)

## 🛠️ Step 2: Setup Project

### 2.1 Clone/Download Project

```bash
git clone <repository-url>
cd EasyBot
```

### 2.2 Buka di Android Studio

1. Open Android Studio
2. "Open an Existing Project"
3. Pilih folder EasyBot
4. Tunggu sync dependencies

### 2.3 Setup API Key

1. Buka file: `app/src/main/java/com/komputerkit/easybot/config/OpenAIConfig.kt`
2. Ganti baris ini:

```kotlin
const val API_KEY = "YOUR_OPENAI_API_KEY_HERE"
```

Menjadi:

```kotlin
const val API_KEY = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" // Key Anda
```

## ✅ Step 3: Testing

### 3.1 Test Compilation

```bash
./gradlew compileDebugKotlin
```

Harus berhasil tanpa error.

### 3.2 Test API Key (Manual)

Buka [https://platform.openai.com/playground](https://platform.openai.com/playground) dan test dengan key Anda.

### 3.3 Build APK

```bash
./gradlew assembleDebug
```

### 3.4 Run App

1. Connect Android device atau start emulator
2. Click "Run" di Android Studio
3. Test chat functionality

## 🐛 Troubleshooting

### Error: "Invalid API Key"

**Solusi:**

- Cek key sudah benar dan tidak ada spasi/karakter tambahan
- Pastikan key dimulai dengan "sk-"
- Generate key baru jika perlu

### Error: "You exceeded your current quota"

**Solusi:**

- Cek usage di [https://platform.openai.com/account/usage](https://platform.openai.com/account/usage)
- Add payment method jika free credit habis
- Tunggu reset monthly quota

### Error: "Rate limit exceeded"

**Solusi:**

- Tunggu beberapa menit
- Jangan spam request
- Upgrade plan jika perlu

### Error: Network/Connection

**Solusi:**

- Cek internet connection
- Test di browser: `curl -X POST "https://api.openai.com/v1/chat/completions"`
- Pastikan tidak ada firewall blocking

### Error: Build/Compilation

**Solusi:**

```bash
./gradlew clean
./gradlew build
```

## 💡 Tips

1. **Keamanan**: Jangan commit API key ke git
2. **Monitoring**: Pantau usage di OpenAI dashboard
3. **Limits**: GPT-3.5-turbo lebih murah dari GPT-4
4. **Backup**: Simpan API key di password manager

## 🎯 Next Steps

Setelah setup berhasil:

1. Customize UI sesuai keinginan
2. Add features (voice input, image, etc.)
3. Implement proper error handling
4. Add analytics/logging
5. Prepare for production release

---

**Need help?** Check the main README.md or create an issue.
