const { db } = require('./firebase-config');

// Simple test user data
const testUsers = [
  {
    id: 'test_user_1',
    name: 'Test User 1',
    email: 'test1@example.com',
    coins: 100,
    score: 0,
    level: 1,
    rank: 'Bronze',
    achievements: [],
    quizHistory: [],
    lastLoginDate: Date.now(),
    registrationDate: Date.now(),
    settings: {
      soundEnabled: true,
      notificationsEnabled: true,
      theme: 'light',
    },
  },
  {
    id: 'test_user_2',
    name: 'Test User 2',
    email: 'test2@example.com',
    coins: 250,
    score: 150,
    level: 2,
    rank: 'Silver',
    achievements: [],
    quizHistory: [],
    lastLoginDate: Date.now(),
    registrationDate: Date.now(),
    settings: {
      soundEnabled: true,
      notificationsEnabled: true,
      theme: 'light',
    },
  },
];

async function importSimpleUsers() {
  console.log('👥 Importing simple test users...');

  try {
    const batch = db.batch();

    testUsers.forEach((user) => {
      const userRef = db.collection('users').doc(user.id);
      batch.set(userRef, user);
    });

    await batch.commit();
    console.log(`✅ Successfully imported ${testUsers.length} test users`);
  } catch (error) {
    console.error('❌ Error importing users:', error);
  }
}

importSimpleUsers()
  .then(() => {
    console.log('✅ User import completed');
    process.exit(0);
  })
  .catch((error) => {
    console.error('💥 User import failed:', error);
    process.exit(1);
  });
