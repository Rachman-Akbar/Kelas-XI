const FirebaseConfig = require('./config/firebase');
const { samplePosts } = require('./data/sample-data');
const fs = require('fs');

class PostImporter {
  constructor() {
    const firebaseConfig = new FirebaseConfig();
    this.db = firebaseConfig.getFirestore();
  }

  async importPosts() {
    console.log('🚀 Starting post import...');

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

    const importedPosts = [];

    try {
      // Create multiple posts for each user
      for (let userIndex = 0; userIndex < importedUsers.length; userIndex++) {
        const user = importedUsers[userIndex];
        const postsPerUser = Math.min(3, samplePosts.length); // Max 3 posts per user

        for (let postIndex = 0; postIndex < postsPerUser; postIndex++) {
          const postData = samplePosts[(userIndex * postsPerUser + postIndex) % samplePosts.length];

          console.log(`📝 Creating post ${postIndex + 1}/${postsPerUser} for user: ${user.username}`);

          try {
            // Get user data from Firestore
            const userDoc = await this.db.collection('users').doc(user.uid).get();
            const userData = userDoc.data();

            // Generate random likes
            const likedBy = this.generateRandomLikes(importedUsers, postData.likesCount);

            const postDoc = {
              userId: user.uid,
              username: user.username,
              userProfileImageUrl: userData.profileImageUrl || '',
              imageUrl: postData.imageUrl,
              caption: postData.caption,
              likesCount: likedBy.length,
              commentsCount: postData.commentsCount,
              likedBy: likedBy,
              isVisible: true,
              createdAt: this.getRandomDate(),
            };

            // Add post to Firestore
            const postRef = await this.db.collection('posts').add(postDoc);

            importedPosts.push({
              postId: postRef.id,
              userId: user.uid,
              username: user.username,
              caption: postData.caption.substring(0, 50) + '...',
            });

            console.log(`✅ Post created successfully: ${postRef.id}`);

            // Add delay to avoid rate limiting
            await this.delay(300);
          } catch (error) {
            console.error(`❌ Error creating post for ${user.username}:`, error.message);
          }
        }
      }

      console.log(`\n🎉 Post import completed!`);
      console.log(`📊 Total posts created: ${importedPosts.length}`);

      // Save imported posts list for reference
      fs.writeFileSync('./imported-posts.json', JSON.stringify(importedPosts, null, 2));
      console.log(`💾 Post list saved to: imported-posts.json`);

      return importedPosts;
    } catch (error) {
      console.error('❌ Error during post import:', error);
      throw error;
    }
  }

  generateRandomLikes(users, targetCount) {
    const shuffled = [...users].sort(() => 0.5 - Math.random());
    const likeCount = Math.min(targetCount, users.length);
    return shuffled.slice(0, likeCount).map((user) => user.uid);
  }

  getRandomDate() {
    const now = new Date();
    const daysAgo = Math.floor(Math.random() * 30); // Random date within last 30 days
    const randomDate = new Date(now.getTime() - daysAgo * 24 * 60 * 60 * 1000);
    return randomDate;
  }

  async delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Run import if called directly
if (require.main === module) {
  const importer = new PostImporter();
  importer
    .importPosts()
    .then(() => {
      console.log('✅ Import completed successfully');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import failed:', error);
      process.exit(1);
    });
  }

  async importPostsWithoutUsers() {
    console.log('🚀 Starting post import without user authentication...');

    // Create mock users for posts
    const mockUsers = [
      { uid: 'user1', email: 'john@example.com', displayName: 'John Doe', photoURL: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user2', email: 'jane@example.com', displayName: 'Jane Smith', photoURL: 'https://images.unsplash.com/photo-1494790108755-2616b2123dc3?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user3', email: 'mike@example.com', displayName: 'Mike Wilson', photoURL: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user4', email: 'sarah@example.com', displayName: 'Sarah Johnson', photoURL: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face' },
      { uid: 'user5', email: 'alex@example.com', displayName: 'Alex Brown', photoURL: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face' }
    ];

    const importedPosts = [];

    try {
      for (let i = 0; i < samplePosts.length; i++) {
        const post = samplePosts[i] || samplePosts[0];
        const user = mockUsers[i % mockUsers.length];

        console.log(`📝 Creating post ${i + 1}/${samplePosts.length} by ${user.displayName}`);

        const postData = {
          userId: user.uid,
          userEmail: user.email,
          userName: user.displayName,
          userPhotoUrl: user.photoURL,
          content: post.content,
          imageUrl: post.imageUrl,
          likes: Math.floor(Math.random() * 50), // Random likes
          likedBy: [],
          comments: [],
          createdAt: new Date(),
          updatedAt: new Date()
        };

        // Add random comments
        const commentCount = Math.floor(Math.random() * 5);
        for (let j = 0; j < commentCount; j++) {
          const commentUser = mockUsers[Math.floor(Math.random() * mockUsers.length)];
          postData.comments.push({
            userId: commentUser.uid,
            userName: commentUser.displayName,
            userPhotoUrl: commentUser.photoURL,
            text: this.generateRandomComment(),
            createdAt: new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000) // Random time in last 24h
          });
        }

        const docRef = await this.db.collection('posts').add(postData);
        importedPosts.push({
          id: docRef.id,
          ...postData
        });

        // Delay to avoid overwhelming Firestore
        await new Promise(resolve => setTimeout(resolve, 500));
      }

      console.log('🎉 Post import completed!');
      console.log(`📊 Total posts processed: ${samplePosts.length}`);
      console.log(`✅ Successfully imported: ${importedPosts.length}`);

      // Save imported posts
      fs.writeFileSync('./imported-posts-mock.json', JSON.stringify(importedPosts, null, 2));
      console.log('💾 Post list saved to: imported-posts-mock.json');

      return importedPosts;

    } catch (error) {
      console.error('❌ Error during post import:', error);
      throw error;
    }
  }

  generateRandomComment() {
    const comments = [
      "Great post! 👍",
      "Love this! ❤️",
      "Amazing! 🔥",
      "So cool! 😍",
      "Awesome content! 🌟",
      "Beautiful! ✨",
      "Incredible! 🤩",
      "Nice shot! 📸",
      "Well done! 👏",
      "Fantastic! 🎉"
    ];
    return comments[Math.floor(Math.random() * comments.length)];
  }
}

module.exports = PostImporter;
