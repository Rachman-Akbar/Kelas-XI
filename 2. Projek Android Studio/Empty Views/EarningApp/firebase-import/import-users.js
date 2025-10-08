const admin = require('firebase-admin');

// Import user data
const { achievements, sampleUsers, leaderboardEntries, defaultUserSettings } = require('./data/users');

async function importUserData() {
  try {
    const db = admin.firestore();

    console.log('🚀 Starting user data import...');

    // Create batch for better performance
    const batch = db.batch();

    // 1. Import Achievements
    console.log('📊 Importing achievements...');

    for (const achievement of achievements) {
      const achievementRef = db.collection('achievements').doc(achievement.id);
      batch.set(achievementRef, achievement);
    }

    // 2. Import Default User Settings Template
    console.log('⚙️ Importing default user settings...');

    const settingsRef = db.collection('app_settings').doc('default_user_settings');
    batch.set(settingsRef, {
      ...defaultUserSettings,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    });

    // 3. Import Sample Users (optional - for testing)
    console.log('👥 Importing sample users...');

    for (const user of sampleUsers) {
      const userRef = db.collection('users').doc(user.id);
      batch.set(userRef, user);

      // Create user settings for each sample user
      const userSettingsRef = db.collection('user_settings').doc(user.id);
      batch.set(userSettingsRef, {
        ...defaultUserSettings,
        userId: user.id,
        createdAt: user.createdAt,
        updatedAt: Date.now(),
      });
    }

    // 4. Import Leaderboard Entries
    console.log('🏆 Importing leaderboard entries...');

    for (const entry of leaderboardEntries) {
      const leaderboardRef = db.collection('leaderboard').doc(entry.id);
      batch.set(leaderboardRef, entry);
    }

    // 5. Create Global Leaderboard Summary
    const globalLeaderboardRef = db.collection('app_settings').doc('global_leaderboard');
    batch.set(globalLeaderboardRef, {
      totalUsers: sampleUsers.length,
      lastUpdated: Date.now(),
      topUser: leaderboardEntries[0],
      averageScore: leaderboardEntries.reduce((sum, entry) => sum + entry.totalScore, 0) / leaderboardEntries.length,
      createdAt: Date.now(),
    });

    // 6. Create User Statistics Summary
    const userStatsRef = db.collection('app_settings').doc('user_statistics');
    batch.set(userStatsRef, {
      totalRegisteredUsers: sampleUsers.length,
      activeUsers24h: sampleUsers.filter((user) => Date.now() - user.lastLoginAt < 86400000).length,
      activeUsers7d: sampleUsers.filter((user) => Date.now() - user.lastLoginAt < 604800000).length,
      totalQuizzesCompleted: sampleUsers.reduce((sum, user) => sum + user.statistics.totalQuizzes, 0),
      totalTimeSpent: sampleUsers.reduce((sum, user) => sum + user.statistics.totalTimeSpent, 0),
      averageScore: sampleUsers.reduce((sum, user) => sum + user.statistics.averageScore, 0) / sampleUsers.length,
      lastUpdated: Date.now(),
      createdAt: Date.now(),
    });

    // Commit all operations
    await batch.commit();

    // Display import summary
    console.log('\n✅ User data import completed successfully!');
    console.log('📊 Import Summary:');
    console.log(`   • ${achievements.length} achievements imported`);
    console.log(`   • ${sampleUsers.length} sample users imported`);
    console.log(`   • ${leaderboardEntries.length} leaderboard entries imported`);
    console.log(`   • Default user settings template created`);
    console.log(`   • Global statistics created`);

    console.log('\n📋 Achievements imported:');
    achievements.forEach((achievement) => {
      console.log(`   • ${achievement.title} (${achievement.type})`);
    });

    console.log('\n👥 Sample users imported:');
    sampleUsers.forEach((user) => {
      console.log(`   • ${user.name} (${user.email}) - Level ${user.level} - ${user.totalScore} points`);
    });

    console.log('\n🏆 Leaderboard:');
    leaderboardEntries.forEach((entry) => {
      console.log(`   ${entry.rank}. ${entry.userName} - ${entry.totalScore} points`);
    });

    return {
      success: true,
      imported: {
        achievements: achievements.length,
        users: sampleUsers.length,
        leaderboard: leaderboardEntries.length,
        settings: 1,
      },
    };
  } catch (error) {
    console.error('❌ Error importing user data:', error);
    throw error;
  }
}

// Function to create user profile on registration
async function createUserProfile(userId, userData) {
  try {
    const db = admin.firestore();

    const userProfile = {
      id: userId,
      email: userData.email,
      name: userData.name || userData.email.split('@')[0],
      profileImageUrl: userData.profileImageUrl || '',
      totalScore: 0,
      level: 1,
      coins: 100, // starting coins
      subjectProgress: {},
      dailyStreak: 0,
      maxDailyStreak: 0,
      lastLoginAt: Date.now(),
      createdAt: Date.now(),
      achievements: [],
      statistics: {
        totalQuizzes: 0,
        totalTimeSpent: 0,
        averageScore: 0,
        favoriteSubject: '',
        strongestDifficulty: 'easy',
        weeklyGoal: 5,
        weeklyProgress: 0,
      },
    };

    // Create user profile
    await db.collection('users').doc(userId).set(userProfile);

    // Create user settings
    await db
      .collection('user_settings')
      .doc(userId)
      .set({
        ...defaultUserSettings,
        userId: userId,
        createdAt: Date.now(),
        updatedAt: Date.now(),
      });

    console.log(`✅ User profile created for ${userData.email}`);
    return userProfile;
  } catch (error) {
    console.error('❌ Error creating user profile:', error);
    throw error;
  }
}

// Function to update user login
async function updateUserLogin(userId) {
  try {
    const db = admin.firestore();
    const userRef = db.collection('users').doc(userId);

    const userDoc = await userRef.get();
    if (!userDoc.exists) {
      throw new Error('User not found');
    }

    const userData = userDoc.data();
    const now = Date.now();
    const lastLogin = userData.lastLoginAt;

    // Check if this is a consecutive day (within 48 hours)
    const timeDiff = now - lastLogin;
    const isConsecutiveDay = timeDiff >= 86400000 && timeDiff <= 172800000; // 24-48 hours

    let newStreak = userData.dailyStreak || 0;
    if (isConsecutiveDay) {
      newStreak += 1;
    } else if (timeDiff > 172800000) {
      newStreak = 1; // Reset streak if more than 48 hours
    }

    const updates = {
      lastLoginAt: now,
      dailyStreak: newStreak,
      maxDailyStreak: Math.max(userData.maxDailyStreak || 0, newStreak),
    };

    await userRef.update(updates);

    console.log(`✅ User login updated for ${userId}`);
    return { ...userData, ...updates };
  } catch (error) {
    console.error('❌ Error updating user login:', error);
    throw error;
  }
}

module.exports = {
  importUserData,
  createUserProfile,
  updateUserLogin,
};
