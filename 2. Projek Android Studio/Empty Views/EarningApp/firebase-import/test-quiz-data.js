const admin = require('firebase-admin');

// Initialize Firebase Admin
const serviceAccount = require('./service-account-key.json');

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
}

const firestore = admin.firestore();

async function testQuizData() {
  console.log('🔍 Testing Quiz Data in Firebase...\n');

  try {
    // Test 1: Check subjects
    console.log('📚 Testing Subjects:');
    const subjectsSnapshot = await firestore.collection('subjects').where('isActive', '==', true).get();

    console.log(`✅ Found ${subjectsSnapshot.size} active subjects:`);
    subjectsSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`   - ${data.name} (${doc.id}) - ${data.totalQuestions} questions`);
    });

    // Test 2: Check questions
    console.log('\n❓ Testing Questions:');
    const questionsSnapshot = await firestore.collection('questions').where('isActive', '==', true).get();

    console.log(`✅ Found ${questionsSnapshot.size} active questions`);

    // Test 3: Questions by subject
    console.log('\n📊 Questions by Subject:');
    const subjects = ['matematika', 'fisika', 'kimia', 'biologi'];

    for (const subjectId of subjects) {
      const subjectQuestions = await firestore.collection('questions').where('subjectId', '==', subjectId).where('isActive', '==', true).get();

      console.log(`   ${subjectId}: ${subjectQuestions.size} questions`);

      // Count by difficulty
      const difficulties = { easy: 0, medium: 0, hard: 0 };
      subjectQuestions.forEach((doc) => {
        const difficulty = doc.data().difficulty;
        if (difficulties[difficulty] !== undefined) {
          difficulties[difficulty]++;
        }
      });

      console.log(`     Easy: ${difficulties.easy}, Medium: ${difficulties.medium}, Hard: ${difficulties.hard}`);
    }

    // Test 4: Sample question data
    console.log('\n🔍 Sample Question Data:');
    const sampleQuestion = await firestore.collection('questions').where('subjectId', '==', 'matematika').where('difficulty', '==', 'easy').limit(1).get();

    if (!sampleQuestion.empty) {
      const questionData = sampleQuestion.docs[0].data();
      console.log('   Sample Question:');
      console.log(`   - ID: ${sampleQuestion.docs[0].id}`);
      console.log(`   - Question: ${questionData.question}`);
      console.log(`   - Options: [${questionData.options.join(', ')}]`);
      console.log(`   - Correct Answer: ${questionData.correctAnswer}`);
      console.log(`   - Difficulty: ${questionData.difficulty}`);
      console.log(`   - Subject: ${questionData.subjectId}`);
    }

    console.log('\n🎉 Quiz data test completed successfully!');
    return true;
  } catch (error) {
    console.error('❌ Error testing quiz data:', error);
    return false;
  }
}

// Run the test
testQuizData()
  .then((success) => {
    if (success) {
      console.log('\n✅ All quiz data tests passed!');
      console.log('🚀 Your app can now load subjects and questions from Firebase!');
    } else {
      console.log('\n❌ Some tests failed. Please check your Firebase setup.');
    }
    process.exit(success ? 0 : 1);
  })
  .catch((error) => {
    console.error('💥 Test execution failed:', error);
    process.exit(1);
  });
