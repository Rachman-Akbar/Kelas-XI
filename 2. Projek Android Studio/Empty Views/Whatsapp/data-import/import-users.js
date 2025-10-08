const { auth, db } = require('./firebase-config');
const fs = require('fs');
const path = require('path');

async function importUsers() {
  try {
    console.log('🔄 Starting user import...');

    // Read sample users data
    const usersData = JSON.parse(fs.readFileSync(path.join(__dirname, 'sample-users.json'), 'utf8'));

    const importedUsers = [];

    for (const userData of usersData) {
      try {
        // Create user in Firebase Auth
        const userRecord = await auth.createUser({
          email: userData.email,
          password: userData.password,
          displayName: userData.username,
        });

        console.log(`✅ Created user: ${userData.email} with UID: ${userRecord.uid}`);

        // Save user data to Realtime Database
        const userDataForDB = {
          uid: userRecord.uid,
          username: userData.username,
          email: userData.email,
          profilePic: userData.profilePic,
          status: userData.status,
          lastSeen: userData.lastSeen,
        };

        await db.ref(`users/${userRecord.uid}`).set(userDataForDB);
        console.log(`✅ Saved user data to database: ${userData.username}`);

        importedUsers.push({
          ...userDataForDB,
          originalEmail: userData.email,
        });
      } catch (error) {
        if (error.code === 'auth/email-already-exists') {
          console.log(`⚠️ User already exists: ${userData.email}`);

          // Get existing user and add to imported users list
          const existingUser = await auth.getUserByEmail(userData.email);
          const userDataForDB = {
            uid: existingUser.uid,
            username: userData.username,
            email: userData.email,
            profilePic: userData.profilePic,
            status: userData.status,
            lastSeen: userData.lastSeen,
          };

          await db.ref(`users/${existingUser.uid}`).update(userDataForDB);
          console.log(`✅ Updated existing user data: ${userData.username}`);

          importedUsers.push({
            ...userDataForDB,
            originalEmail: userData.email,
          });
        } else {
          console.error(`❌ Error creating user ${userData.email}:`, error.message);
        }
      }
    }

    // Save user mapping for chat import
    fs.writeFileSync(path.join(__dirname, 'user-mapping.json'), JSON.stringify(importedUsers, null, 2));

    console.log(`\n🎉 User import completed! Imported ${importedUsers.length} users.`);
    console.log('📝 User mapping saved to user-mapping.json');
  } catch (error) {
    console.error('❌ Error importing users:', error);
  }
}

// Run the import if this file is executed directly
if (require.main === module) {
  importUsers()
    .then(() => {
      console.log('✅ Import process finished.');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import process failed:', error);
      process.exit(1);
    });
}

module.exports = { importUsers };
