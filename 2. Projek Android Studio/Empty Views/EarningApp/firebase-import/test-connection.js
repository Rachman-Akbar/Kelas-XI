const { db } = require('./firebase-config');

async function testConnection() {
  console.log('🔧 Testing Firebase connection...');

  try {
    // Test write
    const testRef = db.collection('test').doc('connection');
    await testRef.set({
      message: 'Connection test successful',
      timestamp: new Date(),
      status: 'OK',
    });

    console.log('✅ Write test successful');

    // Test read
    const doc = await testRef.get();
    if (doc.exists) {
      console.log('✅ Read test successful');
      console.log('📄 Data:', doc.data());
    }

    // Clean up test document
    await testRef.delete();
    console.log('✅ Cleanup successful');

    console.log('🎉 Firebase connection is working properly!');
  } catch (error) {
    console.error('❌ Connection test failed:', error.message);
    console.error('🔍 Full error:', error);
  }
}

testConnection()
  .then(() => {
    console.log('\n✅ Test completed');
    process.exit(0);
  })
  .catch((error) => {
    console.error('\n💥 Test failed:', error);
    process.exit(1);
  });
