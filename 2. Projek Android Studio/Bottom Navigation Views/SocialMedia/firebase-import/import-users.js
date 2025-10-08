const FirebaseConfig = require('./config/firebase');
const { sampleUsers } = require('./data/sample-data');

class UserImporter {
  constructor() {
    const firebaseConfig = new FirebaseConfig();
    this.db = firebaseConfig.getFirestore();
    this.auth = firebaseConfig.getAuth();
  }

  async importUsers() {
    console.log('🚀 Starting user import...');

    const importedUsers = [];

    try {
      for (let i = 0; i < sampleUsers.length; i++) {
        const userData = sampleUsers[i];
        console.log(`📝 Creating user ${i + 1}/${sampleUsers.length}: ${userData.username}`);

        try {
          // Create user in Firebase Auth
          const userRecord = await this.auth.createUser({
            email: userData.email,
            password: userData.password,
            displayName: userData.username,
            photoURL: userData.profileImageUrl,
          });

          // Create user document in Firestore
          const userDoc = {
            userId: userRecord.uid,
            username: userData.username,
            email: userData.email,
            profileImageUrl: userData.profileImageUrl,
            bio: userData.bio,
            followersCount: userData.followersCount,
            followingCount: userData.followingCount,
            postsCount: userData.postsCount,
            createdAt: new Date(),
            isPrivate: userData.isPrivate,
          };

          await this.db.collection('users').doc(userRecord.uid).set(userDoc);

          importedUsers.push({
            uid: userRecord.uid,
            username: userData.username,
            email: userData.email,
          });

          console.log(`✅ User created successfully: ${userData.username} (${userRecord.uid})`);

          // Add delay to avoid rate limiting
          await this.delay(500);
        } catch (error) {
          console.error(`❌ Error creating user ${userData.username}:`, error.message);

          // If user already exists in Auth, try to get the existing user
          if (error.code === 'auth/email-already-exists') {
            try {
              const existingUser = await this.auth.getUserByEmail(userData.email);
              console.log(`ℹ️  User already exists: ${userData.username} (${existingUser.uid})`);

              // Update/create Firestore document for existing user
              const userDoc = {
                userId: existingUser.uid,
                username: userData.username,
                email: userData.email,
                profileImageUrl: userData.profileImageUrl,
                bio: userData.bio,
                followersCount: userData.followersCount,
                followingCount: userData.followingCount,
                postsCount: userData.postsCount,
                createdAt: new Date(),
                isPrivate: userData.isPrivate,
              };

              await this.db.collection('users').doc(existingUser.uid).set(userDoc, { merge: true });

              importedUsers.push({
                uid: existingUser.uid,
                username: userData.username,
                email: userData.email,
              });
            } catch (getError) {
              console.error(`❌ Error handling existing user:`, getError.message);
            }
          }
        }
      }

      console.log(`\n🎉 User import completed!`);
      console.log(`📊 Total users processed: ${sampleUsers.length}`);
      console.log(`✅ Successfully imported: ${importedUsers.length}`);

      // Save imported users list for reference
      const fs = require('fs');
      fs.writeFileSync('./imported-users.json', JSON.stringify(importedUsers, null, 2));
      console.log(`💾 User list saved to: imported-users.json`);

      return importedUsers;
    } catch (error) {
      console.error('❌ Error during user import:', error);
      throw error;
    }
  }

  async delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Run import if called directly
if (require.main === module) {
  const importer = new UserImporter();
  importer
    .importUsers()
    .then(() => {
      console.log('✅ Import completed successfully');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import failed:', error);
      process.exit(1);
    });
}

module.exports = UserImporter;
