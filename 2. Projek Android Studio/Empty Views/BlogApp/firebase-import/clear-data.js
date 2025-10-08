const { initializeFirebase } = require('./firebase-config');

// Clear all collections
const clearAllData = async () => {
  console.log('🗑️  BlogApp Data Cleaner');
  console.log('========================\n');

  const db = initializeFirebase();

  const collections = ['users', 'categories', 'tags', 'blogs', 'comments'];

  try {
    for (const collectionName of collections) {
      console.log(`🧹 Clearing collection: ${collectionName}`);

      const snapshot = await db.collection(collectionName).get();

      if (snapshot.empty) {
        console.log(`   ℹ️  Collection ${collectionName} is already empty`);
        continue;
      }

      const batch = db.batch();
      snapshot.docs.forEach((doc) => {
        batch.delete(doc.ref);
      });

      await batch.commit();
      console.log(`   ✅ Cleared ${snapshot.size} documents from ${collectionName}`);
    }

    console.log('\n🎯 All collections cleared successfully!');
    console.log('Ready for fresh import.');
  } catch (error) {
    console.error('❌ Error clearing data:', error);
  }

  process.exit(0);
};

// Clear specific collection
const clearCollection = async (collectionName) => {
  console.log(`🗑️  Clearing collection: ${collectionName}`);

  const db = initializeFirebase();

  try {
    const snapshot = await db.collection(collectionName).get();

    if (snapshot.empty) {
      console.log(`Collection ${collectionName} is already empty`);
      return;
    }

    const batch = db.batch();
    snapshot.docs.forEach((doc) => {
      batch.delete(doc.ref);
    });

    await batch.commit();
    console.log(`✅ Cleared ${snapshot.size} documents from ${collectionName}`);
  } catch (error) {
    console.error(`❌ Error clearing ${collectionName}:`, error);
  }
};

// Run based on command line argument
const main = async () => {
  const args = process.argv.slice(2);

  if (args.length === 0) {
    await clearAllData();
  } else {
    const collectionName = args[0];
    await clearCollection(collectionName);
    process.exit(0);
  }
};

if (require.main === module) {
  main();
}

module.exports = { clearAllData, clearCollection };
