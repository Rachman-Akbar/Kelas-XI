const { importUsers } = require('./import-users');
const { importChats } = require('./import-chats');

async function runFullImport() {
  console.log('🚀 Starting full data import for WhatsApp Clone...\n');

  try {
    // Step 1: Import Users
    console.log('='.repeat(50));
    console.log('STEP 1: IMPORTING USERS');
    console.log('='.repeat(50));
    await importUsers();

    // Wait a bit for Firebase to process
    console.log('\n⏳ Waiting 3 seconds before importing chats...');
    await new Promise((resolve) => setTimeout(resolve, 3000));

    // Step 2: Import Chats
    console.log('\n' + '='.repeat(50));
    console.log('STEP 2: IMPORTING CHATS');
    console.log('='.repeat(50));
    await importChats();

    console.log('\n' + '🎉'.repeat(20));
    console.log('🎉 FULL IMPORT COMPLETED SUCCESSFULLY! 🎉');
    console.log('🎉'.repeat(20));
    console.log('\n📱 Your WhatsApp Clone is now ready with sample data!');
    console.log('👥 Users have been created with the following credentials:');
    console.log('   📧 Email: [user-email] | 🔑 Password: password123');
    console.log('\n🔐 You can now login to the app using any of the imported user accounts.');
  } catch (error) {
    console.error('❌ Full import failed:', error);
    throw error;
  }
}

// Clear existing data function
async function clearAllData() {
  const { db, auth } = require('./firebase-config');

  console.log('🗑️ Clearing existing data...');

  try {
    // Clear database
    await db.ref('users').remove();
    await db.ref('chats').remove();
    console.log('✅ Database cleared');

    // Note: We don't clear Auth users as it requires more complex operations
    console.log('⚠️ Note: Auth users are not cleared. You may need to delete them manually from Firebase Console.');
  } catch (error) {
    console.error('❌ Error clearing data:', error);
  }
}

// Command line arguments handling
const args = process.argv.slice(2);

if (args.includes('--clear')) {
  clearAllData()
    .then(() => {
      console.log('✅ Data clearing completed.');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Data clearing failed:', error);
      process.exit(1);
    });
} else if (args.includes('--help')) {
  console.log(`
WhatsApp Clone Data Import Tool
==============================

Usage:
  node import.js              - Run full import (users + chats)
  node import.js --clear      - Clear existing data
  node import.js --help       - Show this help message
  
  npm run import-users        - Import only users
  npm run import-chats        - Import only chats
  
Environment Setup:
  1. Copy .env.example to .env
  2. Fill in your Firebase credentials
  3. Run the import script
  
`);
  process.exit(0);
} else {
  // Run full import by default
  runFullImport()
    .then(() => {
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import failed:', error);
      process.exit(1);
    });
}
