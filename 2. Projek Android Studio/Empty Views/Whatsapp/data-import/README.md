# WhatsApp Clone - Data Import Tool

Folder ini berisi script Node.js untuk mengimpor data sample ke Firebase project Anda.

## 📋 Prerequisites

1. **Node.js** (versi 14 atau lebih baru)
2. **Firebase Project** dengan:
   - Authentication enabled
   - Realtime Database enabled
   - Firebase Admin SDK service account key

## 🚀 Setup Instructions

### Step 1: Install Dependencies

```bash
cd data-import
npm install
```

### Step 2: Configure Firebase

1. Buka Firebase Console (https://console.firebase.google.com)
2. Pilih project Anda
3. Go to **Project Settings** > **Service Accounts**
4. Click **Generate New Private Key**
5. Download the JSON file

### Step 3: Setup Environment Variables

1. Copy file `.env.example` menjadi `.env`:

   ```bash
   cp .env.example .env
   ```

2. Edit file `.env` dan isi dengan data dari service account JSON:
   ```env
   FIREBASE_PROJECT_ID=your-project-id
   FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project-id.iam.gserviceaccount.com
   FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_HERE\n-----END PRIVATE KEY-----\n"
   FIREBASE_DATABASE_URL=https://your-project-id-default-rtdb.firebaseio.com/
   ```

### Step 4: Run Import

```bash
# Import semua data (users + chats)
npm start
# atau
node import.js

# Import hanya users
npm run import-users

# Import hanya chats (jalankan setelah import users)
npm run import-chats
```

## 📊 Sample Data

### Users (8 users)

- john.doe@example.com (password: password123)
- jane.smith@example.com (password: password123)
- mike.johnson@example.com (password: password123)
- sarah.wilson@example.com (password: password123)
- david.brown@example.com (password: password123)
- lisa.garcia@example.com (password: password123)
- robert.miller@example.com (password: password123)
- emily.davis@example.com (password: password123)

### Chats

- 5 conversation threads dengan multiple messages
- Messages dengan timestamp yang realistis
- Berbagai jenis status dan conversation flows

## 🔧 Available Commands

```bash
# Full import
node import.js

# Clear existing data
node import.js --clear

# Show help
node import.js --help

# Individual imports
npm run import-users
npm run import-chats
```

## 📝 Generated Files

- `user-mapping.json` - Mapping email ke Firebase UID (dibuat otomatis)

## ⚠️ Important Notes

1. **Password**: Semua user dibuat dengan password `password123`
2. **Email Verification**: Users dibuat tanpa email verification
3. **Data Clearing**: Command `--clear` hanya menghapus data di database, bukan Auth users
4. **Rate Limits**: Script ini menangani rate limits Firebase secara otomatis

## 🔐 Security

- Jangan commit file `.env` ke version control
- File `.env` sudah ditambahkan ke `.gitignore`
- Service account key harus dijaga kerahasiaan

## 🐛 Troubleshooting

### Error: "Service account key required"

- Pastikan file `.env` sudah dikonfigurasi dengan benar
- Pastikan private key diformat dengan benar (dengan \\n untuk newlines)

### Error: "Database permission denied"

- Pastikan Realtime Database rules mengizinkan write access
- Untuk testing, bisa gunakan rules: `{"rules": {".read": true, ".write": true}}`

### Error: "User already exists"

- Script akan skip user yang sudah ada dan melanjutkan import
- Gunakan `--clear` untuk menghapus data existing

## 📱 Testing

Setelah import berhasil, Anda bisa login ke aplikasi Android menggunakan:

- Email: salah satu dari 8 email di atas
- Password: `password123`

Anda akan melihat:

- ✅ Daftar users di halaman "Select Contact"
- ✅ Conversations yang sudah ada di tab "Chats"
- ✅ Messages history saat membuka chat
- ✅ Real-time messaging functionality
