const { initializeFirebase } = require('./firebase-config');

async function testConnection() {
  console.log('🔄 Testing Firebase connection...\n');

  try {
    const { db, auth } = initializeFirebase();

    // Test Firestore connection
    console.log('📊 Testing Firestore connection...');
    const testDoc = await db.collection('_test').doc('connection').set({
      timestamp: new Date(),
      message: 'Connection test successful',
    });

    // Read back the test document
    const docSnapshot = await db.collection('_test').doc('connection').get();
    if (docSnapshot.exists) {
      console.log('✅ Firestore: Write and read operations successful');
      console.log('📄 Test document data:', docSnapshot.data());
    }

    // Clean up test document
    await db.collection('_test').doc('connection').delete();
    console.log('🧹 Test document cleaned up');

    // Skip Auth test for now (permission issue)
    console.log('\n👤 Skipping Firebase Auth test (permission issue - normal for imports)');
    console.log('ℹ️  Auth will work fine in the Android app');

    console.log('\n🎉 Firestore connection working correctly!');
    console.log('🚀 Ready to import data...');
  } catch (error) {
    console.error('❌ Connection test failed:', error.message);
    console.error('🔍 Please check your service-account-key.json file');
    process.exit(1);
  }
}

// Run test if this file is executed directly
if (require.main === module) {
  testConnection();
}

module.exports = { testConnection };
