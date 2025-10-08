const { initializeFirebase } = require('./firebase-config');
const { sampleBlogs } = require('./sample-data');

const importBlogData = async () => {
  console.log('🚀 Starting blog data import...');

  const db = initializeFirebase();
  const blogsCollection = db.collection('blogs');

  try {
    let successCount = 0;
    let errorCount = 0;

    // Import each blog
    for (const blog of sampleBlogs) {
      try {
        // Use the blog id as document id, or let Firestore generate one
        const docRef = blog.id ? blogsCollection.doc(blog.id) : blogsCollection.doc();

        // Remove the id field from the data before saving
        const { id, ...blogData } = blog;

        await docRef.set(blogData);

        console.log(`✅ Blog imported: "${blog.title}"`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing blog "${blog.title}":`, error.message);
        errorCount++;
      }
    }

    console.log('\n📊 Import Summary:');
    console.log(`✅ Successfully imported: ${successCount} blogs`);
    console.log(`❌ Failed to import: ${errorCount} blogs`);
    console.log(`📝 Total processed: ${sampleBlogs.length} blogs`);

    if (successCount > 0) {
      console.log('\n🎉 Blog data imported successfully!');
      console.log('You can now check your Firestore database in Firebase Console.');
    }
  } catch (error) {
    console.error('❌ Error during import process:', error);
  }
};

// Import user data (demo users)
const importUserData = async () => {
  console.log('🚀 Starting user data import...');

  const db = initializeFirebase();
  const usersCollection = db.collection('users');

  const sampleUsers = [
    {
      id: 'user-demo-1',
      email: 'demo@blogapp.com',
      displayName: 'Demo User',
      createdAt: Date.now() - 30 * 24 * 60 * 60 * 1000, // 30 days ago
      isActive: true,
    },
    {
      id: 'user-demo-2',
      email: 'blogger@example.com',
      displayName: 'Professional Blogger',
      createdAt: Date.now() - 20 * 24 * 60 * 60 * 1000, // 20 days ago
      isActive: true,
    },
    {
      id: 'user-demo-3',
      email: 'developer@tech.com',
      displayName: 'Tech Developer',
      createdAt: Date.now() - 15 * 24 * 60 * 60 * 1000, // 15 days ago
      isActive: true,
    },
  ];

  try {
    let successCount = 0;
    let errorCount = 0;

    for (const user of sampleUsers) {
      try {
        const { id, ...userData } = user;
        await usersCollection.doc(id).set(userData);

        console.log(`✅ User imported: "${user.email}"`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing user "${user.email}":`, error.message);
        errorCount++;
      }
    }

    console.log('\n📊 User Import Summary:');
    console.log(`✅ Successfully imported: ${successCount} users`);
    console.log(`❌ Failed to import: ${errorCount} users`);
  } catch (error) {
    console.error('❌ Error during user import:', error);
  }
};

// Main function
const main = async () => {
  console.log('🔥 BlogApp Firebase Data Import Tool');
  console.log('=====================================\n');

  try {
    // Import users first
    await importUserData();
    console.log('\n-----------------------------------\n');

    // Then import blogs
    await importBlogData();

    console.log('\n🎯 Import process completed!');
    console.log('Check your Firebase Console to see the imported data.');
  } catch (error) {
    console.error('❌ Import failed:', error);
  }

  process.exit(0);
};

// Run the import if this file is executed directly
if (require.main === module) {
  main();
}

module.exports = { importBlogData, importUserData };
