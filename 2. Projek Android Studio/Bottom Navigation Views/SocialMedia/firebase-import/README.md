# Firebase Data Import untuk SocialMedia App

Script Node.js untuk import data dummy ke Firebase Firestore dan Authentication.

## 📋 Prerequisites

1. **Node.js** (versi 14 atau lebih baru)
2. **Firebase Project** yang sudah dikonfigurasi
3. **Service Account Key** dari Firebase Console

## 🚀 Setup

### 1. Install Dependencies

```bash
cd firebase-import
npm install
```

### 2. Setup Firebase Service Account

1. Buka [Firebase Console](https://console.firebase.google.com)
2. Pilih project Anda
3. Go to **Project Settings** > **Service Accounts**
4. Click **Generate New Private Key**
5. Download file JSON dan **rename menjadi `service-account-key.json`**
6. **Letakkan file `service-account-key.json` di folder `firebase-import`**

**✅ SUDAH SELESAI!** Script akan otomatis membaca file `service-account-key.json`

### 3. File Structure

Pastikan struktur folder seperti ini:

```
firebase-import/
├── service-account-key.json    ← File yang Anda download
├── package.json
├── config/
│   └── firebase.js
└── ... file lainnya
```

**TIDAK PERLU** setup environment variables lagi karena script langsung membaca file JSON.

## 📊 Data yang Akan Diimport

### 👥 Users (10 users)

- john_doe, jane_smith, mike_wilson, sarah_johnson, alex_brown
- emma_davis, david_garcia, lisa_martinez, chris_lee, rachel_taylor
- Setiap user memiliki profile lengkap dengan foto, bio, followers/following count

### 📸 Posts (~30 posts)

- 3 posts per user dengan gambar dari Unsplash
- Caption yang realistis
- Random likes dan comments count
- Tanggal random dalam 30 hari terakhir

### 📱 Stories (6 stories)

- Stories untuk 6 user pertama
- Mix antara image stories dan text stories
- Auto-expire dalam 24 jam
- Random viewers

## 🏃‍♂️ Cara Menjalankan

### Import Semua Data Sekaligus (Recommended)

```bash
npm run import-all
```

### Import Step by Step

```bash
# 1. Import Users terlebih dahulu
npm run import-users

# 2. Import Posts (membutuhkan users)
npm run import-posts

# 3. Import Stories (membutuhkan users)
npm run import-stories
```

## 📁 Output Files

Setelah import berhasil, akan dibuat file-file berikut:

- `imported-users.json` - List user yang berhasil diimport
- `imported-posts.json` - List post yang berhasil diimport
- `imported-stories.json` - List story yang berhasil diimport
- `import-summary.json` - Summary lengkap dari seluruh import

## 🔐 Sample Login Credentials

Setelah import berhasil, Anda bisa login dengan user berikut:

```
Email: john.doe@example.com
Password: password123

Email: jane.smith@example.com
Password: password123

Email: mike.wilson@example.com
Password: password123

// ... dan seterusnya (semua user menggunakan password: password123)
```

## 🛡️ Firebase Security Rules

Pastikan Firestore Security Rules sudah diatur dengan benar:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Posts collection
    match /posts/{postId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.userId;
    }

    // Stories collection
    match /stories/{storyId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.userId;
    }

    // Follows collection
    match /follows/{followId} {
      allow read, write: if request.auth != null;
    }

    // Blocks collection
    match /blocks/{blockId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🔍 Troubleshooting

### Error: "Project ID not found"

- Pastikan `FIREBASE_PROJECT_ID` di file `.env` sudah benar
- Check Firebase Console untuk memastikan project ID

### Error: "Permission denied"

- Pastikan Service Account Key sudah benar
- Check IAM permissions di Firebase Console
- Pastikan Service Account memiliki role "Firebase Admin SDK Administrator Service Agent"

### Error: "Email already exists"

- Script akan otomatis handle existing users
- User yang sudah ada akan di-update, bukan di-create ulang

### Error: "Network timeout"

- Script sudah include delay untuk menghindari rate limiting
- Jika masih timeout, coba kurangi batch size atau tambah delay

## 📈 Monitoring

Selama import berjalan, Anda akan melihat progress seperti ini:

```
🚀 Starting complete data import...

==========================================
STEP 1: IMPORTING USERS
==========================================
📝 Creating user 1/10: john_doe
✅ User created successfully: john_doe (abc123def456)
📝 Creating user 2/10: jane_smith
✅ User created successfully: jane_smith (def456ghi789)
...

==========================================
STEP 2: IMPORTING POSTS
==========================================
📝 Creating post 1/3 for user: john_doe
✅ Post created successfully: post123abc
...

==========================================
🎉 IMPORT COMPLETED SUCCESSFULLY!
==========================================
📊 SUMMARY:
   👥 Users: 10
   📸 Posts: 30
   📱 Stories: 6
   🗂️  Total records: 46
==========================================
```

## 🤝 Support

Jika mengalami masalah:

1. Check file `.env` sudah benar
2. Pastikan Firebase project sudah aktif
3. Pastikan internet connection stabil
4. Check Firebase Console untuk error logs

Happy importing! 🎉
