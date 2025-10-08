const { initializeFirebase } = require('./firebase-config');

// Sample users data (for Firestore only, not Auth)
const sampleUsers = [
  {
    name: 'John Doe',
    email: 'john.doe@example.com',
    phone: '+62812345678901',
    address: 'Jl. Sudirman No. 123, Jakarta Pusat, DKI Jakarta',
    favorites: [],
    createdAt: Date.now(),
  },
  {
    name: 'Jane Smith',
    email: 'jane.smith@example.com',
    phone: '+62812345678902',
    address: 'Jl. Gatot Subroto No. 456, Jakarta Selatan, DKI Jakarta',
    favorites: [],
    createdAt: Date.now(),
  },
  {
    name: 'Ahmad Rahman',
    email: 'ahmad.rahman@example.com',
    phone: '+62812345678903',
    address: 'Jl. Asia Afrika No. 789, Bandung, Jawa Barat',
    favorites: [],
    createdAt: Date.now(),
  },
];

async function importUsers() {
  console.log('🔄 Starting user import process...\n');
  console.log('⚠️  Note: This only creates user documents in Firestore, not Firebase Auth users');

  try {
    const { db } = initializeFirebase();

    console.log(`👥 Importing ${sampleUsers.length} user documents...`);

    const batch = db.batch();

    sampleUsers.forEach((user, index) => {
      // Use a generated ID for the user document
      const docRef = db.collection('users').doc();
      const userWithId = { ...user, id: docRef.id };
      batch.set(docRef, userWithId);
      console.log(`➕ Added user ${index + 1}: ${user.name} (${user.email})`);
    });

    // Commit the batch
    await batch.commit();

    console.log('\n✅ User document import completed successfully!');
    console.log(`📊 Total user documents imported: ${sampleUsers.length}`);

    // Verify import
    const usersSnapshot = await db.collection('users').get();
    console.log(`🔍 Verification: ${usersSnapshot.size} user documents in database`);

    console.log('\n📝 Important Notes:');
    console.log('• These are only Firestore documents, not actual Firebase Auth users');
    console.log('• Real users will be created when they sign up through the app');
    console.log('• User documents will be linked to Auth UIDs when users authenticate');
  } catch (error) {
    console.error('❌ Error importing user documents:', error.message);
    process.exit(1);
  }
}

// Run import if this file is executed directly
if (require.main === module) {
  importUsers();
}

module.exports = { importUsers, sampleUsers };
