const firebaseConfig = require('./config/firebase');

async function testConnection() {
  console.log('🔍 Testing Firebase connection...');

  try {
    const db = firebaseConfig.getFirestore();
    const auth = firebaseConfig.getAuth();

    // Test Firestore connection
    console.log('📄 Testing Firestore connection...');
    const testDoc = await db.collection('test').doc('connection-test').set({
      timestamp: new Date(),
      message: 'Connection test successful',
    });
    console.log('✅ Firestore connection: OK');

    // Clean up test document
    await db.collection('test').doc('connection-test').delete();

    // Test Auth connection
    console.log('🔐 Testing Auth connection...');
    const users = await auth.listUsers(1);
    console.log('✅ Auth connection: OK');

    console.log('\n🎉 All connections successful!');
    console.log('You can now run the import scripts.');

    return true;
  } catch (error) {
    console.error('❌ Connection test failed:', error.message);

    if (error.message.includes('Could not load the default credentials')) {
      console.log('\n💡 Solution:');
      console.log('1. Make sure you have created a .env file');
      console.log('2. Check that all Firebase credentials are correct in .env');
      console.log('3. Verify the service account key is valid');
    }

    if (error.code === 'auth/project-not-found') {
      console.log('\n💡 Solution:');
      console.log('1. Check FIREBASE_PROJECT_ID in .env file');
      console.log('2. Make sure the Firebase project exists');
    }

    return false;
  }
}

// Run test if called directly
if (require.main === module) {
  testConnection()
    .then((success) => {
      process.exit(success ? 0 : 1);
    })
    .catch((error) => {
      console.error('❌ Test failed:', error);
      process.exit(1);
    });
}

module.exports = testConnection;
