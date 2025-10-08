const { db } = require('./firebase-config');

async function createTestQuizSession() {
  console.log('🎯 Creating test quiz session data...\n');

  try {
    // Create a sample quiz session for testing
    const quizSession = {
      id: 'test_session_1',
      userId: 'test_user_1',
      subjectId: 'matematika',
      difficulty: 'easy',
      questions: [
        {
          questionId: 'math_easy_1',
          userAnswer: 1,
          correctAnswer: 1,
          isCorrect: true,
          timeSpent: 15,
          points: 10,
        },
        {
          questionId: 'math_easy_2',
          userAnswer: 1,
          correctAnswer: 1,
          isCorrect: true,
          timeSpent: 12,
          points: 10,
        },
      ],
      score: 20,
      totalQuestions: 2,
      correctAnswers: 2,
      completedAt: Date.now(),
      coinsEarned: 20,
      status: 'completed',
    };

    await db.collection('quiz_sessions').doc(quizSession.id).set(quizSession);
    console.log('✅ Test quiz session created');

    // Create sample leaderboard entry
    const leaderboardEntry = {
      userId: 'test_user_1',
      userName: 'Test User 1',
      totalScore: 20,
      totalCoins: 120,
      completedQuizzes: 1,
      rank: 'Bronze',
      lastActivity: Date.now(),
    };

    await db.collection('leaderboard').doc('test_user_1').set(leaderboardEntry);
    console.log('✅ Test leaderboard entry created');

    console.log('\n🎉 Test data ready for EarningApp!');
    console.log('📱 Your app can now:');
    console.log('   - Load subjects from Firebase');
    console.log('   - Display quiz questions');
    console.log('   - Save quiz results');
    console.log('   - Track user progress');
    console.log('   - Show leaderboard');
  } catch (error) {
    console.error('❌ Error creating test data:', error);
  }
}

createTestQuizSession()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error('💥 Test data creation failed:', error);
    process.exit(1);
  });
