const admin = require('firebase-admin');

// Initialize Firebase Admin
const serviceAccount = require('./google-services.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

async function checkData() {
  try {
    console.log('🔍 Checking Firestore data...\n');

    // Check posts collection
    console.log('📄 POSTS COLLECTION:');
    const postsSnapshot = await db.collection('posts').limit(5).get();
    console.log(`Total posts found: ${postsSnapshot.size}`);

    postsSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`- Post ID: ${doc.id}`);
      console.log(`  User: ${data.username || 'Unknown'}`);
      console.log(`  Caption: ${data.caption || 'No caption'}`);
      console.log(`  Has imageBase64: ${data.imageBase64 ? 'Yes' : 'No'} (${data.imageBase64 ? data.imageBase64.length : 0} chars)`);
      console.log(`  Has imageUrl: ${data.imageUrl ? 'Yes' : 'No'} (${data.imageUrl ? data.imageUrl.length : 0} chars)`);
      console.log(`  Created: ${data.createdAt ? data.createdAt.toDate() : 'Unknown'}`);
      console.log(`  Visible: ${data.isVisible !== false ? 'Yes' : 'No'}`);
      console.log('');
    });

    // Check stories collection
    console.log('📖 STORIES COLLECTION:');
    const storiesSnapshot = await db.collection('stories').limit(5).get();
    console.log(`Total stories found: ${storiesSnapshot.size}`);

    storiesSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`- Story ID: ${doc.id}`);
      console.log(`  User: ${data.username || 'Unknown'}`);
      console.log(`  Has imageBase64: ${data.imageBase64 ? 'Yes' : 'No'} (${data.imageBase64 ? data.imageBase64.length : 0} chars)`);
      console.log(`  Has imageUrl: ${data.imageUrl ? 'Yes' : 'No'} (${data.imageUrl ? data.imageUrl.length : 0} chars)`);
      console.log(`  Active: ${data.isActive !== false ? 'Yes' : 'No'}`);
      console.log(`  Expires: ${data.expiresAt ? data.expiresAt.toDate() : 'Unknown'}`);
      console.log('');
    });

    // Check users collection
    console.log('👤 USERS COLLECTION:');
    const usersSnapshot = await db.collection('users').limit(3).get();
    console.log(`Total users found: ${usersSnapshot.size}`);

    usersSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`- User ID: ${doc.id}`);
      console.log(`  Username: ${data.username || 'Unknown'}`);
      console.log(`  Email: ${data.email || 'Unknown'}`);
      console.log(`  Has profileImageBase64: ${data.profileImageBase64 ? 'Yes' : 'No'}`);
      console.log(`  Posts count: ${data.postsCount || 0}`);
      console.log('');
    });
  } catch (error) {
    console.error('❌ Error checking data:', error);
  }
}

checkData().then(() => {
  console.log('✅ Data check completed');
  process.exit(0);
});
