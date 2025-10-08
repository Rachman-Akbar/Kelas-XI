const { initializeFirebase } = require('./firebase-config');
const { sampleBlogs, sampleUsers, sampleCategories, sampleComments, sampleTags } = require('./sample-data-complete');

// Import Users Collection
const importUsersData = async (db) => {
  console.log('👥 Importing users data...');

  const usersCollection = db.collection('users');
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const user of sampleUsers) {
      try {
        const { id, ...userData } = user;
        await usersCollection.doc(id).set(userData);

        console.log(`✅ User imported: "${user.displayName}" (${user.email})`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing user "${user.email}":`, error.message);
        errorCount++;
      }
    }

    console.log(`📊 Users Import: ✅ ${successCount} success, ❌ ${errorCount} failed`);
    return { successCount, errorCount };
  } catch (error) {
    console.error('❌ Error during users import:', error);
    return { successCount, errorCount: sampleUsers.length };
  }
};

// Import Categories Collection
const importCategoriesData = async (db) => {
  console.log('📁 Importing categories data...');

  const categoriesCollection = db.collection('categories');
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const category of sampleCategories) {
      try {
        const { id, ...categoryData } = category;
        await categoriesCollection.doc(id).set(categoryData);

        console.log(`✅ Category imported: "${category.name}" ${category.icon}`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing category "${category.name}":`, error.message);
        errorCount++;
      }
    }

    console.log(`📊 Categories Import: ✅ ${successCount} success, ❌ ${errorCount} failed`);
    return { successCount, errorCount };
  } catch (error) {
    console.error('❌ Error during categories import:', error);
    return { successCount, errorCount: sampleCategories.length };
  }
};

// Import Tags Collection
const importTagsData = async (db) => {
  console.log('🏷️  Importing tags data...');

  const tagsCollection = db.collection('tags');
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const tag of sampleTags) {
      try {
        const { id, ...tagData } = tag;
        await tagsCollection.doc(id).set(tagData);

        console.log(`✅ Tag imported: "#${tag.name}"`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing tag "${tag.name}":`, error.message);
        errorCount++;
      }
    }

    console.log(`📊 Tags Import: ✅ ${successCount} success, ❌ ${errorCount} failed`);
    return { successCount, errorCount };
  } catch (error) {
    console.error('❌ Error during tags import:', error);
    return { successCount, errorCount: sampleTags.length };
  }
};

// Import Blogs Collection
const importBlogsData = async (db) => {
  console.log('📝 Importing blogs data...');

  const blogsCollection = db.collection('blogs');
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const blog of sampleBlogs) {
      try {
        const { id, ...blogData } = blog;
        await blogsCollection.doc(id).set(blogData);

        console.log(`✅ Blog imported: "${blog.title}"`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing blog "${blog.title}":`, error.message);
        errorCount++;
      }
    }

    console.log(`📊 Blogs Import: ✅ ${successCount} success, ❌ ${errorCount} failed`);
    return { successCount, errorCount };
  } catch (error) {
    console.error('❌ Error during blogs import:', error);
    return { successCount, errorCount: sampleBlogs.length };
  }
};

// Import Comments Collection
const importCommentsData = async (db) => {
  console.log('💬 Importing comments data...');

  const commentsCollection = db.collection('comments');
  let successCount = 0;
  let errorCount = 0;

  try {
    for (const comment of sampleComments) {
      try {
        const { id, ...commentData } = comment;
        await commentsCollection.doc(id).set(commentData);

        console.log(`✅ Comment imported: "${comment.content.substring(0, 50)}..."`);
        successCount++;
      } catch (error) {
        console.error(`❌ Error importing comment:`, error.message);
        errorCount++;
      }
    }

    console.log(`📊 Comments Import: ✅ ${successCount} success, ❌ ${errorCount} failed`);
    return { successCount, errorCount };
  } catch (error) {
    console.error('❌ Error during comments import:', error);
    return { successCount, errorCount: sampleComments.length };
  }
};

// Main Import Function
const importAllData = async () => {
  console.log('🔥 BlogApp Complete Data Import');
  console.log('=====================================\n');

  const db = initializeFirebase();

  try {
    console.log('📋 Import Summary:');
    console.log(`- Users: ${sampleUsers.length} records`);
    console.log(`- Categories: ${sampleCategories.length} records`);
    console.log(`- Tags: ${sampleTags.length} records`);
    console.log(`- Blogs: ${sampleBlogs.length} records`);
    console.log(`- Comments: ${sampleComments.length} records`);
    console.log('\n🚀 Starting import process...\n');

    // Import in order: Users -> Categories -> Tags -> Blogs -> Comments
    const usersResult = await importUsersData(db);
    console.log('');

    const categoriesResult = await importCategoriesData(db);
    console.log('');

    const tagsResult = await importTagsData(db);
    console.log('');

    const blogsResult = await importBlogsData(db);
    console.log('');

    const commentsResult = await importCommentsData(db);
    console.log('');

    // Final Summary
    console.log('🎯 FINAL IMPORT SUMMARY');
    console.log('======================');
    console.log(`👥 Users: ✅ ${usersResult.successCount}/${sampleUsers.length}`);
    console.log(`📁 Categories: ✅ ${categoriesResult.successCount}/${sampleCategories.length}`);
    console.log(`🏷️  Tags: ✅ ${tagsResult.successCount}/${sampleTags.length}`);
    console.log(`📝 Blogs: ✅ ${blogsResult.successCount}/${sampleBlogs.length}`);
    console.log(`💬 Comments: ✅ ${commentsResult.successCount}/${sampleComments.length}`);

    const totalSuccess = usersResult.successCount + categoriesResult.successCount + tagsResult.successCount + blogsResult.successCount + commentsResult.successCount;
    const totalRecords = sampleUsers.length + sampleCategories.length + sampleTags.length + sampleBlogs.length + sampleComments.length;

    console.log(`\n🎉 Total: ${totalSuccess}/${totalRecords} records imported successfully!`);

    if (totalSuccess === totalRecords) {
      console.log('\n✨ All data imported successfully!');
      console.log('🔗 Check your Firebase Console: https://console.firebase.google.com');
      console.log('📱 Your BlogApp is now ready with sample data!');
    }
  } catch (error) {
    console.error('❌ Import process failed:', error);
  }

  process.exit(0);
};

// Run import if this file is executed directly
if (require.main === module) {
  importAllData();
}

module.exports = {
  importAllData,
  importUsersData,
  importCategoriesData,
  importTagsData,
  importBlogsData,
  importCommentsData,
};
