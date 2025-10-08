// User Achievement Data
const achievements = [
  {
    id: 'first_quiz',
    title: 'Quiz Pertama',
    description: 'Selesaikan quiz pertama Anda',
    iconUrl: '',
    type: 'quiz_completion',
    requirement: 1,
    reward: 50, // coins
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'score_100',
    title: 'Pencetak Skor',
    description: 'Raih skor 100 dalam satu quiz',
    iconUrl: '',
    type: 'score',
    requirement: 100,
    reward: 100,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'streak_5',
    title: 'Beruntun 5',
    description: 'Jawab 5 soal berturut-turut dengan benar',
    iconUrl: '',
    type: 'streak',
    requirement: 5,
    reward: 75,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'math_master',
    title: 'Master Matematika',
    description: 'Selesaikan 10 quiz matematika',
    iconUrl: '',
    type: 'subject_master',
    requirement: 10,
    reward: 200,
    isActive: true,
    subjectId: 'matematika',
    createdAt: Date.now(),
  },
  {
    id: 'speed_demon',
    title: 'Kilat',
    description: 'Selesaikan quiz dalam waktu kurang dari 5 menit',
    iconUrl: '',
    type: 'speed',
    requirement: 300, // 5 minutes in seconds
    reward: 150,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'daily_learner',
    title: 'Pelajar Harian',
    description: 'Login dan belajar selama 7 hari berturut-turut',
    iconUrl: '',
    type: 'daily_streak',
    requirement: 7,
    reward: 300,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'all_subjects',
    title: 'Penjelajah Ilmu',
    description: 'Selesaikan minimal 1 quiz di semua mata pelajaran',
    iconUrl: '',
    type: 'subject_explorer',
    requirement: 8, // number of subjects
    reward: 500,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    id: 'perfect_score',
    title: 'Sempurna',
    description: 'Raih skor 100% dalam satu quiz',
    iconUrl: '',
    type: 'perfect',
    requirement: 100, // percentage
    reward: 250,
    isActive: true,
    createdAt: Date.now(),
  },
];

// Sample User Data for testing (optional)
const sampleUsers = [
  {
    id: 'user_sample_1',
    email: 'student1@example.com',
    name: 'Budi Santoso',
    profileImageUrl: '',
    totalScore: 850,
    level: 3,
    coins: 420,
    subjectProgress: {
      matematika: {
        subjectId: 'matematika',
        totalQuestions: 15,
        correctAnswers: 12,
        difficultyProgress: {
          easy: {
            difficulty: 'easy',
            totalQuestions: 8,
            correctAnswers: 7,
            averageTime: 25.5,
            streak: 3,
            maxStreak: 5,
          },
          medium: {
            difficulty: 'medium',
            totalQuestions: 5,
            correctAnswers: 4,
            averageTime: 45.2,
            streak: 2,
            maxStreak: 4,
          },
          hard: {
            difficulty: 'hard',
            totalQuestions: 2,
            correctAnswers: 1,
            averageTime: 85.0,
            streak: 0,
            maxStreak: 1,
          },
        },
        lastAccessedAt: Date.now() - 86400000, // 1 day ago
      },
      fisika: {
        subjectId: 'fisika',
        totalQuestions: 8,
        correctAnswers: 6,
        difficultyProgress: {
          easy: {
            difficulty: 'easy',
            totalQuestions: 5,
            correctAnswers: 4,
            averageTime: 28.0,
            streak: 1,
            maxStreak: 3,
          },
          medium: {
            difficulty: 'medium',
            totalQuestions: 3,
            correctAnswers: 2,
            averageTime: 52.5,
            streak: 0,
            maxStreak: 2,
          },
        },
        lastAccessedAt: Date.now() - 172800000, // 2 days ago
      },
    },
    dailyStreak: 3,
    maxDailyStreak: 7,
    lastLoginAt: Date.now() - 3600000, // 1 hour ago
    createdAt: Date.now() - 2592000000, // 30 days ago
    achievements: ['first_quiz', 'score_100'],
    statistics: {
      totalQuizzes: 12,
      totalTimeSpent: 2840, // seconds
      averageScore: 75.5,
      favoriteSubject: 'matematika',
      strongestDifficulty: 'easy',
      weeklyGoal: 10,
      weeklyProgress: 7,
    },
  },
  {
    id: 'user_sample_2',
    email: 'student2@example.com',
    name: 'Siti Rahayu',
    profileImageUrl: '',
    totalScore: 1250,
    level: 4,
    coins: 680,
    subjectProgress: {
      matematika: {
        subjectId: 'matematika',
        totalQuestions: 20,
        correctAnswers: 18,
        difficultyProgress: {
          easy: {
            difficulty: 'easy',
            totalQuestions: 10,
            correctAnswers: 10,
            averageTime: 22.0,
            streak: 5,
            maxStreak: 10,
          },
          medium: {
            difficulty: 'medium',
            totalQuestions: 8,
            correctAnswers: 7,
            averageTime: 38.5,
            streak: 3,
            maxStreak: 7,
          },
          hard: {
            difficulty: 'hard',
            totalQuestions: 2,
            correctAnswers: 1,
            averageTime: 95.0,
            streak: 0,
            maxStreak: 1,
          },
        },
        lastAccessedAt: Date.now() - 7200000, // 2 hours ago
      },
      bahasa_indonesia: {
        subjectId: 'bahasa_indonesia',
        totalQuestions: 12,
        correctAnswers: 11,
        difficultyProgress: {
          easy: {
            difficulty: 'easy',
            totalQuestions: 8,
            correctAnswers: 8,
            averageTime: 20.0,
            streak: 4,
            maxStreak: 8,
          },
          medium: {
            difficulty: 'medium',
            totalQuestions: 4,
            correctAnswers: 3,
            averageTime: 35.0,
            streak: 1,
            maxStreak: 3,
          },
        },
        lastAccessedAt: Date.now() - 86400000, // 1 day ago
      },
    },
    dailyStreak: 5,
    maxDailyStreak: 12,
    lastLoginAt: Date.now() - 1800000, // 30 minutes ago
    createdAt: Date.now() - 1814400000, // 21 days ago
    achievements: ['first_quiz', 'score_100', 'streak_5', 'daily_learner'],
    statistics: {
      totalQuizzes: 18,
      totalTimeSpent: 4320, // seconds
      averageScore: 82.3,
      favoriteSubject: 'matematika',
      strongestDifficulty: 'easy',
      weeklyGoal: 15,
      weeklyProgress: 12,
    },
  },
];

// Leaderboard Data
const leaderboardEntries = [
  {
    id: 'leaderboard_1',
    userId: 'user_sample_2',
    userName: 'Siti Rahayu',
    userImageUrl: '',
    totalScore: 1250,
    level: 4,
    rank: 1,
    subjectScores: {
      matematika: 680,
      bahasa_indonesia: 420,
      fisika: 150,
    },
    lastUpdated: Date.now(),
  },
  {
    id: 'leaderboard_2',
    userId: 'user_sample_1',
    userName: 'Budi Santoso',
    userImageUrl: '',
    totalScore: 850,
    level: 3,
    rank: 2,
    subjectScores: {
      matematika: 520,
      fisika: 280,
      kimia: 50,
    },
    lastUpdated: Date.now(),
  },
];

// User Settings Template
const defaultUserSettings = {
  notifications: {
    dailyReminder: true,
    achievementAlerts: true,
    weeklyReport: true,
    sound: true,
  },
  preferences: {
    language: 'id',
    theme: 'light',
    autoAdvance: false,
    showExplanations: true,
    timeLimit: true,
  },
  privacy: {
    showInLeaderboard: true,
    shareProgress: false,
    dataCollection: true,
  },
};

module.exports = {
  achievements,
  sampleUsers,
  leaderboardEntries,
  defaultUserSettings,
};
