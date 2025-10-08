const { initializeFirebase } = require('./firebase-config');

const testConnection = async () => {
  console.log('🔍 Testing Firebase connection...');

  try {
    const db = initializeFirebase();

    // Test write operation
    console.log('📝 Testing write operation...');
    const testDoc = await db.collection('test').add({
      message: 'Hello from Node.js!',
      timestamp: Date.now(),
      testConnection: true,
    });

    console.log('✅ Write test successful. Document ID:', testDoc.id);

    // Test read operation
    console.log('📖 Testing read operation...');
    const snapshot = await db.collection('test').limit(1).get();

    if (!snapshot.empty) {
      console.log('✅ Read test successful. Found', snapshot.size, 'document(s)');
      snapshot.forEach((doc) => {
        console.log('📄 Sample document data:', doc.data());
      });
    } else {
      console.log('⚠️ No documents found in test collection');
    }

    // Clean up test document
    console.log('🧹 Cleaning up test document...');
    await testDoc.delete();
    console.log('✅ Test document deleted');

    console.log('\n🎉 Firebase connection test completed successfully!');
    console.log('Your Firebase configuration is working properly.');
  } catch (error) {
    console.error('❌ Firebase connection test failed:', error.message);
    console.error('\n🔧 Please check:');
    console.error('1. Your .env file configuration');
    console.error('2. Firebase project settings');
    console.error('3. Service account key permissions');
    console.error('4. Network connectivity');
  }

  process.exit(0);
};

// Run test if this file is executed directly
if (require.main === module) {
  testConnection();
}

module.exports = { testConnection };
