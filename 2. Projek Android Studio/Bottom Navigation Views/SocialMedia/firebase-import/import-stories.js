const FirebaseConfig = require('./config/firebase');
const { sampleStories } = require('./data/sample-data');
const fs = require('fs');

class StoryImporter {
  constructor() {
    const firebaseConfig = new FirebaseConfig();
    this.db = firebaseConfig.getFirestore();
  }

  async importStories() {
    console.log('🚀 Starting story import...');

    // Load imported users
    let importedUsers = [];
    try {
      const usersData = fs.readFileSync('./imported-users.json', 'utf8');
      importedUsers = JSON.parse(usersData);
    } catch (error) {
      console.error('❌ Error loading imported users. Please run import-users.js first.');
      throw new Error('Users must be imported first');
    }

    if (importedUsers.length === 0) {
      throw new Error('No users found. Please import users first.');
    }

    const importedStories = [];

    try {
      // Create stories for random users (not all users have stories)
      const usersWithStories = importedUsers.slice(0, 6); // First 6 users get stories

      for (let userIndex = 0; userIndex < usersWithStories.length; userIndex++) {
        const user = usersWithStories[userIndex];
        const storyData = sampleStories[userIndex % sampleStories.length];

        console.log(`📝 Creating story for user: ${user.username}`);

        try {
          // Get user data from Firestore
          const userDoc = await this.db.collection('users').doc(user.uid).get();
          const userData = userDoc.data();

          const now = new Date();
          const expiresAt = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 24 hours from now

          // Generate random views
          const viewedBy = this.generateRandomViews(importedUsers, user.uid);

          const storyDoc = {
            userId: user.uid,
            username: user.username,
            userProfileImageUrl: userData.profileImageUrl || '',
            imageUrl: storyData.imageUrl,
            text: storyData.text,
            backgroundColor: storyData.backgroundColor,
            createdAt: this.getRandomRecentDate(),
            expiresAt: expiresAt,
            viewedBy: viewedBy,
            isActive: true,
          };

          // Add story to Firestore
          const storyRef = await this.db.collection('stories').add(storyDoc);

          importedStories.push({
            storyId: storyRef.id,
            userId: user.uid,
            username: user.username,
            hasImage: storyData.imageUrl !== '',
            hasText: storyData.text !== '',
          });

          console.log(`✅ Story created successfully: ${storyRef.id}`);

          // Add delay to avoid rate limiting
          await this.delay(300);
        } catch (error) {
          console.error(`❌ Error creating story for ${user.username}:`, error.message);
        }
      }

      console.log(`\n🎉 Story import completed!`);
      console.log(`📊 Total stories created: ${importedStories.length}`);

      // Save imported stories list for reference
      fs.writeFileSync('./imported-stories.json', JSON.stringify(importedStories, null, 2));
      console.log(`💾 Story list saved to: imported-stories.json`);

      return importedStories;
    } catch (error) {
      console.error('❌ Error during story import:', error);
      throw error;
    }
  }

  generateRandomViews(users, storyOwnerId) {
    // Don't include story owner in viewers
    const otherUsers = users.filter((user) => user.uid !== storyOwnerId);
    const shuffled = [...otherUsers].sort(() => 0.5 - Math.random());
    const viewCount = Math.floor(Math.random() * Math.min(10, otherUsers.length)); // Random 0-10 views
    return shuffled.slice(0, viewCount).map((user) => user.uid);
  }

  getRandomRecentDate() {
    const now = new Date();
    const hoursAgo = Math.floor(Math.random() * 20); // Random time within last 20 hours
    const randomDate = new Date(now.getTime() - hoursAgo * 60 * 60 * 1000);
    return randomDate;
  }

  async delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Run import if called directly
if (require.main === module) {
  const importer = new StoryImporter();
  importer
    .importStories()
    .then(() => {
      console.log('✅ Import completed successfully');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import failed:', error);
      process.exit(1);
    });
  }

  async importStoriesWithoutUsers() {
    console.log('🚀 Starting story import without user authentication...');

    // Create mock users for stories
    const mockUsers = [
      { uid: 'user1', email: 'john@example.com', displayName: 'John Doe', photoURL: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user2', email: 'jane@example.com', displayName: 'Jane Smith', photoURL: 'https://images.unsplash.com/photo-1494790108755-2616b2123dc3?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user3', email: 'mike@example.com', displayName: 'Mike Wilson', photoURL: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user4', email: 'sarah@example.com', displayName: 'Sarah Johnson', photoURL: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user5', email: 'alex@example.com', displayName: 'Alex Brown', photoURL: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face' }
    ];

    const importedStories = [];

    try {
      for (let i = 0; i < sampleStories.length; i++) {
        const story = sampleStories[i] || sampleStories[0];
        const user = mockUsers[i % mockUsers.length];

        console.log(`📱 Creating story ${i + 1}/${sampleStories.length} by ${user.displayName}`);

        const storyData = {
          userId: user.uid,
          userEmail: user.email,
          userName: user.displayName,
          userPhotoUrl: user.photoURL,
          imageUrl: story.imageUrl,
          text: story.text,
          backgroundColor: story.backgroundColor,
          views: Math.floor(Math.random() * 100), // Random views
          viewedBy: [],
          createdAt: new Date(),
          expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000) // 24 hours from now
        };

        const docRef = await this.db.collection('stories').add(storyData);
        importedStories.push({
          id: docRef.id,
          ...storyData
        });

        // Delay to avoid overwhelming Firestore
        await new Promise(resolve => setTimeout(resolve, 500));
      }

      console.log('🎉 Story import completed!');
      console.log(`📊 Total stories processed: ${sampleStories.length}`);
      console.log(`✅ Successfully imported: ${importedStories.length}`);

      // Save imported stories
      fs.writeFileSync('./imported-stories-mock.json', JSON.stringify(importedStories, null, 2));
      console.log('💾 Story list saved to: imported-stories-mock.json');

      return importedStories;

    } catch (error) {
      console.error('❌ Error during story import:', error);
      throw error;
    }
  }
}

module.exports = StoryImporter;
