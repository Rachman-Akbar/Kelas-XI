const importSubjects = require('./import-subjects');
const importQuestions = require('./import-questions');
const { importUserData } = require('./import-users');

async function importAllData() {
  console.log('🚀 Starting complete data import for EarningApp...\n');

  try {
    // Import subjects first
    console.log('📚 Step 1: Importing subjects...');
    await importSubjects();

    console.log('\n❓ Step 2: Importing questions...');
    await importQuestions();

    console.log('\n👥 Step 3: Importing user data (achievements, settings, sample users)...');
    await importUserData();

    console.log('\n🎉 All data imported successfully!');
    console.log('✅ Your EarningApp database is now ready to use.');
  } catch (error) {
    console.error('💥 Import failed:', error);
    process.exit(1);
  }
}

// Run the complete import
importAllData()
  .then(() => {
    console.log('\n🚀 Ready to launch your app!');
    process.exit(0);
  })
  .catch((error) => {
    console.error('💥 Complete import failed:', error);
    process.exit(1);
  });
