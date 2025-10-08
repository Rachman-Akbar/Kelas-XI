const UserImporter = require('./import-users');
const PostImporter = require('./import-posts');
const StoryImporter = require('./import-stories');
const { importPreloadedImages } = require('./scripts/import-preloaded-images');

class AllDataImporter {
  constructor() {
    this.userImporter = new UserImporter();
    this.postImporter = new PostImporter();
    this.storyImporter = new StoryImporter();
  }

  async importAll() {
    console.log('🚀 Starting complete data import...\n');

    try {
      // Step 1: Import Users
      console.log('==========================================');
      console.log('STEP 1: IMPORTING USERS');
      console.log('==========================================');
      const users = await this.userImporter.importUsers();
      console.log(`✅ Users imported: ${users.length}\n`);

      // Delay before next step
      await this.delay(2000);

      // Step 2: Import Posts
      console.log('==========================================');
      console.log('STEP 2: IMPORTING POSTS');
      console.log('==========================================');
      const posts = await this.postImporter.importPosts();
      console.log(`✅ Posts imported: ${posts.length}\n`);

      // Delay before next step
      await this.delay(2000);

      // Step 3: Import Stories
      console.log('==========================================');
      console.log('STEP 3: IMPORTING STORIES');
      console.log('==========================================');
      const stories = await this.storyImporter.importStories();
      console.log(`✅ Stories imported: ${stories.length}\n`);

      // Delay before next step
      await this.delay(2000);

      // Step 4: Import Preloaded Images
      console.log('==========================================');
      console.log('STEP 4: IMPORTING PRELOADED IMAGES');
      console.log('==========================================');
      await importPreloadedImages();
      console.log(`✅ Preloaded images imported\n`);

      // Final summary
      console.log('==========================================');
      console.log('🎉 IMPORT COMPLETED SUCCESSFULLY!');
      console.log('==========================================');
      console.log(`📊 SUMMARY:`);
      console.log(`   👥 Users: ${users.length}`);
      console.log(`   📸 Posts: ${posts.length}`);
      console.log(`   📱 Stories: ${stories.length}`);
      console.log(`   🖼️  Preloaded Images: Available`);
      console.log(`   🗂️  Total records: ${users.length + posts.length + stories.length}`);
      console.log('==========================================');

      // Create summary file
      const summary = {
        importDate: new Date().toISOString(),
        users: users.length,
        posts: posts.length,
        stories: stories.length,
        total: users.length + posts.length + stories.length,
        userDetails: users.map((u) => ({ username: u.username, uid: u.uid })),
        postDetails: posts.map((p) => ({ postId: p.postId, username: p.username })),
        storyDetails: stories.map((s) => ({ storyId: s.storyId, username: s.username })),
      };

      const fs = require('fs');
      fs.writeFileSync('./import-summary.json', JSON.stringify(summary, null, 2));
      console.log(`💾 Import summary saved to: import-summary.json`);

      return summary;
    } catch (error) {
      console.error('❌ Error during complete import:', error);
      throw error;
    }
  }

  async delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// Run import if called directly
if (require.main === module) {
  const importer = new AllDataImporter();
  importer
    .importAll()
    .then(() => {
      console.log('\n🏁 All imports completed successfully!');
      console.log('Your SocialMedia app database is now ready to use! 🎊');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Import failed:', error);
      process.exit(1);
    });
}

module.exports = AllDataImporter;
