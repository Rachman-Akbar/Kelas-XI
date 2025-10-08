const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin SDK
const serviceAccount = require('./service-account-key.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: serviceAccount.project_id,
});

const db = admin.firestore();

// Import enhanced questions data
const enhancedQuestions = require('./data/enhanced_questions');

async function importEnhancedQuestions() {
  console.log('🚀 Starting import of enhanced questions...');
  console.log(`📊 Total questions to import: ${enhancedQuestions.length}`);

  try {
    // Batch write for better performance
    const batch = db.batch();

    enhancedQuestions.forEach((question) => {
      const questionRef = db.collection('questions').doc(question.id);
      batch.set(questionRef, question);
    });

    // Commit the batch
    await batch.commit();

    console.log('✅ Enhanced questions imported successfully!');
    console.log(`📈 Imported ${enhancedQuestions.length} questions total`);

    // Summary by subject and difficulty
    const summary = {};
    enhancedQuestions.forEach((q) => {
      if (!summary[q.subjectId]) {
        summary[q.subjectId] = { easy: 0, medium: 0, hard: 0 };
      }
      summary[q.subjectId][q.difficulty]++;
    });

    console.log('\n📋 Summary by subject and difficulty:');
    Object.entries(summary).forEach(([subject, counts]) => {
      console.log(`  ${subject}:`);
      console.log(`    Easy: ${counts.easy}`);
      console.log(`    Medium: ${counts.medium}`);
      console.log(`    Hard: ${counts.hard}`);
      console.log(`    Total: ${counts.easy + counts.medium + counts.hard}`);
    });
  } catch (error) {
    console.error('❌ Error importing enhanced questions:', error);
  }
}

// Run the import
importEnhancedQuestions()
  .then(() => {
    console.log('\n🎉 Enhanced questions import completed!');
    process.exit(0);
  })
  .catch((error) => {
    console.error('💥 Import failed:', error);
    process.exit(1);
  });
