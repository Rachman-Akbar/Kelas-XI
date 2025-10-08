const admin = require('firebase-admin');
const { preloadedImages } = require('../data/sample-data');
const FirebaseConfig = require('../config/firebase');

// Initialize Firebase using shared config
const firebaseConfig = new FirebaseConfig();
const db = firebaseConfig.getFirestore();

async function importPreloadedImages() {
  console.log('🖼️  Starting to import preloaded images...');

  try {
    const batch = db.batch();

    // Import story images
    console.log('📱 Importing story images...');
    for (const image of preloadedImages.stories) {
      const docRef = db.collection('preloaded_images').doc(`story_${image.id}`);
      batch.set(docRef, {
        id: image.id,
        url: image.url,
        category: image.category,
        title: image.title,
        description: image.description,
        type: 'story',
        aspectRatio: '9:16',
        tags: [image.category, 'story', 'vertical'],
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        isActive: true,
      });
    }

    // Import post images
    console.log('📸 Importing post images...');
    for (const image of preloadedImages.posts) {
      const docRef = db.collection('preloaded_images').doc(`post_${image.id}`);
      batch.set(docRef, {
        id: image.id,
        url: image.url,
        category: image.category,
        title: image.title,
        description: image.description,
        type: 'post',
        aspectRatio: '1:1',
        tags: [image.category, 'post', 'square'],
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        isActive: true,
      });
    }

    // Import background colors
    console.log('🎨 Importing background colors...');
    for (const bg of preloadedImages.backgrounds) {
      const docRef = db.collection('preloaded_images').doc(`bg_${bg.id}`);
      batch.set(docRef, {
        id: bg.id,
        color: bg.color,
        name: bg.name,
        type: 'background',
        category: 'color',
        tags: ['background', 'color', 'gradient'],
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        isActive: true,
      });
    }

    // Commit batch
    await batch.commit();

    console.log('✅ Successfully imported preloaded images:');
    console.log(`   📱 ${preloadedImages.stories.length} story images`);
    console.log(`   📸 ${preloadedImages.posts.length} post images`);
    console.log(`   🎨 ${preloadedImages.backgrounds.length} background colors`);

    // Create image categories collection
    await createImageCategories();
  } catch (error) {
    console.error('❌ Error importing preloaded images:', error);
    throw error;
  }
}

async function createImageCategories() {
  console.log('📂 Creating image categories...');

  const categories = [
    { id: 'nature', name: 'Nature', icon: '🌿', description: 'Landscapes, sunsets, forests' },
    { id: 'food', name: 'Food', icon: '🍽️', description: 'Delicious meals and drinks' },
    { id: 'fitness', name: 'Fitness', icon: '💪', description: 'Workout and healthy lifestyle' },
    { id: 'city', name: 'City', icon: '🏙️', description: 'Urban life and architecture' },
    { id: 'lifestyle', name: 'Lifestyle', icon: '☕', description: 'Daily life moments' },
    { id: 'travel', name: 'Travel', icon: '✈️', description: 'Adventures and destinations' },
    { id: 'art', name: 'Art', icon: '🎨', description: 'Creative and artistic content' },
    { id: 'music', name: 'Music', icon: '🎵', description: 'Musical instruments and studios' },
    { id: 'fashion', name: 'Fashion', icon: '👗', description: 'Style and fashion inspiration' },
    { id: 'books', name: 'Books', icon: '📚', description: 'Reading and literature' },
    { id: 'pets', name: 'Pets', icon: '🐕', description: 'Cute animals and pets' },
    { id: 'landscape', name: 'Landscape', icon: '🏔️', description: 'Beautiful scenery' },
  ];

  const batch = db.batch();

  for (const category of categories) {
    const docRef = db.collection('image_categories').doc(category.id);
    batch.set(docRef, {
      ...category,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      isActive: true,
    });
  }

  await batch.commit();
  console.log(`✅ Created ${categories.length} image categories`);
}

async function createImageTemplates() {
  console.log('📋 Creating story templates...');

  const templates = [
    {
      id: 'quote_template_1',
      name: 'Inspirational Quote',
      type: 'text',
      backgroundColor: '#FF6B6B',
      textColor: '#FFFFFF',
      fontSize: 24,
      fontWeight: 'bold',
      textAlign: 'center',
      template: 'Share your favorite quote here...',
      category: 'quotes',
    },
    {
      id: 'announcement_template_1',
      name: 'Big Announcement',
      type: 'text',
      backgroundColor: '#4ECDC4',
      textColor: '#FFFFFF',
      fontSize: 20,
      fontWeight: 'normal',
      textAlign: 'center',
      template: 'Big news! 📢',
      category: 'announcement',
    },
    {
      id: 'mood_template_1',
      name: 'Daily Mood',
      type: 'text',
      backgroundColor: '#45B7D1',
      textColor: '#FFFFFF',
      fontSize: 22,
      fontWeight: 'normal',
      textAlign: 'center',
      template: "Today I'm feeling... 😊",
      category: 'mood',
    },
    {
      id: 'question_template_1',
      name: 'Ask Question',
      type: 'text',
      backgroundColor: '#96CEB4',
      textColor: '#FFFFFF',
      fontSize: 20,
      fontWeight: 'normal',
      textAlign: 'center',
      template: 'What do you think? 🤔',
      category: 'question',
    },
  ];

  const batch = db.batch();

  for (const template of templates) {
    const docRef = db.collection('story_templates').doc(template.id);
    batch.set(docRef, {
      ...template,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      isActive: true,
    });
  }

  await batch.commit();
  console.log(`✅ Created ${templates.length} story templates`);
}

async function main() {
  try {
    await importPreloadedImages();
    await createImageTemplates();
    console.log('🎉 All preloaded content imported successfully!');
    process.exit(0);
  } catch (error) {
    console.error('💥 Import failed:', error);
    process.exit(1);
  }
}

// Run if called directly
if (require.main === module) {
  main();
}

module.exports = { importPreloadedImages, createImageCategories, createImageTemplates };
