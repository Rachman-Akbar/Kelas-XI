# 🚀 CARA MENDAPATKAN OPENAI API KEY - LENGKAP

## **🎯 Step-by-Step Tutorial**

### **Step 1: Buat/Login ke OpenAI Account**

1. **Buka browser** dan kunjungi: **https://platform.openai.com**
2. **Klik "Sign up"** (jika belum punya akun) atau **"Log in"** (jika sudah punya)
3. **Daftar menggunakan**:
   - Email dan password
   - Atau login dengan Google/Microsoft
4. **Verifikasi email** jika diminta

### **Step 2: Setup Billing (PENTING!)**

⚠️ **WAJIB**: OpenAI mengharuskan setup billing untuk menggunakan API

1. Setelah login, **klik profile** di pojok kanan atas
2. Pilih **"Billing"**
3. Klik **"Add payment method"**
4. Masukkan **kartu kredit/debit** yang valid
5. **Set spending limit** (misalnya $5-$10 untuk percobaan)

### **Step 3: Generate API Key**

1. **Klik "API Keys"** di sidebar kiri
2. **Klik tombol hijau "Create new secret key"**
3. **Beri nama** key (misalnya: "EasyBot Android")
4. **Copy API key** yang muncul (dimulai dengan `sk-proj-...`)

⚠️ **PENTING**: Key hanya ditampilkan sekali! Simpan dengan aman.

### **Step 4: Masukkan ke Aplikasi**

1. **Buka Android Studio**
2. **Navigate ke**: `app/src/main/java/com/komputerkit/easybot/config/OpenAIConfig.kt`
3. **Ganti baris ini**:

   ```kotlin
   const val API_KEY = "MASUKKAN_API_KEY_ANDA_DISINI"
   ```

   **Dengan**:

   ```kotlin
   const val API_KEY = "sk-proj-PASTE_YOUR_API_KEY_HERE"
   ```

### **Step 5: Build & Test**

1. **Build aplikasi**:
   ```bash
   ./gradlew assembleDebug
   ```
2. **Install ke device**
3. **Test kirim pesan** - Bot sekarang akan merespons!

---

## **💳 Info Biaya OpenAI API**

### **Pricing (Sangat Murah!)**

- **GPT-3.5-turbo**: ~$0.0015 per 1K tokens (~750 kata)
- **Contoh**: 100 percakapan = sekitar $0.50 (Rp 7,500)

### **Free Credits**

- **Akun baru**: Biasanya dapat free credits $5
- **Cukup** untuk 3000+ percakapan bot

---

## **🔒 Keamanan API Key**

### **DO:**

- ✅ Simpan API key dengan aman
- ✅ Jangan commit ke public repository
- ✅ Set spending limit di OpenAI dashboard

### **DON'T:**

- ❌ Share API key dengan orang lain
- ❌ Upload ke GitHub public
- ❌ Hardcode di production app

---

## **🐛 Troubleshooting**

### **Error: "Insufficient quota"**

- **Solusi**: Add credit ke billing account

### **Error: "Invalid API key"**

- **Solusi**: Re-generate API key baru

### **Error: "Rate limit exceeded"**

- **Solusi**: Tunggu 1 menit, atau upgrade plan

---

## **✅ Setelah Setup Berhasil:**

Bot EasyBot akan:

- ✅ **Merespons pesan** dengan cepat
- ✅ **Memberikan jawaban** yang relevan
- ✅ **Bekerja stabil** tanpa error
- ✅ **Logging detail** untuk debugging

**Happy coding! 🚀 Bot Anda siap digunakan!**
