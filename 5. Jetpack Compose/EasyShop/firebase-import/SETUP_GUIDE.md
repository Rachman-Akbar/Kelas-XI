# 🔑 Cara Mendapatkan Firebase Service Account Key

## 📝 Langkah-langkah

### 1. Buka Firebase Console

- Pergi ke [Firebase Console](https://console.firebase.google.com/)
- Pilih project EasyShop Anda

### 2. Akses Project Settings

- Klik ikon ⚙️ (gear) di sidebar kiri
- Pilih **"Project settings"**

### 3. Service Accounts Tab

- Klik tab **"Service accounts"**
- Scroll ke bawah ke bagian **"Firebase Admin SDK"**

### 4. Generate Private Key

- Pilih **"Node.js"** sebagai language
- Klik tombol **"Generate new private key"**
- Akan muncul dialog konfirmasi

### 5. Download Key File

- Klik **"Generate key"**
- File JSON akan otomatis ter-download
- **PENTING**: Simpan file ini dengan aman!

### 6. Rename & Move File

- Rename file yang di-download menjadi `service-account-key.json`
- Pindahkan ke folder `firebase-import/`

## 📁 Struktur Akhir

```
firebase-import/
├── service-account-key.json  ← File yang Anda download
├── package.json
├── firebase-config.js
└── ... (file lainnya)
```

## ⚠️ Security Warning

- **JANGAN** share file ini dengan siapa pun
- **JANGAN** commit ke Git/GitHub
- File ini memberikan akses penuh ke Firebase project Anda
- Simpan di tempat yang aman

## 🔍 Isi File service-account-key.json

File akan berisi struktur seperti ini:

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...",
  "client_email": "firebase-adminsdk-...@your-project.iam.gserviceaccount.com",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "...",
  "client_x509_cert_url": "..."
}
```

## ✅ Verifikasi

Setelah menempatkan file:

```bash
npm run test
```

Jika berhasil, Anda akan melihat:

```
✅ Firebase Admin SDK initialized successfully!
📱 Project ID: your-project-id
✅ Firestore: Write and read operations successful
✅ Firebase Auth: Connection successful
🎉 All Firebase services are working correctly!
```

## 🆘 Troubleshooting

### Error: "ENOENT: no such file or directory"

- Pastikan file `service-account-key.json` ada
- Check nama file (case sensitive)
- Pastikan file di folder yang benar

### Error: "Invalid service account"

- Download ulang service account key
- Pastikan menggunakan project yang benar
- Check Firebase project permissions

---

**Setelah file siap, jalankan import data! 🚀**
