const { db } = require('./firebase-config');

async function verifyData() {
  console.log('🔍 Verifying Firebase data...\n');

  try {
    // Check subjects
    const subjectsSnapshot = await db.collection('subjects').get();
    console.log(`📚 Subjects: ${subjectsSnapshot.size} documents found`);
    subjectsSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`   - ${data.name} (${doc.id})`);
    });

    // Check questions
    const questionsSnapshot = await db.collection('questions').get();
    console.log(`\n❓ Questions: ${questionsSnapshot.size} documents found`);

    // Group questions by subject and difficulty
    const questionsBySubject = {};
    questionsSnapshot.forEach((doc) => {
      const data = doc.data();
      if (!questionsBySubject[data.subjectId]) {
        questionsBySubject[data.subjectId] = { easy: 0, medium: 0, hard: 0 };
      }
      questionsBySubject[data.subjectId][data.difficulty]++;
    });

    Object.keys(questionsBySubject).forEach((subjectId) => {
      const counts = questionsBySubject[subjectId];
      console.log(`   - ${subjectId}: Easy(${counts.easy}) Medium(${counts.medium}) Hard(${counts.hard})`);
    });

    // Check users
    const usersSnapshot = await db.collection('users').get();
    console.log(`\n👥 Users: ${usersSnapshot.size} documents found`);

    // Check achievements
    const achievementsSnapshot = await db.collection('achievements').get();
    console.log(`🏆 Achievements: ${achievementsSnapshot.size} documents found`);

    // Check app settings
    const settingsSnapshot = await db.collection('app_settings').get();
    console.log(`⚙️ App Settings: ${settingsSnapshot.size} documents found`);

    console.log('\n✅ Data verification completed successfully!');
    console.log('🚀 Your Firebase database is ready for the EarningApp!');
  } catch (error) {
    console.error('❌ Error verifying data:', error);
  }
}

verifyData()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error('💥 Verification failed:', error);
    process.exit(1);
  });
