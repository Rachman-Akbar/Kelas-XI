const { db } = require('./firebase-config');
const subjects = require('./data/subjects');

async function importSubjects() {
  console.log('🚀 Starting subjects import...');

  try {
    const batch = db.batch();

    subjects.forEach((subject) => {
      const docRef = db.collection('subjects').doc(subject.id);
      batch.set(docRef, subject);
    });

    await batch.commit();

    console.log('✅ Successfully imported', subjects.length, 'subjects:');
    subjects.forEach((subject) => {
      console.log(`   - ${subject.name} (${subject.id})`);
    });
  } catch (error) {
    console.error('❌ Error importing subjects:', error);
  }
}

// Run if called directly
if (require.main === module) {
  importSubjects()
    .then(() => {
      console.log('🎉 Subjects import completed!');
      process.exit(0);
    })
    .catch((error) => {
      console.error('💥 Import failed:', error);
      process.exit(1);
    });
}

module.exports = importSubjects;
