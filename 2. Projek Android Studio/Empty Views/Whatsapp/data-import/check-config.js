const { admin, db, auth } = require('./firebase-config');

async function checkFirebaseConfiguration() {
  console.log('🔍 Checking Firebase configuration...\n');

  try {
    // Test Firebase connection
    console.log('✅ Firebase Admin SDK initialized successfully');
    console.log(`📋 Project ID: ${admin.app().options.projectId}`);

    // Test database connection
    try {
      const testRef = db.ref('test');
      await testRef.set({ test: 'connection', timestamp: Date.now() });
      await testRef.remove();
      console.log('✅ Realtime Database connection successful');
    } catch (dbError) {
      console.log('❌ Realtime Database connection failed:', dbError.message);
    }

    // Test Auth configuration
    try {
      const users = await auth.listUsers(1);
      console.log('✅ Firebase Authentication is configured');
      console.log(`👥 Existing users count: ${users.users.length}`);
    } catch (authError) {
      console.log('❌ Firebase Authentication error:', authError.message);

      if (authError.code === 'auth/configuration-not-found') {
        console.log('\n🔧 Firebase Authentication is not enabled or configured.');
        console.log('Please follow these steps:');
        console.log('1. Go to Firebase Console: https://console.firebase.google.com');
        console.log('2. Select your project: whatsapp-31cde');
        console.log('3. Go to Authentication > Sign-in method');
        console.log('4. Enable "Email/Password" provider');
        console.log('5. Save and try import again');
      }
    }
  } catch (error) {
    console.error('❌ Firebase configuration error:', error.message);
  }
}

checkFirebaseConfiguration()
  .then(() => {
    console.log('\n✅ Configuration check completed.');
    process.exit(0);
  })
  .catch((error) => {
    console.error('❌ Configuration check failed:', error);
    process.exit(1);
  });
