const { initializeFirebase } = require('./firebase-config');

// Sample categories data
const sampleCategories = [
  {
    name: 'Electronics',
    description: 'Gadget dan perangkat elektronik terbaru',
    imageUrl: 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    name: 'Fashion',
    description: 'Pakaian dan aksesoris fashion terkini',
    imageUrl: 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    name: 'Food & Beverage',
    description: 'Makanan dan minuman berkualitas',
    imageUrl: 'https://images.unsplash.com/photo-1567306301408-9b74779a11af?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    name: 'Beauty',
    description: 'Produk kecantikan dan perawatan',
    imageUrl: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    name: 'Home & Living',
    description: 'Perabotan dan dekorasi rumah',
    imageUrl: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
  {
    name: 'Sports',
    description: 'Peralatan dan aksesoris olahraga',
    imageUrl: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500',
    productCount: 0,
    isActive: true,
    createdAt: Date.now(),
  },
];

async function importCategories() {
  console.log('🔄 Starting category import process...\n');

  try {
    const { db } = initializeFirebase();

    console.log(`📂 Importing ${sampleCategories.length} categories...`);

    const batch = db.batch();

    sampleCategories.forEach((category, index) => {
      const docRef = db.collection('categories').doc();
      batch.set(docRef, category);
      console.log(`➕ Added category ${index + 1}: ${category.name}`);
    });

    // Commit the batch
    await batch.commit();

    console.log('\n✅ Category import completed successfully!');
    console.log(`📊 Total categories imported: ${sampleCategories.length}`);

    // Verify import
    const categoriesSnapshot = await db.collection('categories').get();
    console.log(`🔍 Verification: ${categoriesSnapshot.size} categories in database`);
  } catch (error) {
    console.error('❌ Error importing categories:', error.message);
    process.exit(1);
  }
}

// Run import if this file is executed directly
if (require.main === module) {
  importCategories();
}

module.exports = { importCategories, sampleCategories };
