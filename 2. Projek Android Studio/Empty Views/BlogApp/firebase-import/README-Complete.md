# BlogApp Firebase Import Tool 🔥

Tool ini digunakan untuk mengimpor data sample lengkap ke Firestore database untuk aplikasi BlogApp.

## 🚀 Quick Start

### 1. Install Dependencies

```bash
cd firebase-import
npm install
```

### 2. Setup Service Account Key

- File `service-key-account.json` sudah tersedia ✅
- Firebase Admin SDK menggunakan file JSON ini langsung

### 3. Test Connection

```bash
npm run test
```

### 4. Import Complete Data

```bash
npm run import-all
```

## 🔧 Available Commands

| Command                    | Description                         |
| -------------------------- | ----------------------------------- |
| `npm run test`             | Test koneksi ke Firebase Firestore  |
| `npm run import`           | Import data dasar (users + blogs)   |
| `npm run import-all`       | **Import semua data lengkap** ⭐    |
| `npm run clear`            | Hapus semua data dari semua koleksi |
| `node clear-data.js blogs` | Hapus data dari koleksi tertentu    |

## 📊 Data Collections yang Diimpor

### 👥 Users Collection (5 users)

```javascript
{
  id: 'user-demo-1',
  email: 'demo@blogapp.com',
  displayName: 'Demo User',
  bio: 'Seorang blogger pemula...',
  location: 'Jakarta, Indonesia',
  joinedDate: timestamp,
  totalBlogs: 0
}
```

### 📁 Categories Collection (5 categories)

- 💻 **Technology** - Programming & Development
- 🌟 **Lifestyle** - Tips hidup sehat & produktif
- 🍜 **Food & Travel** - Kuliner & wisata
- 💼 **Business** - Entrepreneurship & karir
- 🚀 **Personal Development** - Motivasi & pengembangan diri

### 🏷️ Tags Collection (10 tags)

`android`, `kotlin`, `firebase`, `programming`, `tutorial`, `food`, `travel`, `lifestyle`, `business`, `inspiration`

### 📝 Blogs Collection (12 blog posts)

- **"Welcome to BlogApp"** - Pengenalan aplikasi
- **"Tips Menulis Blog yang Menarik"** - Tutorial blogging
- **"Teknologi Mobile Development di 2025"** - Tech trends
- **"Pentingnya Firebase dalam Aplikasi Modern"** - Firebase tutorial
- **"Belajar Kotlin untuk Pemula"** - Programming guide
- **"UI/UX Design Trends 2025"** - Design insights
- **"Resep Nasi Gudeg Jogja Asli"** - Food recipe
- **"Cerita Inspiratif: Dari Nol Menjadi Entrepreneur"** - Business story
- **"Setup Development Environment untuk Android"** - Tech tutorial
- **"Tips Traveling Budget ke Bali"** - Travel guide
- **"Manfaat Meditasi untuk Produktivitas"** - Lifestyle tips
- **"Best Practices Firebase Security Rules"** - Security guide

### 💬 Comments Collection (5 comments)

Komentar realistis pada blog posts dengan approval status.

## 🎯 Import Process Flow

1. **Users** → Create demo users first
2. **Categories** → Setup blog categories
3. **Tags** → Create tag system
4. **Blogs** → Import all blog posts with references
5. **Comments** → Add user interactions

## 📁 Project Structure

```
firebase-import/
├── 📦 package.json                 # Dependencies & scripts
├── 🔑 service-key-account.json    # Firebase service key
├── ⚙️ firebase-config.js          # Firebase Admin config
├── 📊 sample-data-complete.js     # Complete sample data
├── 🚀 import-complete-data.js     # Main import script
├── 🧹 clear-data.js              # Data cleaner
├── 🧪 test-connection.js         # Connection tester
└── 📖 README.md                  # This documentation
```

## 🔐 Security Setup

### Firebase Console Setup Required:

1. **Enable Firestore**: https://console.firebase.google.com
2. **Set Security Rules** (untuk testing):

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

## 📱 Usage dengan BlogApp Android

Setelah import berhasil:

1. ✅ **Build & Run** aplikasi Android BlogApp
2. ✅ **Register/Login** dengan email baru atau gunakan demo users:
   - `demo@blogapp.com`
   - `blogger@example.com`
   - `developer@tech.com`
3. ✅ **Home Screen** akan menampilkan 12 blog posts
4. ✅ **Add Post** untuk menulis blog baru
5. ✅ **Real-time sync** dengan Firestore

## 🐛 Troubleshooting

### "Cloud Firestore API has not been used"

- Buka link: https://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=blogapp-80a09
- Klik **"Enable"**
- Tunggu beberapa menit

### "Permission denied"

- Check Firestore Security Rules di Firebase Console
- Pastikan rules dalam test mode atau allow read/write

### Import berhasil tapi app tidak ada data

- Check network connection di emulator/device
- Lihat Logcat untuk error Firebase
- Pastikan google-services.json sudah benar di folder app/

## 🎉 Success Indicators

Setelah `npm run import-all` berhasil:

```
🎯 FINAL IMPORT SUMMARY
======================
👥 Users: ✅ 5/5
📁 Categories: ✅ 5/5
🏷️  Tags: ✅ 10/10
📝 Blogs: ✅ 12/12
💬 Comments: ✅ 5/5

🎉 Total: 37/37 records imported successfully!

✨ All data imported successfully!
🔗 Check your Firebase Console: https://console.firebase.google.com
📱 Your BlogApp is now ready with sample data!
```

## 🔄 Data Management

### Reset & Reimport

```bash
npm run clear      # Clear all data
npm run import-all # Import fresh data
```

### Partial Management

```bash
node clear-data.js blogs    # Clear only blogs
node clear-data.js users    # Clear only users
```

---

**Happy Coding! 🚀**
