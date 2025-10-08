const PostImporter = require('./import-posts');
const StoryImporter = require('./import-stories');
const { importPreloadedImages } = require('./scripts/import-preloaded-images');

class FirestoreDataImporter {
  constructor() {
    this.postImporter = new PostImporter();
    this.storyImporter = new StoryImporter();
  }

  async importFirestoreData() {
    console.log('🚀 Starting Firestore data import (without Auth)...\n');

    try {
      // Step 1: Import Preloaded Images first
      console.log('==========================================');
      console.log('STEP 1: IMPORTING PRELOADED IMAGES');
      console.log('==========================================');
      await importPreloadedImages();
      console.log(`✅ Preloaded images imported\n`);

      // Delay before next step
      await this.delay(2000);

      // Step 2: Import Posts (with mock users)
      console.log('==========================================');
      console.log('STEP 2: IMPORTING POSTS');
      console.log('==========================================');
      const posts = await this.postImporter.importPostsWithoutUsers();
      console.log(`✅ Posts imported: ${posts.length}\n`);

      // Delay before next step
      await this.delay(2000);

      // Step 3: Import Stories (with mock users)
      console.log('==========================================');
      console.log('STEP 3: IMPORTING STORIES');
      console.log('==========================================');
      const stories = await this.storyImporter.importStoriesWithoutUsers();
      console.log(`✅ Stories imported: ${stories.length}\n`);

      // Final summary
      console.log('==========================================');
      console.log('🎉 FIRESTORE IMPORT COMPLETED SUCCESSFULLY!');
      console.log('==========================================');
      console.log(`📊 SUMMARY:`);
      console.log(`   🖼️  Preloaded Images: Available`);
      console.log(`   📸 Posts: ${posts.length}`);
      console.log(`   📱 Stories: ${stories.length}`);
      console.log(`   🗂️  Total records: ${posts.length + stories.length}`);
      console.log('==========================================');

      // Create summary file
      const summary = {
        importDate: new Date().toISOString(),
        preloadedImages: 'Imported',
        posts: posts.length,
        stories: stories.length,
        totalRecords: posts.length + stories.length,
        note: 'User authentication import skipped due to API permissions',
      };

      const fs = require('fs');
      fs.writeFileSync('firestore-import-summary.json', JSON.stringify(summary, null, 2));
      console.log('📋 Import summary saved to: firestore-import-summary.json');
    } catch (error) {
      console.error('❌ Error during Firestore import:', error);
      throw error;
    }
  }

  delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Create and run importer
const importer = new FirestoreDataImporter();
importer
  .importFirestoreData()
  .then(() => {
    console.log('\n✨ All Firestore data imported successfully!');
    process.exit(0);
  })
  .catch((error) => {
    console.error('\n💥 Import failed:', error);
    process.exit(1);
  });
