# BlogApp Firebase Import Tool

Tool ini digunakan untuk mengimpor data sample ke Firestore database untuk aplikasi BlogApp.

## 🚀 Setup

### 1. Install Dependencies

```bash
cd firebase-import
npm install
```

### 2. Setup Environment Variables

1. Copy `.env.example` menjadi `.env`:

   ```bash
   cp .env.example .env
   ```

2. Edit file `.env` dan isi dengan konfigurasi Firebase Anda:
   ```env
   FIREBASE_PROJECT_ID=your-project-id-here
   FIREBASE_PRIVATE_KEY_ID=your-private-key-id
   FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nyour-private-key-here\n-----END PRIVATE KEY-----"
   FIREBASE_CLIENT_EMAIL=your-client-email@your-project.iam.gserviceaccount.com
   FIREBASE_CLIENT_ID=your-client-id
   ```

### 3. Cara Mendapatkan Service Account Key

1. Buka [Firebase Console](https://console.firebase.google.com)
2. Pilih project BlogApp Anda
3. Klik ⚙️ Settings > Project settings
4. Tab **Service accounts**
5. Klik **Generate new private key**
6. Download file JSON
7. Buka file JSON dan copy nilai-nilainya ke file `.env`

## 🔧 Commands

### Test Connection

```bash
npm run test
```

Menguji koneksi ke Firebase Firestore.

### Import Data

```bash
npm run import
# atau
npm start
```

Mengimpor sample data (users dan blogs) ke Firestore.

## 📊 Data yang Diimpor

### Users Collection

- 3 demo users dengan email dan profile lengkap

### Blogs Collection

- 6 sample blog posts dengan berbagai topik
- Data author yang sesuai dengan users
- Timestamp yang realistic (beberapa hari/jam yang lalu)

## 📁 File Structure

```
firebase-import/
├── package.json          # Node.js dependencies
├── .env.example          # Template environment variables
├── .env                  # Your actual environment variables (create this)
├── firebase-config.js    # Firebase Admin SDK configuration
├── sample-data.js        # Sample data untuk import
├── import-data.js        # Main import script
├── test-connection.js    # Test Firebase connection
└── README.md            # This file
```

## ⚠️ Important Notes

1. **Pastikan Firestore sudah diaktifkan** di Firebase Console
2. **Set Firestore Rules** untuk testing (sementara):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.time < timestamp.date(2024, 12, 31);
       }
     }
   }
   ```
3. **Jangan commit file `.env`** ke version control
4. **Service Account Key harus memiliki permission** untuk Firestore

## 🐛 Troubleshooting

### Error: "Firebase Admin SDK not initialized"

- Pastikan file `.env` sudah dibuat dan diisi dengan benar
- Check format private key (harus ada `\n` untuk line breaks)

### Error: "Permission denied"

- Pastikan Service Account memiliki role **Firebase Admin SDK Administrator Service Agent**
- Check Firestore rules

### Error: "Project not found"

- Pastikan `FIREBASE_PROJECT_ID` benar
- Check project masih aktif di Firebase Console
