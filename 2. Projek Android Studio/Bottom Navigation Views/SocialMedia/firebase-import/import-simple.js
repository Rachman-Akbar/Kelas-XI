const FirebaseConfig = require('./config/firebase');
const { samplePosts, sampleStories } = require('./data/sample-data');
const { importPreloadedImages } = require('./scripts/import-preloaded-images');
const fs = require('fs');

class SimpleDataImporter {
  constructor() {
    const firebaseConfig = new FirebaseConfig();
    this.db = firebaseConfig.getFirestore();
  }

  async importAllData() {
    console.log('🚀 Starting complete data import to Firestore...\n');

    try {
      // Step 1: Import Preloaded Images
      console.log('==========================================');
      console.log('STEP 1: IMPORTING PRELOADED IMAGES');
      console.log('==========================================');
      await importPreloadedImages();
      console.log(`✅ Preloaded images imported\n`);

      await this.delay(2000);

      // Step 2: Import Mock Posts
      console.log('==========================================');
      console.log('STEP 2: IMPORTING POSTS');
      console.log('==========================================');
      const posts = await this.importMockPosts();
      console.log(`✅ Posts imported: ${posts.length}\n`);

      await this.delay(2000);

      // Step 3: Import Mock Stories
      console.log('==========================================');
      console.log('STEP 3: IMPORTING STORIES');
      console.log('==========================================');
      const stories = await this.importMockStories();
      console.log(`✅ Stories imported: ${stories.length}\n`);

      // Final summary
      console.log('==========================================');
      console.log('🎉 COMPLETE IMPORT SUCCESSFUL!');
      console.log('==========================================');
      console.log(`📊 SUMMARY:`);
      console.log(`   🖼️  Preloaded Images: 28 items`);
      console.log(`   📸 Posts: ${posts.length}`);
      console.log(`   📱 Stories: ${stories.length}`);
      console.log(`   🗂️  Total records: ${posts.length + stories.length + 28}`);
      console.log('==========================================\n');

      return { posts, stories };
    } catch (error) {
      console.error('❌ Error during complete import:', error);
      throw error;
    }
  }

  async importMockPosts() {
    console.log('📸 Starting posts import with mock users...');

    const mockUsers = [
      { uid: 'user1', displayName: 'John Doe', photoURL: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user2', displayName: 'Jane Smith', photoURL: 'https://images.unsplash.com/photo-1494790108755-2616b2123dc3?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user3', displayName: 'Mike Wilson', photoURL: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user4', displayName: 'Sarah Johnson', photoURL: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user5', displayName: 'Alex Brown', photoURL: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face' },
    ];

    const importedPosts = [];

    for (let i = 0; i < samplePosts.length; i++) {
      const post = samplePosts[i];
      const user = mockUsers[i % mockUsers.length];

      console.log(`📝 Creating post ${i + 1}/${samplePosts.length} by ${user.displayName}`);

      const postData = {
        userId: user.uid,
        userName: user.displayName,
        userPhotoUrl: user.photoURL,
        content: post.caption || 'Amazing post!', // Use caption from sample data
        imageUrl: post.imageUrl,
        likes: post.likesCount || Math.floor(Math.random() * 50),
        likedBy: [],
        comments: this.generateRandomComments(mockUsers),
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      const docRef = await this.db.collection('posts').add(postData);
      importedPosts.push({ id: docRef.id, ...postData });

      await this.delay(300);
    }

    return importedPosts;
  }

  async importMockStories() {
    console.log('📱 Starting stories import with mock users...');

    const mockUsers = [
      { uid: 'user1', displayName: 'John Doe', photoURL: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user2', displayName: 'Jane Smith', photoURL: 'https://images.unsplash.com/photo-1494790108755-2616b2123dc3?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user3', displayName: 'Mike Wilson', photoURL: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user4', displayName: 'Sarah Johnson', photoURL: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user5', displayName: 'Alex Brown', photoURL: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face' },
    ];

    const importedStories = [];

    for (let i = 0; i < sampleStories.length; i++) {
      const story = sampleStories[i];
      const user = mockUsers[i % mockUsers.length];

      console.log(`📱 Creating story ${i + 1}/${sampleStories.length} by ${user.displayName}`);

      const storyData = {
        userId: user.uid,
        userName: user.displayName,
        userPhotoUrl: user.photoURL,
        imageUrl: story.imageUrl,
        text: story.text,
        backgroundColor: story.backgroundColor,
        views: Math.floor(Math.random() * 100),
        viewedBy: [],
        createdAt: new Date(),
        expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
      };

      const docRef = await this.db.collection('stories').add(storyData);
      importedStories.push({ id: docRef.id, ...storyData });

      await this.delay(300);
    }

    return importedStories;
  }

  generateRandomComments(users) {
    const comments = [];
    const commentTexts = ['Great post! 👍', 'Love this! ❤️', 'Amazing! 🔥', 'So cool! 😍', 'Awesome content! 🌟', 'Beautiful! ✨', 'Incredible! 🤩', 'Nice shot! 📸'];

    const commentCount = Math.floor(Math.random() * 4);
    for (let i = 0; i < commentCount; i++) {
      const user = users[Math.floor(Math.random() * users.length)];
      comments.push({
        userId: user.uid,
        userName: user.displayName,
        userPhotoUrl: user.photoURL,
        text: commentTexts[Math.floor(Math.random() * commentTexts.length)],
        createdAt: new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000),
      });
    }

    return comments;
  }

  delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Run import
const importer = new SimpleDataImporter();
importer
  .importAllData()
  .then((result) => {
    console.log('🎉 All data imported successfully!');
    console.log(`📊 Final count: ${result.posts.length} posts, ${result.stories.length} stories`);
    process.exit(0);
  })
  .catch((error) => {
    console.error('💥 Import failed:', error);
    process.exit(1);
  });
