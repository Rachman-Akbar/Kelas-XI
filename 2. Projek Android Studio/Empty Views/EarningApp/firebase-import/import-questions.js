const { db } = require('./firebase-config');
const questions = require('./data/enhanced_questions');

async function importQuestions() {
  console.log('🚀 Starting questions import...');

  try {
    // Import in batches of 500 (Firestore batch limit)
    const batchSize = 500;
    const batches = [];

    for (let i = 0; i < questions.length; i += batchSize) {
      const batch = db.batch();
      const batchQuestions = questions.slice(i, i + batchSize);

      batchQuestions.forEach((question) => {
        const docRef = db.collection('questions').doc(question.id);
        batch.set(docRef, question);
      });

      batches.push(batch);
    }

    // Execute all batches
    for (let i = 0; i < batches.length; i++) {
      await batches[i].commit();
      console.log(`✅ Batch ${i + 1}/${batches.length} committed`);
    }

    console.log('✅ Successfully imported', questions.length, 'questions');

    // Show summary by subject
    const summary = {};
    questions.forEach((question) => {
      if (!summary[question.subjectId]) {
        summary[question.subjectId] = { easy: 0, medium: 0, hard: 0 };
      }
      summary[question.subjectId][question.difficulty]++;
    });

    console.log('\n📊 Questions summary by subject:');
    Object.entries(summary).forEach(([subjectId, counts]) => {
      const total = counts.easy + counts.medium + counts.hard;
      console.log(`   ${subjectId}: ${total} total (Easy: ${counts.easy}, Medium: ${counts.medium}, Hard: ${counts.hard})`);
    });
  } catch (error) {
    console.error('❌ Error importing questions:', error);
  }
}

// Run if called directly
if (require.main === module) {
  importQuestions()
    .then(() => {
      console.log('🎉 Questions import completed!');
      process.exit(0);
    })
    .catch((error) => {
      console.error('💥 Import failed:', error);
      process.exit(1);
    });
}

module.exports = importQuestions;
