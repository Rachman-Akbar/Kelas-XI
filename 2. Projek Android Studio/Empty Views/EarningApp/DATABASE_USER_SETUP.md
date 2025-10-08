# Database User Setup untuk EarningApp

## ✅ Data User yang Telah Berhasil Diimpor

### 1. **Achievements (8 Pencapaian)**

- **Quiz Pertama**: Selesaikan quiz pertama (Reward: 50 coins)
- **Pencetak Skor**: Raih skor 100 dalam satu quiz (Reward: 100 coins)
- **Beruntun 5**: Jawab 5 soal berturut-turut dengan benar (Reward: 75 coins)
- **Master Matematika**: Selesaikan 10 quiz matematika (Reward: 200 coins)
- **Kilat**: Selesaikan quiz dalam waktu kurang dari 5 menit (Reward: 150 coins)
- **Pelajar Harian**: Login dan belajar selama 7 hari berturut-turut (Reward: 300 coins)
- **Penjelajah Ilmu**: Selesaikan minimal 1 quiz di semua mata pelajaran (Reward: 500 coins)
- **Sempurna**: Raih skor 100% dalam satu quiz (Reward: 250 coins)

### 2. **Sample Users (2 User Contoh)**

- **Budi Santoso** (student1@example.com)
  - Level: 3
  - Total Score: 850 points
  - Coins: 420
  - Daily Streak: 3 hari
  - Achievements: Quiz Pertama, Pencetak Skor
- **Siti Rahayu** (student2@example.com)
  - Level: 4
  - Total Score: 1,250 points
  - Coins: 680
  - Daily Streak: 5 hari
  - Achievements: Quiz Pertama, Pencetak Skor, Beruntun 5, Pelajar Harian

### 3. **User Settings Template**

```javascript
- Notifications: Daily reminder, Achievement alerts, Weekly report, Sound
- Preferences: Bahasa Indonesia, Light theme, Auto advance, Show explanations, Time limit
- Privacy: Show in leaderboard, Share progress, Data collection
```

### 4. **Leaderboard System**

- Ranking berdasarkan total score
- Top 2 users:
  1. Siti Rahayu - 1,250 points
  2. Budi Santoso - 850 points

## 🔧 Struktur Database Firestore

### Collections yang Telah Dibuat:

1. **`users`** - Profil lengkap pengguna
2. **`achievements`** - Daftar pencapaian yang bisa diraih
3. **`user_settings`** - Pengaturan individual user
4. **`leaderboard`** - Papan peringkat
5. **`app_settings`** - Pengaturan global aplikasi

## 🚀 Fitur Authentication yang Tersedia

### Registration (Daftar):

- Email & Password validation
- Otomatis create user profile dengan:
  - Starting coins: 100
  - Level: 1
  - Default settings
  - Empty achievements
  - Progress tracking siap digunakan

### Login:

- Email & Password authentication
- Otomatis update daily streak
- Track last login time
- Load user progress

### User Management:

- Update score dan progress
- Achievement system
- Level progression (1-10)
- Coins reward system
- Daily streak tracking

## 📱 Android Integration

### Repository Classes:

- **`AuthRepository`** - Handle authentication
- **`UserRepository`** - Manage user data dan achievements

### Key Features:

- Automatic user profile creation on registration
- Daily streak calculation
- Achievement checking and awarding
- Score and level progression
- Leaderboard integration

## 🎯 Sistem Poin dan Level

### Level Progression:

- Level 1: 0-99 points
- Level 2: 100-299 points
- Level 3: 300-599 points
- Level 4: 600-999 points
- Level 5: 1000-1499 points
- Level 6: 1500-2099 points
- Level 7: 2100-2799 points
- Level 8: 2800-3599 points
- Level 9: 3600-4499 points
- Level 10: 4500+ points

### Reward System:

- 1 coin per 10 points earned
- Achievement rewards: 50-500 coins
- Starting coins: 100

## ✅ Status Database

**Database siap digunakan untuk:**

- ✅ User registration dan login
- ✅ Score tracking dan progress
- ✅ Achievement system
- ✅ Daily streak tracking
- ✅ Leaderboard functionality
- ✅ User settings management

**Total Data Imported:**

- 8 Subjects ✅
- 26 Questions ✅
- 8 Achievements ✅
- 2 Sample Users ✅
- User Settings Template ✅
- Leaderboard System ✅

🎉 **Database EarningApp sudah lengkap dan siap untuk testing!**
