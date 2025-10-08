# EarningApp Firebase Data Importer

Script Node.js untuk mengimpor data sample ke Firestore untuk aplikasi EarningApp.

## Setup

### 1. Install Dependencies

```bash
cd firebase-import
npm install
```

### 2. Setup Firebase Service Account

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Pilih project Anda
3. Klik ⚙️ **Settings** > **Project settings**
4. Ke tab **Service accounts**
5. Klik **Generate new private key**
6. Download file JSON

### 3. Setup Environment Variables

1. Copy file `.env.example` menjadi `.env`:

```bash
cp .env.example .env
```

2. Edit file `.env` dengan informasi dari service account JSON:

```env
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY_ID=your-private-key-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_HERE\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project-id.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your-client-id
FIREBASE_AUTH_URI=https://accounts.google.com/o/oauth2/auth
FIREBASE_TOKEN_URI=https://oauth2.googleapis.com/token
FIREBASE_AUTH_PROVIDER_X509_CERT_URL=https://www.googleapis.com/oauth2/v1/certs
FIREBASE_CLIENT_X509_CERT_URL=https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40your-project-id.iam.gserviceaccount.com
```

## Cara Menggunakan

### Import Semua Data (Recommended)

```bash
npm run import
```

### Import Terpisah

**Import Mata Pelajaran saja:**

```bash
npm run import-subjects
```

**Import Soal-soal saja:**

```bash
npm run import-questions
```

## Data yang Diimpor

### 📚 Mata Pelajaran (8 subjects):

- Matematika (50 soal)
- Fisika (40 soal)
- Kimia (35 soal)
- Biologi (45 soal)
- Bahasa Indonesia (30 soal)
- Bahasa Inggris (38 soal)
- Sejarah (25 soal)
- Geografi (28 soal)

### ❓ Soal-soal dengan 3 tingkat kesulitan:

- **Easy (Mudah)**: 10 poin per jawaban benar
- **Medium (Sedang)**: 15 poin per jawaban benar
- **Hard (Sulit)**: 20 poin per jawaban benar

## Struktur Database

### Collection: `subjects`

```json
{
  "id": "matematika",
  "name": "Matematika",
  "description": "Pelajari konsep matematika dasar hingga lanjutan",
  "color": "#4CAF50",
  "totalQuestions": 50,
  "isActive": true,
  "order": 1
}
```

### Collection: `questions`

```json
{
  "id": "math_easy_1",
  "subjectId": "matematika",
  "question": "Berapakah hasil dari 2 + 3?",
  "options": ["4", "5", "6", "7"],
  "correctAnswer": 1,
  "explanation": "2 + 3 = 5",
  "difficulty": "easy",
  "category": "Aritmatika",
  "timeLimit": 30,
  "points": 10
}
```

## Troubleshooting

### Error: "Permission denied"

- Pastikan service account memiliki role **Editor** atau **Owner**
- Cek kembali konfigurasi `.env`

### Error: "Project not found"

- Pastikan `FIREBASE_PROJECT_ID` sesuai dengan project ID di Firebase Console

### Error: "Invalid private key"

- Pastikan private key dikopi dengan benar, termasuk `\n` untuk line breaks
- Gunakan tanda kutip ganda di `.env`

## File Structure

```
firebase-import/
├── package.json
├── .env.example
├── .env (create this)
├── firebase-config.js
├── import-data.js (main script)
├── import-subjects.js
├── import-questions.js
└── data/
    ├── subjects.js
    └── questions.js
```

## Next Steps

Setelah data berhasil diimpor:

1. Jalankan aplikasi Android
2. Register/Login user baru
3. Data mata pelajaran dan soal akan muncul otomatis
4. User bisa mulai mengerjakan quiz berdasarkan tingkat kesulitan

---

**🚀 Happy Learning!**
